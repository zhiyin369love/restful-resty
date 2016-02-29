package com.qianmo.eshop.resource.z_common.user;


import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.resource.z_common.ApiResource;
import com.qianmo.eshop.model.user.sec_role;

import java.util.List;

/**
 * Created by ccq on 16-2-20.
 */
@API("/roles")
public class RoleResource extends ApiResource {

  @GET
  public List<sec_role> roles(){
    return sec_role.dao.findBy("deleted_at IS NULL");
  }

  @GET("/:id")
  public sec_role permissions(int id){
    return sec_role.dao.findFirstBy("id=?",id);
  }

}
