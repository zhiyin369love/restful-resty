package com.qianmo.eshop.model.geo;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "geo_info")
public class geo_info extends Model<geo_info> {
  public final static geo_info dao = new geo_info();
}
