package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-2-20.
 */
@Table(name = "sec_role_permission", cached = true)
public class sec_role_permission extends Model<sec_role_permission> {
  public static final sec_role_permission dao = new sec_role_permission();

}
