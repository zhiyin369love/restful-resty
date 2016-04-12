package com.qianmo.eshop.resource.seller;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.HashMap;
import java.util.Map;

/**
 * Title：
 * Description
 * Copyright：2016
 * Company：安徽阡陌网络科技有限公司
 *
 * @author Administrator on 2016/4/11 0011
 * @version 1.0
 */
@API("freemarker")
public class FreemarkerTest extends ApiResource {

    @GET
    public Map getFreemarker() {
        Map map = new HashMap();
        map.put("name","test.ftl");
        map.put("message","hello world");
        map.put("user","wangshengsheng");
        return map;
    }
}
