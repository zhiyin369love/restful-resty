package com.qianmo.eshop.model.geo;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "geo_assoc")
public class geo_assoc extends Model<geo_assoc> {
  public final static geo_assoc dao = new geo_assoc();
}
