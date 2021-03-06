package com.qianmo.eshop.common;

import cn.dreampie.security.Principal;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.seller.RetailerResource;

import java.util.Properties;

/**
 * Created by zhangyang on 3/8/16.
 */
public class SessionUtil {

    //从session中获取当前登录用户
    public static user_info getUser() throws Exception {
        Principal<user_info> principal = Subject.getPrincipal();
        if (principal != null)
            return principal.getModel();
        else
            return null;
    }

    //获取当前登录用户的属性
    public static Object getUserAttribute(String key) {
        try {
            user_info userInfo = SessionUtil.getUser();
            if(userInfo != null) {
                return userInfo.get(key);
            } else {
                return null;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static long getUserId() {
        Long userID = (Long) SessionUtil.getUserAttribute("id");
       return  userID == null ? 0: userID;
    }

    /**
     * 获取卖家登录用户最高权限的用户ID
     * @return
     */
    public static long getAdminId(){
        long pid = 0;
        try {
            user_info userInfo = SessionUtil.getUser();
            if(userInfo!=null){
                if(Integer.parseInt(userInfo.get("type").toString()) == 1){
                    pid = userInfo.get("pid");
                }else{
                    pid = userInfo.get("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return pid;
    }

    /**
     * 获取卖家登录用户最高权限用户
     * @return
     */
    public static user_info getAdminUser(){
        user_info userInfo = null;
        try {
            userInfo = getUser();
            if (Integer.parseInt(userInfo.get("type").toString()) == 1){
                userInfo = user_info.dao.findById(userInfo.get("pid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }
}