package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-2-20.
 */
@Table(name = "sec_token", generatedKey = "uuid", cached = true)
public class sec_token extends Model<sec_token> {
  public static final sec_token dao = new sec_token();

}