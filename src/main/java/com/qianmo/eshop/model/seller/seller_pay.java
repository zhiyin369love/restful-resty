package com.qianmo.eshop.model.seller;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "seller_pay")
public class seller_pay extends Model<seller_pay> {
    public final static seller_pay dao = new seller_pay();
}
