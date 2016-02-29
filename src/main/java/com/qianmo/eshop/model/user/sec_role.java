package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ccq on 16-2-20.
 */
@Table(name = "sec_role", cached = true)
public class sec_role extends Model<sec_role> {
  public static final sec_role dao = new sec_role();

  public List<sec_permission> getPermissions() {
    List<sec_permission> permissions = null;
    if (this.get("permissions") == null && this.get("id") != null) {
      permissions = sec_permission.dao.findByRole(this.<Long>get("id"));
      this.put("permissions", permissions);
    } else {
      permissions = this.get("permissions");
    }
    return permissions;
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

  public void updatePermissions() {
    Long roleId = this.get("id");
    if (roleId != null) {
      sec_role role = sec_role.dao.findFirstBy("id=?", roleId);
      Set<Long> oldPermissionIds = role.getPermissionIds();
      Set<Long> newPermissionIds = this.getPermissionIds();

      newPermissionIds.removeAll(oldPermissionIds);
      oldPermissionIds.removeAll(this.getPermissionIds());
      for (Long id : oldPermissionIds) {
        sec_role_permission.dao.deleteBy("role_id=? AND permission_id=?", roleId, id);
      }

      for (Long id : newPermissionIds) {
        new sec_role_permission().set("role_id", roleId).set("permission_id", id).save();
      }
      sec_permission.dao.purgeCache();
      sec_role.dao.purgeCache();
    }
  }
}
