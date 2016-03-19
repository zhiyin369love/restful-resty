package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.UploadedFile;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.route.core.multipart.FILE;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.bean.goods.GoodsInfo;
import com.qianmo.eshop.bean.goods.GoodsSku;
import com.qianmo.eshop.common.*;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品信息
 * Created by fxg06 on 2016/3/1.
 */
@API("/goods")
public class GoodsResource extends SellerResource {
    //获取用户最高权限ID
    private Long seller_id = SessionUtil.getAdminId();

    /**
     * 获取商品列表
     *
     * @param goods_name      商品名称
     * @param goods_status    商品上下架状态
     * @param category_id
     * @param sub_category_id
     * @param page_start
     * @param page_step
     * @return
     */
    @GET
    public HashMap list(String goods_name, Integer goods_status, Integer category_id,
                        Integer sub_category_id, Integer page_start, Integer page_step) {
        String countSql = "SELECT distinct a.id FROM goods_info a " +
                "INNER JOIN goods_sku b ON a.num = b.goods_num " +
                "WHERE a.seller_id = ? AND a.deleted_at IS NULL AND b.deleted_at IS NULL ";
        HashMap resultMap = new HashMap();
        if (category_id == null) {
            return resultMap;
        }
        /*
        判断是否有分页信息，如果没有，给定默认值
         */
        if (page_start == null) {
            page_start = ConstantsUtils.DEFAULT_PAGE_START;//默认从第1条开始
        }
        if (page_step == null) {
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;//默认返回10条
        }
        String sql = YamlRead.getSQL("findGoodsInfo", "seller/goods");
        /*
        判断是根据一级分类查商品还是二级分类查商品
         */
        if (sub_category_id != null && sub_category_id > 0) {
            sql = sql + " AND a.category_id=" + sub_category_id;
            countSql = countSql + " AND a.category_id=" + sub_category_id;
        } else {
            sql = sql + " AND a.category_id in (SELECT id from goods_category where pid=" + category_id + ")";
            countSql = countSql + " AND a.category_id in (SELECT id from goods_category where pid=" + category_id + ")";
        }
        /*
        判断是否根据商品上下架状态查商品
         */
        if (goods_status != null) {
            sql = sql + " AND b.status=" + goods_status;
            countSql = countSql + " AND b.status="+goods_status;
        }
        /*
        判断是否根据商品名称模糊搜索
         */
        if (goods_name != null && !"".equals(goods_name)) {
            sql = sql + " AND a.name like '%" + goods_name + "%'";
            countSql = countSql + " AND a.name like '%" + goods_name + "%'";
        }
        HashMap<Long, GoodsInfo> map = new HashMap<Long, GoodsInfo>();
        FullPage<goods_info> list = goods_info.dao.fullPaginate(page_start / page_step + 1,
                page_step, sql, seller_id);
        //查询结果非空判断
        if (list != null && list.getList().size() > 0) {
            for (goods_info goodsInfo : list.getList()) {
                GoodsInfo goods = map.get(goodsInfo.<Long>get("goods_id")); //
                if (goods == null) {
                    goods = new GoodsInfo();
                    goods.setGoods_id(goodsInfo.<Long>get("goods_id"));
                    goods.setGoods_name(goodsInfo.get("goods_name").toString());
                    goods.setGoods_num(goodsInfo.<Long>get("goods_num"));
                    //判断是否有主图
                    if (goodsInfo.get("main_pic_url") != null) {
                        goods.setMain_pic_url(goodsInfo.get("main_pic_url").toString());
                    }
                    goods.setProducer(goodsInfo.get("producer").toString());
                    //商品规格信息
                    List<GoodsSku> skuList = new ArrayList<GoodsSku>();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(goodsInfo.<Long>get("sku_id"));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(goodsInfo.<Integer>get("status"));
                    //判断是否有价格信息
                    if (goodsInfo.get("list_price") != null) {
                        goodsSku.setPrice(goodsInfo.<BigDecimal>get("list_price"));
                    }
                    //判断是否有上架时间信息
                    if (goodsInfo.get("release_date") != null
                            && goodsInfo.<Integer>get("status")==1) {
                        goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    }
                    goodsSku.setSeller_count(0);
                    //判断该商品规格是否有售出
                    if (goodsInfo.get("sell_count") != null) {
                        goodsSku.setSeller_count(goodsInfo.<Integer>get("sell_count"));
                    }
                    skuList.add(goodsSku);
                    goods.setGoods_sku_list(skuList);
                } else {
                    List<GoodsSku> skuList = (List) goods.getGoods_sku_list();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(goodsInfo.<Long>get("sku_id"));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(goodsInfo.<Integer>get("status"));
                    //判断是否有价格信息
                    if (goodsInfo.get("list_price") != null) {
                        goodsSku.setPrice(goodsInfo.<BigDecimal>get("list_price"));
                    }
                    //判断是否有上架时间信息
                    if (goodsInfo.get("release_date") != null
                            && goodsInfo.<Integer>get("status")==1) {
                        goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    }
                    goodsSku.setSeller_count(0);
                    //判断该商品规格是否有售出
                    if (goodsInfo.get("sell_count") != null) {
                        goodsSku.setSeller_count(goodsInfo.<Integer>get("sell_count"));
                    }
                    skuList.add(goodsSku);
                    goods.setGoods_sku_list(skuList);
                }
                map.put(goods.getGoods_id(), goods);
            }
        }
        List<GoodsInfo> goodsInfoList = new ArrayList<GoodsInfo>();
        //非空判断
        if (map != null && map.size() > 0) {
            for (Long goodsId : map.keySet()) {
                goodsInfoList.add(map.get(goodsId));
            }
        }

        List countList = goods_info.dao.find(countSql,seller_id);
        resultMap.put("goods_list", goodsInfoList);
        if(countList!=null){
            resultMap.put("total_count", countList.size());
        } else{
            resultMap.put("total_count", 0);
        }

        return resultMap;
    }

    /**
     * 获取商品详情
     *
     * @param id 商品ID
     * @return
     */
    @GET("/:id")
    public HashMap get(Long id) {
        HashMap resultMap = new HashMap();
        if (id == null) {
            return resultMap;
        }
        goods_info goodsInfo = goods_info.dao.findFirst(YamlRead.getSQL("findGoods", "seller/goods"), id);

        //判断当前登录用户是否有查看该商品的权限
        if (seller_id == goodsInfo.<Long>get("seller_id")) {
            resultMap.put("goods_info", goodsInfo);
            List<goods_sku> list = goods_sku.dao.find(YamlRead.getSQL("findGoodsSku", "seller/goods"),
                    goodsInfo.get("goods_num"));
            resultMap.put("goods_sku_list", list);
        }
        return resultMap;
    }

    /**
     * 添加商品信息
     *
     * @param goods 商品信息
     * @return
     */
    @POST
    @Transaction
    public HashMap add(goods_info goods) {
        //添加商品基本信息
        goods_info goodsInfo = goods.get("goods_info", goods_info.class);
        //生成商品编号
        String goodsNum = CodeUtils.code(goodsInfo.get("category_id").toString(), ConstantsUtils.GOODS_NUM_TYPE);
        goodsInfo.set("area_id", ConstantsUtils.ALL_AREA_ID);
        goodsInfo.set("num", goodsNum);
        goodsInfo.set("category_id", goodsInfo.get("category_id"));
        goodsInfo.set("name", goodsInfo.get("goods_name"));
        goodsInfo.set("seller_id", seller_id);
        goodsInfo.set("status", ConstantsUtils.RELEASE_STATUS_OFF);//商品状态(0 未上架 1 已上架)
        goodsInfo.save();
        /*
        添加商品规格信息
         */
        List<JSONObject> list = goods.get("goods_sku_list");
        List<goods_sku> skuList = new ArrayList<goods_sku>();
        //非空判断
        if (list != null && list.size() > 0) {
            for (JSONObject obj : list) {
                goods_sku goodsSku = new goods_sku();
                goodsSku.set("status", ConstantsUtils.RELEASE_STATUS_OFF);//商品规格状态(0 未上架 1 已上架)
                goodsSku.set("area_id", ConstantsUtils.ALL_AREA_ID);
                goodsSku.set("goods_num", goodsNum);
                goodsSku.set("amount", obj.get("sku_amount"));
                goodsSku.set("name", obj.get("sku_name"));
                goodsSku.set("unit_id", obj.get("sku_unit_id"));
                goodsSku.set("seller_id", seller_id);
                skuList.add(goodsSku);
            }
        }
        goods_sku.dao.save(skuList);
        return CommonUtils.getCodeMessage(true, "添加商品成功");
    }

    /**
     * 删除商品规格
     * @param id
     * @return
     */
    @DELETE("/sku/:id")
    @Transaction
    public HashMap delete(Integer id){
        goods_sku goodsSku = goods_sku.dao.findFirstBy("id=? AND seller_id=?",id,seller_id);
        //非空判断
        if(goodsSku!=null){
            //删除商品规格
            goodsSku.set("deleted_at",new Date());
            goodsSku.update();
            //删除商品规格价格
            goods_sku_price.dao.deleteBy("sku_id=?",id);
            return CommonUtils.getCodeMessage(true,"删除商品规格成功");
        }else{
            return CommonUtils.getCodeMessage(false,"删除商品规格失败");
        }
    }

    /**
     * 编辑商品
     * @param goods      商品信息
     * @return
     */
    @PUT
    @Transaction
    public HashMap edit(goods_info goods){
        /*
        修改商品基本信息
        */
        goods_info info = goods.get("goods_info", goods_info.class);
        goods_info goodsInfo = goods_info.dao.findById(info.get("goods_id"));
        if(seller_id.equals(goodsInfo.get("seller_id"))){
            info.set("id", info.get("goods_id"));
            info.set("name", info.get("goods_name"));
            info.update();
            /*
            添加商品规格信息
            */
            List<JSONObject> list = goods.get("goods_sku_list");
//            List<goods_sku> skuList = new ArrayList<goods_sku>();
            //非空判断
            if (list != null && list.size() > 0) {
                for (JSONObject obj : list) {
                    goods_sku goodsSku = new goods_sku();
                    if(obj.get("sku_id")==null){
                        goodsSku.set("status", ConstantsUtils.RELEASE_STATUS_OFF);//商品规格状态(0 未上架 1 已上架)
                        goodsSku.set("area_id", ConstantsUtils.ALL_AREA_ID);
                        goodsSku.set("goods_num", info.<Long>get("goods_num"));
                        goodsSku.set("amount", obj.get("sku_amount"));
                        goodsSku.set("name", obj.get("sku_name"));
                        goodsSku.set("unit_id", obj.get("sku_unit_id"));
                        goodsSku.set("seller_id", seller_id);
                        goodsSku.save();
                    }
//                    else {
//                        goodsSku.set("id",obj.get("sku_id"));
//                        goodsSku.set("amount", obj.get("sku_amount"));
//                        goodsSku.set("name", obj.get("sku_name"));
//                        goodsSku.set("unit_id", obj.get("sku_unit_id"));
//                        goodsSku.update();
//                    }
//                    skuList.add(goodsSku);
                }
            }
//            goods_sku.dao.save(skuList);
            return CommonUtils.getCodeMessage(true, "修改商品成功");
        } else{
            return CommonUtils.getCodeMessage(false, "修改商品失败");
        }
    }

//    /**
//     * 编辑商品
//     *
//     * @param goods          商品信息
//     * @param id             商品id
//     * @param delete_pic_url 删除的图片地址
//     * @return
//     */
//    @PUT("/:id")
//    @Transaction
//    public HashMap edit(goods_info goods, Long id, String delete_pic_url) {
//        /*
//        修改商品基本信息
//        */
//        goods_info info = goods.get("goods_info", goods_info.class);
//        goods_info goodsInfo = goods_info.dao.findById(id);
//        if (goodsInfo.<Long>get("seller_id") == seller_id) {
//            goodsInfo.set("name", info.get("goods_name"));
//            goodsInfo.update();
//            /*
//            删除商品图片
//            */
//            if (delete_pic_url != null) {
//                String[] deletePic = delete_pic_url.split(",");
//                for (String delete_pic : deletePic) {
//                    if (delete_pic.indexOf(ConstantsUtils.PIC_DIR) != -1) {
//                        deleteMainPic(delete_pic.substring(delete_pic.indexOf(ConstantsUtils.PIC_DIR)));
//                    }
//                }
//            }
//            /*
//            对商品规格信息的操作
//            */
//            List<JSONObject> list = goods.get("goods_sku_list");
//            if (list != null && list.size() > 0) {
//                for (JSONObject obj : list) {
//                    //status为1表示新增商品规格
//                    if (Integer.parseInt(obj.get("status").toString()) == 1) {
//                        goods_sku goodsSku = new goods_sku();
//                        goodsSku.set("status", ConstantsUtils.RELEASE_STATUS_OFF);//商品规格状态(0 未上架 1 已上架)
//                        goodsSku.set("area_id", ConstantsUtils.ALL_AREA_ID);
//                        goodsSku.set("goods_num", goodsInfo.get("num"));
//                        goodsSku.set("amount", obj.get("sku_amount"));
//                        goodsSku.set("name", obj.get("sku_name"));
//                        goodsSku.set("unit_id", obj.get("sku_unit_id"));
//                        goodsSku.set("seller_id", seller_id);
//                        goodsSku.save();
//                    } else {
//                        goods_sku goodsSku = new goods_sku();
//                        goodsSku.set("id", obj.get("sku_id"));
//                        switch (Integer.parseInt(obj.get("status").toString())) {
//                            case 2: //status为2表示修改商品规格
//                                goodsSku.set("amount", obj.get("sku_amount"));
//                                goodsSku.set("name", obj.get("sku_name"));
//                                goodsSku.set("unit_id", obj.get("sku_unit_id"));
//                                break;
//                            case 3: //status为3表示删除商品规格
//                                goodsSku.set("deleted_at", new Date());
//                                break;
//                        }
//                        goodsSku.update();
//                    }
//                }
//            }
//            return CommonUtils.getCodeMessage(true, "修改商品成功");
//        } else {
//            return CommonUtils.getCodeMessage(false, "修改商品失败");
//        }
//
//    }

    /**
     * 批量删除商品
     * @param goods_list    商品列表
     * @return
     */
    @POST("/delete")
    @Transaction
    public HashMap delete(List<JSONObject> goods_list) {
        //非空判断
        if (goods_list != null && goods_list.size() > 0) {
            for (JSONObject obj : goods_list) {
                if (obj.get("sku_id") != null) {//商品规格ID不为空时，只删除商品规格信息
                    goods_sku.dao.updateColsBy("deleted_at", "id = ? AND seller_id = ?",
                            new Date(), obj.get("sku_id"), seller_id);
                    //删除商品规格价格
                    goods_sku_price.dao.deleteBy("sku_id = ? AND seller_id = ?",
                            obj.get("sku_id"), seller_id);
                } else { //商品规格ID为空时，删除商品及规格信息
                    //删除商品
                    goods_info.dao.updateColsBy("deleted_at", "num = ? AND seller_id = ?",
                            new Date(), obj.get("goods_num"), seller_id);
                    //删除商品规格
                    goods_sku.dao.updateColsBy("deleted_at","goods_num=? AND seller_id = ?",
                            new Date(), obj.get("goods_num"), seller_id);
                    //删除商品价格
                    goods_sku_price.dao.deleteBy("goods_num=?  AND seller_id = ?",
                            obj.get("goods_num"),seller_id);
                }
            }
            return CommonUtils.getCodeMessage(true, "删除商品成功");
        }else{
            return CommonUtils.getCodeMessage(false, "删除商品失败");
        }
    }

    /**
     * 删除商品或商品规格
     *
     * @param id           商品id
     * @param goods_sku_id 商品规格id
     * @return
     */
    @DELETE("/:id")
    @Transaction
    public HashMap delete(Long id, Long goods_sku_id) {
        //获取商品基本信息
        goods_info goods = goods_info.dao.findById(id);
        //判断该商品是否属于该用户
        if (seller_id == goods.<Long>get("seller_id")) {
            if (goods_sku_id != null) {  //商品规格ID不为空时，只删除商品规格信息
                //删除商品规格
                goods_sku sku = new goods_sku();
                sku.set("id", goods_sku_id);
                sku.set("deleted_at", new Date());
                sku.update();
                //删除商品规格价格
                goods_sku_price.dao.deleteBy("sku_id=?", goods_sku_id);
            } else { //商品规格ID为空时，删除商品及规格信息
                //删除商品
                goods.set("deleted_at", new Date());
                goods.update();
                //删除商品规格
                String delSql = YamlRead.getSQL("deleteGoodsSku", "seller/goods");
                goods_sku.dao.update(delSql, new Date(), goods.get("num"));
                //删除商品规格价格
                goods_sku_price.dao.deleteBy("goods_num=?", goods.get("num"));

                /*
                删除商品主图
                 */
                String mainPicUrl = goods.get("main_pic_url");
                //判断图片地址是否正确
                if (mainPicUrl != null && !"".equals(mainPicUrl)
                        && mainPicUrl.indexOf(ConstantsUtils.PIC_DIR) != -1) {
                    deleteMainPic(mainPicUrl.substring(mainPicUrl.indexOf(ConstantsUtils.PIC_DIR)));
                }
                /*
                删除商品详细图片
                 */
                String[] picUrl = goods.get("pic_url_list").toString().split(",");
                //非空判断
                if (picUrl != null && picUrl.length > 0) {
                    for (String pic : picUrl) {
                        //判断图片地址是否正确
                        if (pic.indexOf(ConstantsUtils.PIC_DIR) != -1) {
                            deleteMainPic(pic.substring(pic.indexOf(ConstantsUtils.PIC_DIR)));
                        }
                    }
                }
            }
            return CommonUtils.getCodeMessage(true, "删除商品成功");
        } else {
            return CommonUtils.getCodeMessage(false, "删除商品失败");
        }
    }

    /**
     * 删除图片
     *
     * @param path 图片路径
     */
    public void deleteMainPic(String path) {
        try {
            String picUrl = this.getRequest().getRealPath(path);
            File file = new File(picUrl);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 上传商品主图
     *
     * @param main_pic 商品主图
     * @return 图片名称
     */
    @POST("/upload/main")
    @FILE(dir = ConstantsUtils.GOODS_MAIN_PIC, overwrite = true, allows = {"image/png", "image/jpg", "image/gif", "image/bmp","image/jpeg"})
    public HashMap mainPic(UploadedFile main_pic) {
        String mainPicUrl = this.getRequest().getBaseUri() + ConstantsUtils.GOODS_MAIN_PIC + main_pic.getFileName();
        HashMap resultMap = new HashMap();
        resultMap.put("main_pic_url",mainPicUrl);
        return resultMap;
    }

    /**
     * 上传商品详情图片
     *
     * @param picMap 商品详情图片
     * @return 图片名称
     */
    @POST("/upload/detail")
    @FILE(dir = ConstantsUtils.GOODS_DETAIL_PIC, overwrite = true, allows = {"image/png", "image/jpg", "image/gif", "image/bmp","image/jpeg"})
    public HashMap detailPic(Map<String, UploadedFile> picMap) {
        String baseUri = this.getRequest().getBaseUri() + ConstantsUtils.GOODS_DETAIL_PIC;
        String fileUrl = "";
        if (picMap != null && picMap.size() > 0) {
            for (String key : picMap.keySet()) {
                UploadedFile file = picMap.get(key);
                if ("".equals(fileUrl)) {
                    fileUrl = baseUri + file.getFileName();
                } else {
                    fileUrl = fileUrl + "," + baseUri + file.getFileName();
                }
            }
        }
        HashMap resultMap = new HashMap();
        resultMap.put("detail_pic_url",fileUrl);
        return resultMap;
    }

    /**
     * 商品上下架
     *
     * @param status         上下架状态 1：上架 0：下架
     * @param goods_sku_list 商品规格列表
     * @return
     */
    @PUT("/updown")
    public HashMap updown(Integer status, List<JSONObject> goods_sku_list) {
        for (JSONObject obj : goods_sku_list) {
            /*
            当商品规格id不为空时，表示只修改单个商品规格的上下架信息
            否则表示修改一个或多个商品的商品规格上下架信息
             */
            if (obj.get("sku_id") != null) {
                goods_sku.dao.updateColsBy("status,release_date", "id=? AND seller_id=? AND deleted_at is null",
                        status, new Date(), obj.get("sku_id"), seller_id);
            } else {
                goods_sku.dao.updateColsBy("status,release_date", "goods_num=? AND seller_id=? AND deleted_at is null",
                        status, new Date(), obj.get("goods_num"), seller_id);
            }
        }
        if (status == ConstantsUtils.RELEASE_STATUS_ON) {
            return CommonUtils.getCodeMessage(true, "商品上架成功");
        } else {
            return CommonUtils.getCodeMessage(true, "商品下架成功");
        }
    }

}
