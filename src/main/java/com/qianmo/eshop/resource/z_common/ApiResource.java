package com.qianmo.eshop.resource.z_common;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.common.util.Maper;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.route.core.Resource;
import cn.dreampie.security.DefaultPasswordService;
import cn.dreampie.security.Principal;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.*;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.sms_template;
import com.qianmo.eshop.model.user.user_info;
import cn.dreampie.security.Subject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 公用方法
 * Created by ccq on 16-2-20.
 */
@API("/api/v1.0")
public class ApiResource extends Resource {

    //登录
    @POST("/login")
    public Map login(String username, String pwd, boolean remember_me) {
        int code;
        HttpStatus status;
        String message;
        boolean isBuyer, isSeller;
        Map result;

        //调用登录方法
        try {
            Subject.login(username, pwd, remember_me);
        }
        catch (Exception ex){
            result = CommonUtils.getCodeMessage(true,"用户名密码错误");
            return result;
        }
        //获取缓存是否有用户信息来判断是否登录成功
        user_info UserInfo = (user_info) Subject.getPrincipal().getModel();
        if (UserInfo != null) {
            //登录成功
            status = HttpStatus.OK;
            code = status.getCode();
            message = "登录成功";
            isBuyer = UserInfo.get("isbuyer");
            isSeller = UserInfo.get("isseller");
        } else {
            //登录失败
            status = HttpStatus.OK;
            code = status.getCode();
            message = "用户名或密码错误";
            isBuyer = false;
            isSeller = false;
        }
        //将返回结果写入map中返回
        result = Maper.of("code", code, "message", message, "isbuyer", isBuyer, "isseller", isSeller);
        return result;
    }

    //注销
    @DELETE("/logout")
    public boolean logOut() {
        Subject.logout();
        return true;
    }

    //修改密码
    @PUT(value = "/update_pwd")
    public WebResult updatePwd(String old_pwd, String new_pwd, String confirm_pwd) {
        Long id = SessionUtil.getUserId();
        if (user_info.dao.updatePwd(id, confirm_pwd, new_pwd, old_pwd)) {
            return new WebResult<Map<String,Object>>(HttpStatus.OK, Maper.<String, Object>of("code", HttpStatus.OK.getCode(), "message", "修改成功"));
        } else {
            return new WebResult<Map<String,Object>>(HttpStatus.OK, Maper.<String, Object>of("code", HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "message", "修改失败"));
        }
    }

    //重置密码
    @PUT("/reset_pwd")
    public WebResult resetPwd(String confirm_pwd, String pwd, String token) {
        Long id = SessionUtil.getUserId();
        if (user_info.dao.resetPwd(id, confirm_pwd, pwd, token)) {
            return new WebResult<Map<String, Object>>(HttpStatus.OK, Maper.<String, Object>of("code", HttpStatus.OK, "message", "修改成功"));
        } else {
            return new WebResult<Map<String, Object>>(HttpStatus.OK, Maper.<String, Object>of("code", HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "message", "修改失败"));
        }
    }

    //检查验证码是否正确(重置密码,注册使用)
    @POST("/check_code")
    public WebResult checkCode(String code, String phone) {
        String token = user_info.dao.checkCode(code, phone);
        if (token != null) {
            return new WebResult<Map<String, Object>>(HttpStatus.OK, Maper.<String, Object>of("code", HttpStatus.OK, "message", "验证成功", "token", token));
        } else {
            return new WebResult<Map<String, Object>>(HttpStatus.OK, Maper.<String, Object>of("code", HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "message", "验证失败"));
        }
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param op    操作
     */
    @POST("/send/code")
    public WebResult sendCode(String phone, String op) throws IOException {
        String content;
        if (op.equals(ConstantsUtils.INVITE_VERIFY_CODE_TYPE_REGISTER)) {
            content = sms_template.dao.findById(ConstantsUtils.INVITE_VERIFY_CODE_TYPE_REGISTER).get("content");
        } else content = sms_template.dao.findById(ConstantsUtils.INVITE_VERIFY_CODE_TYPE_RESET).get("content");
        String code;
        String resultContent = "";
        JSONObject returnResult;
        if (phone != null) {
            code = CommonUtils.getRandNum(ConstantsUtils.SIX);
            Date ExpireTime = new Date(System.currentTimeMillis() + 15 * 60 * 1000); //十五分钟
            returnResult = (JSONObject) JSON.parse(SmsApi.sendSms(SmsApi.APIKEY, content.replace("?", code), phone));
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
            return new WebResult(HttpStatus.OK, "输入参数有误");
        }

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
    public WebResult add(String username, String code, String confirm_pwd, String new_pwd) {
        Map<String, Object> result;
        //result 用来保存返回结果 code,message
        result = CommonUtils.AddreturnCodeMessage(false);
        //判断用户是否已经注册
        if (user_info.dao.findBy("username = ? ", username).size() == 0) {
            //判断两次输入的密码是否一致
            if (confirm_pwd.equals(new_pwd)) {
                //判断验证码输入的是否正确 type = 1代表买家注册时发送的验证码
                if (invite_verify_code.dao.findBy("phone = ? and code = ? and type = 1", username, code).size() > 0) {
                    user_info saveUserInfo = new user_info().set("username", username)
                            .set("password", DefaultPasswordService.instance().crypto(new_pwd))
                            .set("salt", "1234567890")
                            .set("area_id", ConstantsUtils.ALL_AREA_ID)
                            .set("type", 0)
                            .set("pid", 0)
                            .set("isbuyer", 0);
                    if (saveUserInfo.save()) {
                        //result写入注册成功信息
                        result = CommonUtils.AddreturnCodeMessage(true);
                    }
                }
            }
        }
        return new WebResult<Map<String, Object>>(HttpStatus.CREATED, result);
    }

    /**
     * 获取当前登录个人信息
     *
     * @return
     */
    @GET("/user")
    public user_info getUserInfo() {
        return user_info.dao.getUserInfo();
    }
}
