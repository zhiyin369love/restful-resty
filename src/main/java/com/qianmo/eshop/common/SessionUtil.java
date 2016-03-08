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
}