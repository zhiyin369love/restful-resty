package com.qianmo.eshop.resource.seller;


import cn.dreampie.common.http.UploadedFile;
import cn.dreampie.orm.page.Page;
import cn.dreampie.route.annotation.*;
import cn.dreampie.route.core.multipart.FILE;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.user.user_info;

import java.util.HashMap;
import java.util.List;

/**
 * 卖家子账号api
 * Created by ccq on 16-1-1.
 */
@API("/account")
public class AccountResource extends SellerResource {

    /**
     * 获取卖家子账号列表
     * @param page_start 从第几页开始取数据
     * @param page_step  取多少数据
     * @return
     */
    @GET
    public HashMap list(Integer page_start, Integer page_step) {
        if (page_start == null || page_start == 0) {
            page_start = ConstantsUtils.DEFAULT_PAGE_START;
        }
        if (page_step == null || page_step == 0) {
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
        }
        int pageNumber = page_start / page_step + 1;
        Page<user_info> userInfoPage = null;
        HashMap result = new HashMap();
        Long seller_id = SessionUtil.getAdminId();
        //type = 1 表示为子账号 seller_id为查询此卖家名下的子账号
        String userInfoSql = YamlRead.getSQL("getAccountList", "seller/seller");
        userInfoPage = user_info.dao.paginate(pageNumber, page_step, userInfoSql, seller_id);
        result.put("user_list", userInfoPage);
        return result;
    }

    /**
     * 获取卖家子账号详情
     *
     * @param id 卖家子账号ID
     * @return
     */
    @GET("/:id")
    public HashMap details(long id) {
        HashMap result = new HashMap();
        //根据ID来查询出子账号的详细信息 type=1 为过滤掉非子账号用户
        user_info userInfo = user_info.dao.findFirstBy("id = ? and type = 1",id);
        result.put("user", userInfo);
        return result;
    }

    /**
     * 编辑卖家子账号信息
     *
     * @param id        子账号ID
     * @param user_info 待编辑的实体信息
     * @return
     */
    @PUT("/:id")
    public HashMap edit(long id, user_info user_info) {
        HashMap result = user_info.dao.edit(user_info);
        return result;
    }

    /**
     * 添加卖家子账号
     *
     * @param model 待添加的子账号实体
     * @return
     */
    @POST
    public HashMap add(user_info model) {
        HashMap result = new HashMap();
        result = CommonUtils.AddreturnCodeMessage(false);
        if (user_info.dao.save(model)) {
            result = CommonUtils.AddreturnCodeMessage(true);
        }
        return result;
    }

    /**
     * 删除卖家子账号信息
     *
     * @param id
     * @return
     */
    @DELETE("/:id")
    public HashMap delete(long id) {
        HashMap result = new HashMap();
        result = CommonUtils.DelreturnCodeMessage(false);
        if (user_info.dao.deleteById(id)) {
            result = CommonUtils.DelreturnCodeMessage(true);
        }
        return result;
    }

    /**
     * 上传用户身份证图片
     *
     * @param id_pic 身份证图片
     * @return 图片名称
     */
    @POST("/upload/user")
    @FILE(dir = ConstantsUtils.USER_PIC, overwrite = false, allows = {"image/png", "image/jpg", "image/gif", "image/bmp","image/jpeg"})
    public HashMap userPic(UploadedFile id_pic) {
        String idPicUrl = this.getRequest().getBaseUri() + ConstantsUtils.USER_PIC + id_pic.getFileName();
        HashMap resultMap = new HashMap();
        resultMap.put("id_pic_url",idPicUrl);
        return resultMap;
    }

    /**
     * 上传用户营业执照-图片
     *
     * @param business_pic 营业执照图片
     * @return 图片名称
     */
    @POST("/upload/business")
    @FILE(dir = ConstantsUtils.USER_PIC, overwrite = false, allows = {"image/png", "image/jpg", "image/gif", "image/bmp","image/jpeg"})
    public HashMap businessPic(UploadedFile business_pic) {
        String businessPicUrl = this.getRequest().getBaseUri() + ConstantsUtils.USER_PIC + business_pic.getFileName();
        HashMap resultMap = new HashMap();
        resultMap.put("business_pic_url",businessPicUrl);
        return resultMap;
    }
}