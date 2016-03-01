package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "goods_type")
public class goods_type extends Model<goods_type> {
    public final static goods_type dao = new goods_type();
}
