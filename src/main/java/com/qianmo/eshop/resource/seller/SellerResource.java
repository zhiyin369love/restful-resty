package com.qianmo.eshop.resource.seller;

import cn.dreampie.route.annotation.*;
import cn.dreampie.security.Principal;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.HashMap;

/**
 * 卖家api
 * Created by ccq on 16-1-1.
 */
@API("/seller")
public class SellerResource extends ApiResource {

  //获取当前登录卖家用户信息
  @GET
  public user_info Get() {
    Principal<user_info> principal = Subject.getPrincipal();
    if (principal != null)
      return principal.getModel();
    else
      return null;
  }

  @PUT("/:id")
  public HashMap Edit(long id, user_info user_info) {
    HashMap result = user_info.dao.Edit(id,user_info);
    return result;
  }
}