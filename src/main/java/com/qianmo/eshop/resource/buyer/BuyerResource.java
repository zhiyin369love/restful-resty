package com.qianmo.eshop.resource.buyer;

import cn.dreampie.route.annotation.*;
import cn.dreampie.security.Principal;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.SessionUtil;
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
    public user_info GetUserInfo() {
        Principal<user_info> principal = Subject.getPrincipal();
        if (principal != null)
            return principal.getModel();
        else
            return null;
    }

    /**
     * 买家修改个人信息,买家完善个人信息
     *
     * @param id        买家ID
     * @param user_info 待编辑的买家信息
     * @return
     */
    @PUT("/:id")
    public Map Edit(long id, user_info user_info) {
        Map result;
        //result 用来保存返回结果 code,message
        result = CommonUtils.AddreturnCodeMessage(false);
        //判断修改的用户信息是否属于当前登录用户
        if (id == SessionUtil.getUserId()) {
            //判断是否修改成功
            if (user_info.update()) {
                //result写入修改成功信息
                result = CommonUtils.AddreturnCodeMessage(true);
            }
        }

        return result;
    }

    /**
     * 买家注册
     *
     * @param username    手机号，即用户账号
     * @param code        手机验证码
     * @param confirm_pwd 二次密码确认
     * @param new_pwd     新密码
     * @return
     */
    @POST("/register")
    public Map add(String username, String code, String confirm_pwd, String new_pwd) {
        Map result;
        //result 用来保存返回结果 code,message
        result = CommonUtils.AddreturnCodeMessage(false);
        //判断两次输入的密码是否一致
        if (confirm_pwd == new_pwd) {
            //判断验证码输入的是否正确 type = 1代表买家注册时发送的验证码
            if (invite_verify_code.dao.findBy("phone = ? and code = ? and type = 1", username, code).size() > 0) {
                user_info saveUserInfo = new user_info().set("username", username).set("password", new_pwd);
                if (saveUserInfo.save()) {
                    //result写入注册成功信息
                    result = CommonUtils.AddreturnCodeMessage(true);
                }
            }
        }
        return result;
    }
}