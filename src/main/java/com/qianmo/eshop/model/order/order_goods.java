package com.qianmo.eshop.model.order;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "buyer_receive_address")
public class order_goods extends Model<order_goods> {
    public final static order_goods dao = new order_goods();
}
