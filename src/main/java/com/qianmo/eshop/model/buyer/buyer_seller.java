package com.qianmo.eshop.model.buyer;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "buyer_receive_address")
public class buyer_seller extends Model<buyer_seller> {
    public final static buyer_seller dao = new buyer_seller();
}
