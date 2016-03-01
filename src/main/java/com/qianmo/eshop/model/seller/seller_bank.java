package com.qianmo.eshop.model.seller;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "seller_bank")
public class seller_bank extends Model<seller_bank> {
    public final static seller_bank dao = new seller_bank();
}
