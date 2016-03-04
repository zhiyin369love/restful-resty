package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.UploadedFile;
import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.route.core.multipart.FILE;
import com.qianmo.eshop.common.CodeUtils;
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
public class GoodsResource extends ApiResource {
    @GET
    public HashMap list(String goods_name,int goods_status,int goods_type_id,int page_start,int page_step){
        HashMap resultMap = new HashMap();

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
        try {
            user_info user = user_info.dao.findById(goods.get("seller_id"));
            int seller_id = 0;
            //判断添加商品的用户是否为子账号，如果是则获取其父级id
            if(Integer.parseInt(user.get("pid").toString())==0){
                seller_id = user.get("id");
            }else{
                seller_id = user.get("pid");
            }
            /*
            添加商品基本信息
            */
            goods_info goodsInfo = goods.get("goods_info");
            String goodsNum = CodeUtils.code(goodsInfo.get("goods_type_id").toString(),1);
            goodsInfo.set("areaId",1);
            goodsInfo.set("num",goodsNum);
            goodsInfo.set("type_id",goodsInfo.get("goods_type_id"));
            goodsInfo.set("name",goodsInfo.get("goods_name"));
            goodsInfo.set("seller_id",seller_id);
            goodsInfo.set("status",0);
            goodsInfo.save();
            /*
            添加商品规格信息
             */
            List<goods_sku> list = goods.get("goods_sku_list");
            List<goods_sku> skuList = new ArrayList<goods_sku>();
            if(list!=null && list.size()>0){
                for (goods_sku sku:list){
                    goods_sku goodsSku = new goods_sku();
                    goodsSku.set("status",0);
                    goodsSku.set("area_id",1);
                    goodsSku.set("goods_num",goodsNum);
                    goodsSku.set("amount",sku.get("sku_amount"));
                    goodsSku.set("name",sku.get("sku_name"));
                    goodsSku.set("unit_id",sku.get("sku_unit_id"));
                    goodsSku.set("unit_name",sku.get("sku_unit_name"));
                    skuList.add(goodsSku);
                }
            }
            goods_sku.dao.save(skuList);
            return new WebResult(HttpStatus.OK, "添加商品成功");
        } catch (Exception e) {
            //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "添加商品失败");
        }
    }
    /**
     * 编辑商品
     * @param goods
     * @param id
     * @return
     */
    @PUT("/:id")
    @Transaction
    public WebResult update(goods_info goods,long id){
        try {
            /*
            修改商品基本信息
            */
            goods_info goodsInfo = goods_info.dao.findById(id);
            goodsInfo.set("name",goodsInfo.get("goods_name"));
            goodsInfo.update();
            /*
            对商品规格信息的操作
             */
            List<goods_sku> list = goods.get("goods_sku_list");
            if(list!=null && list.size()>0){
                for (goods_sku sku:list){
                    //status为1表示新增商品规格
                    if(Integer.parseInt(sku.get("status").toString())==1){
                        goods_sku goodsSku = new goods_sku();
                        goodsSku.set("status",0);
                        goodsSku.set("area_id",1);
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
     * @param id
     * @param goods_sku_id
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
                String delSql = YamlRead.getSQL("deleteGoodsSku","/seller/goods");
                goods_sku.dao.update(delSql,new Date(),goods.get("num"));
                //删除商品规格价格
                goods_sku_price.dao.deleteBy("goods_num=?",goods.get("num"));
            }
            return new WebResult(HttpStatus.OK, "删除商品成功");
        } catch (Exception e) {
            //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "删除商品失败");
        }
    }
    /**
     * 上传商品主图
     * @param main_pic 商品主图
     * @return 图片名称
     */
    @POST("/upload/main")
    @FILE(dir = "/upload/goods", overwrite = true, allows = {"image/png","image/jpg","image/gif","image/bmp"})
    public String mainPic(UploadedFile main_pic){
        return main_pic.getFileName();
    }
    /**
     * 删除图片
     * @param path
     */
    public void deleteMainPic(String path){
        String picUrl = GoodsResource.class.getResource(path).getFile();
        File file = new File(picUrl);
        if (file.exists()){
            file.delete();
        }
    }
    /**
     * 上传商品详情图片
     * @param picMap 商品详情图片
     * @return 图片名称
     */
    @POST("/upload/detail")
    @FILE(dir = "/upload/detail", overwrite = true, allows = {"image/png","image/jpg","image/gif","image/bmp"})
    public String[] detailPic(Map<String,UploadedFile> picMap){
        String[] fileName = null;
        if(picMap!=null && picMap.size()>0){
            fileName = new String[picMap.size()];
            int i = 0;
            for(String key:picMap.keySet()){
                UploadedFile file = picMap.get(key);
                fileName[i] = file.getFileName();
                i++;
            }
        }
        return fileName;
    }

}
