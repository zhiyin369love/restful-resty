package com.qianmo.eshop.model.cart;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "cart")
public class cart extends Model<cart> {
    public final static cart dao = new cart();
}
