package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "buyer_receive_address")
public class goods_sku extends Model<goods_sku> {
    public final static goods_sku dao = new goods_sku();
}
