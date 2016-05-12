package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.UploadedFile;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.route.core.multipart.FILE;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.bean.goods.GoodsInfo;
import com.qianmo.eshop.common.*;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;

import java.io.File;
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
        HashMap resultMap = new HashMap();
        //获取商品id
        FullPage<goods_info> idList = goods_info.dao.getGoodsIdList(goods_name,goods_status,category_id,sub_category_id,page_start,page_step,seller_id);
        String goodsIds = "";
        //非空判断
        if (idList != null && idList.getList().size() > 0) {
            for (goods_info goods : idList.getList()) {
                if ("".equals(goodsIds)) {
                    goodsIds = goods.get("id").toString();
                } else {
                    goodsIds = goodsIds + "," + goods.get("id");
                }
            }
        }
        //获取商品信息
        List<GoodsInfo> goodsInfoList = goods_info.dao.goodsList(goodsIds,seller_id);
        resultMap.put("goods_list", goodsInfoList);
        if (idList != null) {
            resultMap.put("total_count", idList.getTotalRow());
        } else {
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
        if (goodsInfo != null && seller_id.equals(goodsInfo.<Long>get("seller_id"))) {
            resultMap.put("goods_info", goodsInfo);
            List<goods_sku> list = goods_sku.dao.find(YamlRead.getSQL("findGoodsSku", "seller/goods"),goodsInfo.get("goods_num"));
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
        //判断是否填写商品规格,没有填写,不允许添加商品
        List<JSONObject> list = goods.get("goods_sku_list");
        if (CommonUtils.isEmpty(list)) {
            return CommonUtils.getCodeMessage(false, "请填写商品规格");
        }
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
     *
     * @param id
     * @return
     */
    @DELETE("/sku/:id")
    @Transaction
    public HashMap delete(Integer id) {
        goods_sku goodsSku = goods_sku.dao.findFirstBy("id=? AND seller_id=?", id, seller_id);
        //非空判断
        if (goodsSku != null) {
            //删除商品规格
            goodsSku.set("deleted_at", new Date());
            goodsSku.set("status",0);
            goodsSku.update();
            //删除商品规格价格
            goods_sku_price.dao.deleteBy("sku_id=?", id);
            return CommonUtils.getCodeMessage(true, "删除商品规格成功");
        } else {
            return CommonUtils.getCodeMessage(false, "删除商品规格失败");
        }
    }

    /**
     * 编辑商品
     *
     * @param goods 商品信息
     * @return
     */
    @PUT
    @Transaction
    public HashMap edit(goods_info goods) {
        /*
        修改商品基本信息
        */
        goods_info info = goods.get("goods_info", goods_info.class);
        goods_info goodsInfo = goods_info.dao.findById(info.get("goods_id"));
        if (seller_id.equals(goodsInfo.get("seller_id"))) {
            info.set("id", info.get("goods_id"));
            info.set("name", info.get("goods_name"));
            info.update();
            /*
            添加商品规格信息
            */
            List<JSONObject> list = goods.get("goods_sku_list");
            //非空判断
            if (list != null && list.size() > 0) {
                for (JSONObject obj : list) {
                    goods_sku goodsSku = new goods_sku();
                    if (obj.get("sku_id") == null) {
                        goodsSku.set("status", ConstantsUtils.RELEASE_STATUS_OFF);//商品规格状态(0 未上架 1 已上架)
                        goodsSku.set("area_id", ConstantsUtils.ALL_AREA_ID);
                        goodsSku.set("goods_num", info.<Long>get("goods_num"));
                        goodsSku.set("amount", obj.get("sku_amount"));
                        goodsSku.set("name", obj.get("sku_name"));
                        goodsSku.set("unit_id", obj.get("sku_unit_id"));
                        goodsSku.set("seller_id", seller_id);
                        goodsSku.save();
                    }
                }
            } else {
                return CommonUtils.getCodeMessage(false, "请填写商品规格");
            }
            return CommonUtils.getCodeMessage(true, "修改商品成功");
        } else {
            return CommonUtils.getCodeMessage(false, "修改商品失败");
        }
    }
    /**
     * 批量删除商品
     *
     * @param goods_list 商品列表
     * @return
     */
    @POST("/delete")
    @Transaction
    public HashMap delete(List<JSONObject> goods_list) {
        //非空判断
        if (goods_list != null && goods_list.size() > 0) {
            for (JSONObject obj : goods_list) {
                if (obj.get("sku_id") != null) {//商品规格ID不为空时，只删除商品规格信息
                    goods_sku.dao.updateColsBy("deleted_at,status", "id = ? AND seller_id = ?", new Date(),ConstantsUtils.RELEASE_STATUS_OFF, obj.get("sku_id"), seller_id);
                    //删除商品规格价格
                    goods_sku_price.dao.deleteBy("sku_id = ? AND seller_id = ?",
                            obj.get("sku_id"), seller_id);
                } else { //商品规格ID为空时，删除商品及规格信息
                    //删除商品
                    goods_info.dao.updateColsBy("deleted_at", "num = ? AND seller_id = ?", new Date(), obj.get("goods_num"), seller_id);
                    //删除商品规格
                    goods_sku.dao.updateColsBy("deleted_at,status", "goods_num=? AND seller_id = ?",new Date(),ConstantsUtils.RELEASE_STATUS_OFF, obj.get("goods_num"), seller_id);
                    //删除商品价格
                    goods_sku_price.dao.deleteBy("goods_num=?  AND seller_id = ?",
                            obj.get("goods_num"), seller_id);
                }
            }
            return CommonUtils.getCodeMessage(true, "删除商品成功");
        } else {
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
                goods_sku.dao.updateColsBy("deleted_at,status","id=?",new Date(),ConstantsUtils.RELEASE_STATUS_OFF,goods_sku_id);
                //删除商品规格价格
                goods_sku_price.dao.deleteBy("sku_id=?", goods_sku_id);
            } else { //商品规格ID为空时，删除商品及规格信息
                //删除商品
                goods.set("deleted_at", new Date());
                goods.update();
                //删除商品规格
                goods_sku.dao.updateColsBy("deleted_at,status","goods_num=?",new Date(),ConstantsUtils.RELEASE_STATUS_OFF,goods.get("num"));
                //删除商品规格价格
                goods_sku_price.dao.deleteBy("goods_num=?", goods.get("num"));
                //删除商品主图
                String mainPicUrl = goods.get("main_pic_url");
                //判断图片地址是否正确
                if (mainPicUrl != null && !"".equals(mainPicUrl)
                        && mainPicUrl.indexOf(ConstantsUtils.PIC_DIR) != -1) {
                    deleteMainPic(mainPicUrl.substring(mainPicUrl.indexOf(ConstantsUtils.PIC_DIR)));
                }
                //删除商品详细图片
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
    @FILE(dir = ConstantsUtils.GOODS_MAIN_PIC, overwrite = false, allows = {"image/png", "image/jpg", "image/gif", "image/bmp", "image/jpeg"})
    public HashMap mainPic(UploadedFile main_pic) {
        String mainPicUrl = this.getRequest().getBaseUri() + ConstantsUtils.GOODS_MAIN_PIC + main_pic.getFileName();
        HashMap resultMap = new HashMap();
        resultMap.put("main_pic_url", mainPicUrl);
        return resultMap;
    }

    /**
     * 上传商品详情图片
     *
     * @param picMap 商品详情图片
     * @return 图片名称
     */
    @POST("/upload/detail")
    @FILE(dir = ConstantsUtils.GOODS_DETAIL_PIC, overwrite = true, allows = {"image/png", "image/jpg", "image/gif", "image/bmp", "image/jpeg"})
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
        resultMap.put("detail_pic_url", fileUrl);
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
    @Transaction
    public HashMap updown(Integer status, List<JSONObject> goods_sku_list) {
        boolean flag = false;
        for (JSONObject obj : goods_sku_list) {
            Long goodsNum = obj.getLong("goods_num");
            Long skuId = obj.getLong("sku_id");
            String price = goods_sku_price.dao.getSkuPrice(goodsNum,skuId);
            if(price == null || price.equals("0.00")){
                if(status == ConstantsUtils.RELEASE_STATUS_ON){
                    continue;
                }
            }
            flag = goods_sku.dao.updown(skuId,goodsNum,status,seller_id);
            if (!flag){
                break;
            }
        }
        if(flag){
            if (status == ConstantsUtils.RELEASE_STATUS_ON) {
                return CommonUtils.getCodeMessage(true, "商品上架成功");
            } else {
                return CommonUtils.getCodeMessage(true, "商品下架成功");
            }
        } else {
            if (status == ConstantsUtils.RELEASE_STATUS_ON) {
                return CommonUtils.getCodeMessage(false, "商品上架失败");
            } else {
                return CommonUtils.getCodeMessage(false, "商品下架失败");
            }
        }
    }

}
