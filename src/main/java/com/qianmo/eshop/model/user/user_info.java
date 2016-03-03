package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ccq on 16-2-20.
 */
@Table(name = "user_info", cached = true)
public class user_info extends Model<user_info> {
  public final static user_info dao = new user_info();

  public boolean save() {
    boolean result;
    if (this.getRole() == null) {
      throw new IllegalArgumentException("必须设置角色");
    }
    if (super.save()) {
      sec_user_role ur = new sec_user_role().set("user_id", this.get("id")).set("role_id", this.getRole().get("id"));
      result = ur.save();
    } else {
      result = false;
    }
    return result;
  }

  public sec_role getRole() {
    sec_role role;
    if (this.get("role") == null && this.get("id") != null) {
      String sql = "SELECT role.id,role.name FROM sec_role role,sec_user_role user_role WHERE role.id=user_role.role_id AND user_role.user_id=?";
      role = sec_role.dao.findFirst(sql, this.get("id"));
      this.put("role", role);
    } else {
      role = this.get("role");
    }
    return role;
  }

  public List<sec_permission> getPermissions() {
    Long role_id = getRole().<Long>get("id");
    List<sec_permission> permissions;
    if (this.get("permissions") == null && role_id != null) {
      permissions = sec_permission.dao.findByRole(role_id);
      this.put("permissions", permissions);
    } else {
      permissions = this.get("permissions");
    }
    return permissions;
  }

  @JSONField(serialize = false)
  public Set<String> getPermissionValues() {
    List<sec_permission> permissions = getPermissions();
    Set<String> permissionValues = null;
    if (permissions != null) {
      permissionValues = new HashSet<String>();
      for (sec_permission permission : permissions) {
        permissionValues.add(permission.<String>get("value"));
      }
    }
    return permissionValues;
  }


  @JSONField(serialize = false)
  public Set<Long> getPermissionIds() {
    List<sec_permission> permissions = getPermissions();
    Set<Long> permissionIds = null;
    if (permissions != null) {
      permissionIds = new HashSet<Long>();
      for (sec_permission permission : permissions) {
        permissionIds.add(permission.<Long>get("id"));
      }
    }
    return permissionIds;
  }

  public user_info getUserInfoById(long id) {
    return user_info.dao.findById(id);
  }
}
