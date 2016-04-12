package com.qianmo.eshop.config;

import cn.dreampie.security.AuthenticateService;
import cn.dreampie.security.Principal;
import cn.dreampie.security.credential.Credential;
import com.qianmo.eshop.model.user.sec_permission;
import com.qianmo.eshop.model.user.user_info;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ccq on 16-2-20.
 */
public class MyAuthenticateService extends AuthenticateService {


  public Principal getPrincipal(String username) {
    user_info user = user_info.dao.unCache().findFirstBy("username=? AND deleted_at IS NULL", username);
    if (user != null)
      return new Principal<user_info>(username, user.<String>get("password"), user.getPermissionValues(), user);
    else
      return null;
  }

  public Set<Credential> getAllCredentials() {
    List<sec_permission> permissions = sec_permission.dao.findBy("deleted_at IS NULL");
    Set<Credential> credentials = new HashSet<Credential>();

    for (sec_permission permission : permissions) {
      credentials.add(new Credential(permission.<String>get("method"), permission.<String>get("url"), permission.<String>get("value")));
    }

    return credentials;
  }
}
