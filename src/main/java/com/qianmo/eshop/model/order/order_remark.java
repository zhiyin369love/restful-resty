package com.qianmo.eshop.model.order;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "order_remark")
public class order_remark extends Model<order_remark> {
    public final static order_remark dao = new order_remark();
}
