package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

import java.util.List;

/**
 * Created by ccq on 16-2-20.
 */
@Table(name = "sec_permission", cached = true)
public class sec_permission extends Model<sec_permission> {
  public static final sec_permission dao = new sec_permission();

  public List<sec_permission> findByRole(Long roleId) {
    String sql = "SELECT permission.id,permission.name,permission.value FROM sec_role_permission role_permission,sec_permission permission WHERE role_permission.permission_id=permission.id AND role_permission.role_id=?";
    return find(sql, roleId);
  }
}
