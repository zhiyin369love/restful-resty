package com.qianmo.eshop.resource.seller;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        List<Map> list = new ArrayList<Map>();
        Map map1 = new HashMap();
        map1.put("name","张三");
        map1.put("age",18);
        Map map2 = new HashMap();
        map2.put("name","李四");
        map2.put("age",19);
        list.add(map1);
        list.add(map2);
        map.put("listsss",list);
        return map;
    }
}
