package com.qianmo.eshop.model.order;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "buyer_receive_address")
public class order_user extends Model<order_user> {
    public final static order_user dao = new order_user();
}
