package com.qianmo.eshop.resource.z_common;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.common.util.Maper;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.route.core.Resource;
import cn.dreampie.security.Principal;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.*;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.user_info;
import cn.dreampie.security.Subject;

import java.util.Date;
import java.util.Map;


/**
 * 公用方法
 * Created by ccq on 16-2-20.
 */
@API("/api/v1.0")
public class ApiResource extends Resource {

    //登录
    @POST("/login")
    public WebResult Login(String username, String password, boolean rememberMe) {
        int code;
        HttpStatus status;
        String message;
        boolean isBuyer, isSeller;
        Map result;

        //调用登录方法
        Subject.login(username, password, rememberMe);
        //获取缓存是否有用户信息来判断是否登录成功
        user_info UserInfo = (user_info) Subject.getPrincipal().getModel();
        if (UserInfo != null) {
            //登录成功
            status = HttpStatus.ACCEPTED;
            code = status.getCode();
            message = "登录成功";
            isBuyer = UserInfo.get("isbuyer");
            isSeller = UserInfo.get("isseller");
        } else {
            //登录失败
            status = HttpStatus.NON_AUTHORITATIVE_INFORMATION;
            code = status.getCode();
            message = "用户名或密码错误";
            isBuyer = false;
            isSeller = false;
        }
        //将返回结果写入map中返回
        result = Maper.of("code", code, "message", message, "isbuyer", isBuyer, "isseller", isSeller);
        return new WebResult(status, result);
    }

    //注销
    @DELETE("/logout")
    public boolean Logout() {
        Subject.logout();
        return true;
    }

    //修改密码
    @POST("/updatepwd/:id")
    public WebResult UpdatePwd(long id, String confirm_pwd, String new_pwd, String old_pwd) {

        if (user_info.dao.UpdatePwd(id, confirm_pwd, new_pwd, old_pwd)) {
            return new WebResult(HttpStatus.OK, Maper.of("code", HttpStatus.OK, "message", "修改成功"));
        } else {
            return new WebResult(HttpStatus.BAD_REQUEST, Maper.of("code", HttpStatus.BAD_REQUEST, "message", "修改失败"));
        }
    }

    //重置密码
    @POST("/resetpwd/:id")
    public WebResult ResetPwd(long id, String confirm_pwd, String pwd, String token) {

        if (user_info.dao.ResetPwd(id, confirm_pwd, pwd, token)) {
            return new WebResult(HttpStatus.OK, Maper.of("code", HttpStatus.OK, "message", "修改成功"));
        } else {
            return new WebResult(HttpStatus.BAD_REQUEST, Maper.of("code", HttpStatus.BAD_REQUEST, "message", "修改失败"));
        }
    }

    //检查验证码是否正确(重置密码,注册使用)
    @POST("/checkcode")
    public WebResult CheckCode(String code, String phone) {
        String token = user_info.dao.CheckCode(code, phone);
        if (token != null) {
            return new WebResult(HttpStatus.OK, Maper.of("code", HttpStatus.OK, "message", "验证成功", "token", token));
        } else {
            return new WebResult(HttpStatus.BAD_REQUEST, Maper.of("code", HttpStatus.BAD_REQUEST, "message", "修改失败"));
        }
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param op    操作
     */
    @PUT("/send/code")
    public WebResult SendCode(String phone, String op) {
        try {
            String content = PropertyUtil.getProperty("sms.content");
            String code;
            String resultContent = "";
            JSONObject returnResult;
            if (phone != null) {
                code = CommonUtils.getRandNum(ConstantsUtils.SIX);
                Date ExpireTime = new Date(System.currentTimeMillis() + 15*60*1000); //十五分钟
                returnResult = (JSONObject) JSON.parse(SmsApi.sendSms(SmsApi.APIKEY, content + code, phone));
                invite_verify_code.dao.set("area_id", ConstantsUtils.ALL_AREA_ID).set("code", code).set("type", op)
                        .set("expire_time", DateUtils.getDateString(ExpireTime, DateUtils.format_yyyyMMddHHmmss)).set("phone", phone).save();
                if (returnResult.get("msg") == null || (returnResult.get("msg") != null && !"OK".equals(returnResult.get("msg")))) {
                    resultContent += phone + "短信发送失败;";
                }
                if (!"".equals(resultContent)) {
                    return new WebResult(HttpStatus.OK, resultContent);
                } else {
                    return new WebResult(HttpStatus.OK, "发送验证码成功");
                }
            } else {
                return new WebResult(HttpStatus.EXPECTATION_FAILED, "输入参数有误");
            }
        } catch (Exception e) {
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "异常错误");
        }
    }
}
