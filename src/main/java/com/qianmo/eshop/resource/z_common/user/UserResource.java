package com.qianmo.eshop.resource.z_common.user;


import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.security.DefaultPasswordService;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;
import com.qianmo.eshop.model.user.sec_role;

import java.util.Date;
import java.util.List;

/**
 * Created by ccq on 16-2-20.
 */
@API("/users")
public class UserResource extends ApiResource {

  @GET
  public List<user_info> users() {
    return user_info.dao.findBy("deleted_at IS NULL");
  }

  @GET("/:id")
  public user_info get(int id) {
    user_info user = user_info.dao.findFirstBy("id=?", id);
    if (user != null) {
      user.remove("password");
    }
    return user;
  }

  @POST
  public boolean save(user_info user) {
    String password = user.get("password");
    user.set("password", DefaultPasswordService.instance().crypto(password,"123456890"));
    return user.save();
  }

  @PUT
  @Transaction
  public boolean update(user_info user) {
    sec_role role = user.<sec_role>get("role");
    role.updatePermissions();
    return user.update();
  }


  @DELETE("/:id")
  public boolean delete(int id) {
    return user_info.dao.updateColsBy("deleted_at", "id=?", new Date(), id);
  }
}
