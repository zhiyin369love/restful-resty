package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.UploadedFile;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.POST;
import cn.dreampie.route.core.multipart.FILE;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

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
    @POST("/upload/img")
    @FILE(dir = "/upload/goods", overwrite = true)
    public WebResult addMainPic(UploadedFile main_pic){
        return null;
    }
}
