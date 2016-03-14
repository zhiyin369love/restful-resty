package com.qianmo.eshop.resource.buyer;

import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.security.Principal;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.HashMap;
import java.util.Map;

/**
 * 买家api
 * Created by ccq on 16-1-1.
 */
@API("/buyer")
public class BuyerResource extends ApiResource {

    /**
     * 获取当前登录的买家信息
     *
     * @return
     */
    @GET
    public user_info getUserInfo() {
        return user_info.dao.getUserInfo();
    }

    /**
     * 买家修改个人信息,买家完善个人信息
     *
     * @param id        买家ID
     * @param user_info 待编辑的买家信息
     * @return
     */
    @PUT("/:id")
    @Transaction
    public Map edit(long id, user_info user_info, int bind_code) {
        Map result;
        //result 用来保存返回结果 code,message
        result = CommonUtils.AddreturnCodeMessage(false);
        //判断修改的用户信息是否属于当前登录用户
        if (id == SessionUtil.getUserId()) {
            if(!CommonUtils.isEmpty(bind_code)) {
                //判断此次是否修改绑定码,若是则调用绑定方法
                buyer_seller.dao.bindSeller(bind_code,id);
            }
            //判断是否修改成功
            if (user_info.update()) {
                //result写入修改成功信息
                result = CommonUtils.AddreturnCodeMessage(true);
            }
        }
        return result;
    }


}