package com.qianmo.eshop.model.buyer;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "buyer_receive_address")
public class buyer_receive_address extends Model<buyer_receive_address> {
    public final static buyer_receive_address dao = new buyer_receive_address();
}
