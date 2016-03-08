package com.qianmo.eshop.model.order;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "order_info")
public class order_info extends Model<order_info> {
    public final static order_info dao = new order_info();



}
