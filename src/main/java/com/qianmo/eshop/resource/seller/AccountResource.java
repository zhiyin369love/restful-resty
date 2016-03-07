package com.qianmo.eshop.resource.seller;


import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.route.annotation.*;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.user.user_info;

import java.util.HashMap;

/**
 * 卖家子账号api
 * Created by ccq on 16-1-1.
 */
@API("/account")
public class AccountResource extends SellerResource {

    /**
     * 获取卖家子账号列表
     * @param seller_id 卖家ID
     * @return
     */
    @GET
    public HashMap List(long seller_id) {
        HashMap result = new HashMap();
        //type = 1 表示为子账号 seller_id为查询此卖家名下的子账号
        result.put("user_list", user_info.dao.findBy("type = 1 and pid = ?",seller_id));
        return result;
    }

    /**
     * 获取卖家子账号详情
     * @param id 卖家子账号ID
     * @return
     */
    @GET("/:id")
    public HashMap Details(long id) {
        HashMap result = new HashMap();
        //根据ID来查询出子账号的详细信息
        result.put("user", user_info.dao.findById(id));
        return result;
    }

    /**
     * 编辑卖家子账号信息
     * @param id 子账号ID
     * @param user_info 待编辑的实体信息
     * @return
     */
    @PUT("/:id")
    public HashMap Edit(long id,user_info user_info) {
        HashMap result = user_info.dao.Edit(id,user_info);
        return result;
    }

    /**
     * 添加卖家子账号
     * @param model 待添加的子账号实体
     * @return
     */
    @POST
    public HashMap Add(user_info model) {
        HashMap result = new HashMap();
        result = CommonUtils.AddreturnCodeMessage(false);
        if(user_info.dao.save(model)){
            result = CommonUtils.AddreturnCodeMessage(true);
        }
        return result;
    }

    /**
     * 删除卖家子账号信息
     * @param id
     * @return
     */
    @DELETE("/:id")
    public HashMap Delete(long id) {
        HashMap result = new HashMap();
        result = CommonUtils.DelreturnCodeMessage(false);
        if(user_info.dao.deleteById(id)){
            result = CommonUtils.DelreturnCodeMessage(true);
        }
        return result;
    }
}