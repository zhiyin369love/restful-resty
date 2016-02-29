package com.qianmo.eshop.resource.z_common;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.DELETE;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.POST;
import cn.dreampie.security.Principal;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.model.user.user_info;

/**
 * Created by ccq on 16-2-20.
 */
@API("/sessions")
public class SessionResource extends ApiResource {

  @GET
  public user_info get() {
    Principal<user_info> principal = Subject.getPrincipal();
    if (principal != null)
      return principal.getModel();
    else
      return null;
  }


  @POST
  public user_info login(String username, String password, boolean rememberMe) {
    Subject.login(username, password, rememberMe);
    return (user_info) Subject.getPrincipal().getModel();
  }


  @DELETE
  public boolean logout() {
    Subject.logout();
    return true;
  }
}
