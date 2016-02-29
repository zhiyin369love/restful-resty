package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-2-20.
 */
@Table(name = "sec_user_role", cached = true)
public class sec_user_role extends Model<sec_user_role> {
  public static final sec_user_role dao = new sec_user_role();
}
