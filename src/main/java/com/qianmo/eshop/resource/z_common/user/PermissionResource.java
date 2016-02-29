package com.qianmo.eshop.resource.z_common.user;


import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.resource.z_common.ApiResource;
import com.qianmo.eshop.model.user.sec_permission;

import java.util.List;

/**
 * Created by ccq on 16-2-20.
 */
@API("/permissions")
public class PermissionResource extends ApiResource {

  @GET
  public List<sec_permission> permissions() {
    return sec_permission.dao.findBy("deleted_at IS NULL");
  }

}
