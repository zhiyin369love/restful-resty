package com.qianmo.eshop.resource.buyer;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.model.user.app_nav;
import com.qianmo.eshop.model.user.app_version;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title:
 * Description
 * Copyringt :
 * Company : 安徽阡陌网络科技有限公司
 *
 * @author fxg on 16-4-11
 * @version 1.0
 */
@API("/app")
public class APPResource extends BuyerResource {
    /**
     * 获取最新版本APP
     * @param version 版本号
     * @param type 类型 1：Android  2：IOS
     * @return
     */
    @GET
    public HashMap list(String version,Integer type){
        app_version appVersion = null;
        HashMap resultMap = new HashMap();
        List<app_version> list = app_version.dao.findBy("type=? AND deleted_at is null",type);
        if (list!=null && list.size()>0){
            appVersion = list.get(0);
        }
        if(appVersion==null || version.equals(appVersion.get("version"))){
            resultMap.put("version_code",0);
            resultMap.put("message","当前版本为最新版本，无需更新");
        }else{
            resultMap.put("version_code",1);
            resultMap.put("message",appVersion.get("version"));
            resultMap.put("content",appVersion.get("content"));
            resultMap.put("url",appVersion.get("url"));
        }
        return resultMap;

    }

    /**
     * 获取APP导航
     * @param app_type 类型 1：买家APP 2：卖家APP
     * @return
     */
    @GET("/nav")
    public HashMap getHomeNav(int app_type){
        HashMap resultMap = new HashMap();
        List<app_nav> list = app_nav.dao.findBy("app_type=? AND deleted_at is null ORDER BY sort", app_type);
        resultMap.put("app_nav_list",list);
        return resultMap;
    }
}
