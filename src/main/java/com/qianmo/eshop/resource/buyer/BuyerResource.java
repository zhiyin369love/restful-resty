package com.qianmo.eshop.resource.buyer;

import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

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
     * @param user 待编辑的买家信息
     * @return
     */
    @PUT
    @Transaction
    public Map edit(user_info user, int bind_code) {
        Map result;
        long id;
        id = SessionUtil.getUserId();
        user_info userInfo = user_info.dao.findById(id);
        //result 用来保存返回结果 code,message
        result = CommonUtils.EditreturnCodeMessage(false);
        //判断修改的用户信息是否属于当前登录用户
        if (userInfo != null) {
            boolean isBind = true;
            user.set("id", id);
            if (!CommonUtils.isEmpty(bind_code)) {
                //判断此次是否填写绑定码,若是则调用绑定方法
                if (!buyer_seller.dao.bindSeller(bind_code, id)) {
                    isBind = false;
                }
            }
            //判断是否修改成功
            if (isBind) {
                if (user.update()) {
                    //result写入修改成功信息
                    result = CommonUtils.EditreturnCodeMessage(true);
                }
            }

        }
        return result;
    }


}