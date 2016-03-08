package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.UploadedFile;
import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.route.core.multipart.FILE;
import cn.dreampie.security.Subject;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.bean.goods.GoodsInfo;
import com.qianmo.eshop.bean.goods.GoodsSku;
import com.qianmo.eshop.common.CodeUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.io.File;
import java.util.*;

/**
 * Created by fxg06 on 2016/3/1.
 */
@API("/goods")
public class GoodsResource extends SellerResource {
    /**
     * 获取商品列表
     * @param goods_name 商品名称
     * @param goods_status 商品上下架状态
     * @param category_id
     * @param sub_category_id
     * @param page_start
     * @param page_step
     * @return
     */
    @GET
    public HashMap list(String goods_name,Integer goods_status,Integer category_id,Integer sub_category_id,Integer page_start,Integer page_step){
        user_info userInfo = (user_info) Subject.getPrincipal().getModel();
        long seller_id = 0;
        //判断登录用户是否为子账号，如果是则获取其父级id
        if(userInfo!=null){
            if(Long.parseLong(userInfo.get("pid").toString())==0){
                seller_id = Long.parseLong(userInfo.get("id").toString());
            }else{
                seller_id = Long.parseLong(userInfo.get("pid").toString());
            }
        }
        /*
        判断是否有分页信息，如果没有，给定默认值
         */
        if (page_start==null){
            page_start = ConstantsUtils.DEFAULT_PAGE_START;
        }
        if (page_step==null){
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
        }
        HashMap resultMap = new HashMap();
        String sql = YamlRead.getSQL("findGoodsInfo","seller/goods");
        /*
        判断是根据一级分类查商品还是二级分类查商品
         */
        if(sub_category_id!=null && sub_category_id>0){
            sql = sql + " AND a.category_id="+sub_category_id;
        }else{
            sql = sql + " AND a.category_id in (SELECT id from goods_category where pid="+category_id+")";
        }
        /*
        判断是否根据商品上下架状态查商品
         */
        if (goods_status!=null){
            sql = sql + " AND b.status="+goods_status;
        }
        /*
        判断是否根据商品名称模糊搜索
         */
        if (goods_name!=null && !"".equals(goods_name)){
            sql = sql + " AND a.name like '%"+goods_name+"%'";
        }
        HashMap<Long,GoodsInfo> map = new HashMap<Long, GoodsInfo>();
        FullPage<goods_info> list = goods_info.dao.fullPaginate(page_start/page_step + 1,page_step,sql,seller_id);
        if (list!=null && list.getTotalRow()>0){
            for(goods_info goodsInfo : list.getList()){
                GoodsInfo goods = map.get(Long.parseLong(goodsInfo.get("goods_id").toString()));
                if (goods == null){
                    goods = new GoodsInfo();
                    goods.setGoods_id(Long.parseLong(goodsInfo.get("goods_id").toString()));
                    goods.setGoods_name(goodsInfo.get("goods_name").toString());
                    goods.setGoods_num(Long.parseLong(goodsInfo.get("goods_num").toString()));
                    //判断是否有主图
                    if(goodsInfo.get("main_pic_url")!=null){
                        goods.setMain_pic_url(goodsInfo.get("main_pic_url").toString());
                    }
                    goods.setProducer(goodsInfo.get("producer").toString());

                    List<GoodsSku> skuList = new ArrayList<GoodsSku>();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(Long.parseLong(goodsInfo.get("sku_id").toString()));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(Integer.parseInt(goodsInfo.get("status").toString()));
                    if(goodsInfo.get("list_price")!=null){
                        goodsSku.setPrice(Double.parseDouble(goodsInfo.get("list_price").toString()));
                    }
                    if(goodsInfo.get("release_date")!=null){
                        goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    }
                    skuList.add(goodsSku);
                    goods.setSkuList(skuList);
                }else{
                    List<GoodsSku> skuList = (List)goods.getSkuList();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(Long.parseLong(goodsInfo.get("sku_id").toString()));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(Integer.parseInt(goodsInfo.get("status").toString()));
                    if(goodsInfo.get("price")!=null){
                        goodsSku.setPrice(Double.parseDouble(goodsInfo.get("price").toString()));
                    }
                    if(goodsInfo.get("release_date")!=null){
                        goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    }
                    skuList.add(goodsSku);
                    goods.setSkuList(skuList);
                }
                map.put(goods.getGoods_id(),goods);
            }
        }
        List<GoodsInfo> goodsInfoList = new ArrayList<GoodsInfo>();
        if (map!=null && map.size()>0){
            for(Long goodsId:map.keySet()){
                goodsInfoList.add(map.get(goodsId));
            }
        }
        resultMap.put("goods_list",goodsInfoList);
        resultMap.put("total_count",goodsInfoList.size());
        return resultMap;
    }

    /**
     * 获取商品详情
     * @param id
     * @return
     */
    @GET("/:id")
    public HashMap get(long id){
        user_info userInfo = (user_info) Subject.getPrincipal().getModel();
        HashMap resultMap = new HashMap();
        goods_info goodsInfo = goods_info.dao.findFirst(YamlRead.getSQL("findGoods","seller/goods"),id);
        long seller_id = 0;
        /*
        判断当前登录用户是否为子账号，如果是则获取其父级id
         */
        if(userInfo!=null){
            if(Long.parseLong(userInfo.get("pid").toString())==0){
                seller_id = Long.parseLong(userInfo.get("id").toString());
            }else{
                seller_id = Long.parseLong(userInfo.get("pid").toString());
            }
        }
        /*
        判断当前登录用户是否有查看该商品的权限
         */
        if (seller_id==Long.parseLong(goodsInfo.get("seller_id").toString())){
            resultMap.put("goods_info",goodsInfo);
            List<goods_sku> list = goods_sku.dao.find(YamlRead.getSQL("findGoodsSku","seller/goods"),goodsInfo.get("goods_num"));
            resultMap.put("goods_sku_list",list);
        }
        return resultMap;
    }
    /**
     * 添加商品信息
     * @param goods
     * @return
     */
    @POST
    @Transaction
    public WebResult add(goods_info goods){
        user_info userInfo = (user_info) Subject.getPrincipal().getModel();
        long seller_id = 0;
        //判断添加商品的用户是否为子账号，如果是则获取其父级id
        if(userInfo!=null){
            if(Long.parseLong(userInfo.get("pid").toString())==0){
                seller_id = Long.parseLong(userInfo.get("id").toString());
            }else{
                seller_id = Long.parseLong(userInfo.get("pid").toString());
            }
        }
        /*
        添加商品基本信息
        */
        goods_info goodsInfo = goods.get("goods_info",goods_info.class);
        //生成商品编号
        String goodsNum = CodeUtils.code(goodsInfo.get("category_id").toString(),ConstantsUtils.GOODS_NUM_TYPE);
        goodsInfo.set("area_id",ConstantsUtils.ALL_AREA_ID);
        goodsInfo.set("num",goodsNum);
        goodsInfo.set("category_id",goodsInfo.get("category_id"));
        goodsInfo.set("name",goodsInfo.get("goods_name"));
        goodsInfo.set("seller_id",seller_id);
        goodsInfo.set("status",ConstantsUtils.RELEASE_STATUS_OFF);//商品状态(0 未上架 1 已上架)
        goodsInfo.save();
        /*
        添加商品规格信息
         */
        List<JSONObject> list = goods.get("goods_sku_list");
        List<goods_sku> skuList = new ArrayList<goods_sku>();
        if(list!=null && list.size()>0){
            for(JSONObject obj : list){
                goods_sku goodsSku = new goods_sku();
                goodsSku.set("status",ConstantsUtils.RELEASE_STATUS_OFF);//商品规格状态(0 未上架 1 已上架)
                goodsSku.set("area_id",ConstantsUtils.ALL_AREA_ID);
                goodsSku.set("goods_num",goodsNum);
                goodsSku.set("amount",obj.get("sku_amount"));
                goodsSku.set("name",obj.get("sku_name"));
                goodsSku.set("unit_id",obj.get("sku_unit_id"));
                goodsSku.set("seller_id",seller_id);
                skuList.add(goodsSku);
            }
        }
        goods_sku.dao.save(skuList);
        return new WebResult(HttpStatus.OK, "添加商品成功");
    }
    /**
     * 编辑商品
     * @param goods
     * @param id
     * @return
     */
    @PUT("/:id")
    @Transaction
    public WebResult edit(goods_info goods,long id,String delete_pic_url){
        try {
            /*
            修改商品基本信息
            */
            goods_info goodsInfo = goods_info.dao.findById(id);
            goodsInfo.set("name",goodsInfo.get("goods_name"));
            goodsInfo.update();
            /*
            删除商品图片
             */
            if(delete_pic_url!=null){
                String[] deletePic = delete_pic_url.split(",");
                for(String delete_pic:deletePic){
                    if(delete_pic.indexOf(ConstantsUtils.PIC_DIR)!=-1){
                        deleteMainPic(delete_pic.substring(delete_pic.indexOf(ConstantsUtils.PIC_DIR)));
                    }
                }
            }
            /*
            对商品规格信息的操作
             */
            List<goods_sku> list = goods.get("goods_sku_list");
            if(list!=null && list.size()>0){
                for (goods_sku sku:list){
                    //status为1表示新增商品规格
                    if(Integer.parseInt(sku.get("status").toString())==1){
                        goods_sku goodsSku = new goods_sku();
                        goodsSku.set("status",ConstantsUtils.RELEASE_STATUS_OFF);//商品规格状态(0 未上架 1 已上架)
                        goodsSku.set("area_id", ConstantsUtils.ALL_AREA_ID);
                        goodsSku.set("goods_num",goodsInfo.get("num"));
                        goodsSku.set("amount",sku.get("sku_amount"));
                        goodsSku.set("name",sku.get("sku_name"));
                        goodsSku.set("unit_id",sku.get("sku_unit_id"));
                        goodsSku.set("unit_name",sku.get("sku_unit_name"));
                        goodsSku.save();
                    }else{
                        goods_sku goodsSku = goods_sku.dao.findById(sku.get("sku_id"));
                        //status为2表示修改商品规格
                        if(Integer.parseInt(sku.get("status").toString())==2)
                        {
                            goodsSku.set("amount",sku.get("sku_amount"));
                            goodsSku.set("name",sku.get("sku_name"));
                            goodsSku.set("unit_id",sku.get("sku_unit_id"));
                            goodsSku.set("unit_name",sku.get("sku_unit_name"));
                        }
                        //status为3表示删除商品规格
                        else
                        {
                            goodsSku.set("deleted_at",new Date());
                        }
                        goodsSku.update();
                    }
                }
            }
            return new WebResult(HttpStatus.OK, "修改商品成功");
        } catch (Exception e) {
            //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "修改商品失败");
        }
    }
    /**
     * 删除商品或商品规格
     * @param id 商品id
     * @param goods_sku_id 商品规格id
     * @return
     */
    @DELETE("/:id")
    @Transaction
    public WebResult delete(long id,Long goods_sku_id){
        try {
            /*
            商品规格ID不为空时，只删除商品规格信息
             */
            if(goods_sku_id!=null)
            {
                //删除商品规格
                goods_sku sku = goods_sku.dao.findById(goods_sku_id);
                sku.set("deleted_at",new Date());
                sku.update();
                //删除商品规格价格
                goods_sku_price.dao.deleteBy("sku_id=?",goods_sku_id);

            }
            /*
            商品规格ID为空时，删除商品及规格信息
             */
            else
            {
                //删除商品
                goods_info goods = goods_info.dao.findById(id);
                goods.set("deleted_at",new Date());
                goods.update();
                //删除商品规格
                String delSql = YamlRead.getSQL("deleteGoodsSku","seller/goods");
                goods_sku.dao.update(delSql,new Date(),goods.get("num"));
                //删除商品规格价格
                goods_sku_price.dao.deleteBy("goods_num=?",goods.get("num"));

                //删除商品主图
                String mainPicUrl = goods.get("main_pic_url");
                if(mainPicUrl!=null && !"".equals(mainPicUrl) && mainPicUrl.indexOf(ConstantsUtils.PIC_DIR)!=-1){
                    deleteMainPic(mainPicUrl.substring(mainPicUrl.indexOf(ConstantsUtils.PIC_DIR)));
                }
                //删除商品详细图片
                String[] picUrl = goods.get("pic_url_list").toString().split(",");
                if(picUrl!=null && picUrl.length>0){
                    for(String pic:picUrl){
                        if(pic.indexOf(ConstantsUtils.PIC_DIR)!=-1){
                            deleteMainPic(pic.substring(pic.indexOf(ConstantsUtils.PIC_DIR)));
                        }
                    }
                }
            }
            return new WebResult(HttpStatus.OK, "删除商品成功");
        } catch (Exception e) {
            //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "删除商品失败");
        }
    }
    /**
     * 删除图片
     * @param path 图片路径
     */
    public void deleteMainPic(String path){
        try{
            String picUrl = this.getRequest().getRealPath(path);
            File file = new File(picUrl);
            if (file.exists()){
                file.delete();
            }
        }catch (Exception e){
            e.getMessage();
        }

    }
    /**
     * 上传商品主图
     * @param main_pic 商品主图
     * @return 图片名称
     */
    @POST("/upload/main")
    @FILE(dir = ConstantsUtils.GOODS_MAIN_PIC, overwrite = true, allows = {"image/png","image/jpg","image/gif","image/bmp"})
    public String mainPic(UploadedFile main_pic){
        return this.getRequest().getBaseUri()+ConstantsUtils.GOODS_MAIN_PIC+main_pic.getFileName();
    }
    /**
     * 上传商品详情图片
     * @param picMap 商品详情图片
     * @return 图片名称
     */
    @POST("/upload/detail")
    @FILE(dir = ConstantsUtils.GOODS_DETAIL_PIC, overwrite = true, allows = {"image/png","image/jpg","image/gif","image/bmp"})
    public String detailPic(Map<String,UploadedFile> picMap){
        String baseUri = this.getRequest().getBaseUri()+ConstantsUtils.GOODS_DETAIL_PIC;
        String fileName = "";
        if(picMap!=null && picMap.size()>0){
            for(String key:picMap.keySet()){
                UploadedFile file = picMap.get(key);
                if("".equals(fileName)){
                    fileName = baseUri + file.getFileName();
                }else{
                    fileName = fileName + "," + baseUri + file.getFileName();
                }
            }
        }
        return fileName;
    }
    /**
     * 商品上下架
     * @param status 上下架状态 1：上架 0：下架
     * @param updown_list
     * @return
     */
    @PUT("/updown")
    public WebResult updown(int status,List<goods_sku> updown_list){
        try{
            for(goods_sku sku:updown_list){
                /*
                当商品规格id不为空时，表示只修改单个商品规格的上下架信息
                否则表示修改一个或多个商品的商品规格上下架信息
                 */
                if(sku.get("sku_id")!=null){
                    goods_sku.dao.updateColsBy("status","id=?",status,sku.get("sku_id"));
                }else{
                    goods_sku.dao.updateColsBy("status","goods_num=?",status,sku.get("goods_num"));
                }
            }
            if(status==ConstantsUtils.RELEASE_STATUS_ON){
                return new WebResult(HttpStatus.OK, "商品上架成功");
            }else{
                return new WebResult(HttpStatus.EXPECTATION_FAILED, "商品下架成功");
            }
        }catch (Exception e){
            if(status==ConstantsUtils.RELEASE_STATUS_ON){
                return new WebResult(HttpStatus.OK, "商品上架失败");
            }else{
                return new WebResult(HttpStatus.EXPECTATION_FAILED, "商品下架失败");
            }
        }
    }

}
