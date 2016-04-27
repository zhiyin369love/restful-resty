package com.qianmo.eshop.resource.buyer;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.model.user.help_center;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title:
 * Description
 * Copyringt :
 * Company : 安徽阡陌网络科技有限公司
 *
 * @author fxg on 16-4-18
 * @version 1.0
 */
@API("/help_center")
public class HelpCenterResource extends ApiResource {
    /**
     * 获取帮助中心列表
     * @return
     */
    @GET
    public Map list(){
        Map map = new HashMap();
        List<help_center> list = help_center.dao.findAll();
        map.put("name","help_center_list.ftl");
        map.put("help_center_list",list);
        return map;
    }

    /**
     * 获取帮助中心详细信息
     * @param id
     * @return
     */
    @GET("/:id")
    public Map helpCenter(long id){
        Map map = new HashMap();
        map.put("name","help_center.ftl");
        help_center helpCenter = help_center.dao.findById(id);
        map.put("help_center",helpCenter);
        return map;
    }
}
