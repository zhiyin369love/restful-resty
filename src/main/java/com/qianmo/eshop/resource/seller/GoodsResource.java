package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.UploadedFile;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.DELETE;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.POST;
import cn.dreampie.route.core.multipart.FILE;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku_unit;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.io.File;
import java.util.Map;

/**
 * Created by fxg06 on 2016/3/1.
 */
@API("/goods")
public class GoodsResource extends ApiResource {
    @POST
    @Transaction
    public WebResult add(goods_info goods){
       return null;
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
