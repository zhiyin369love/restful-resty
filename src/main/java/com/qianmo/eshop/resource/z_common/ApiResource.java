package com.qianmo.eshop.resource.z_common;

import cn.dreampie.common.http.UploadedFile;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.POST;
import cn.dreampie.route.core.Resource;
import cn.dreampie.route.core.multipart.FILE;

/**
 * Created by ccq on 16-2-20.
 */
@API("/api/v1.0")
public class ApiResource extends Resource {


    /**
     * 可以做一些公用的方法
     */

    //上传文件
    @POST("/upload/img")
    @FILE(dir = "/upload", overwrite = true)
    public UploadedFile upload(UploadedFile testfile) {
        //注意UploadedFile  参数的名字 需要和input的name对应
        //如 <input type="file" name="x"> 用UploadedFile x来接收文件
        //如果上传多个文件，使用Map<String,UploadedFile> files来接收所有的文件，key为input的name x
        return testfile;
    }
}
