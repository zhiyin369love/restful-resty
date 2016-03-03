package com.qianmo.eshop.model.buyer;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

import java.util.List;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "buyer_seller")
public class buyer_seller extends Model<buyer_seller> {
    public final static buyer_seller dao = new buyer_seller();

    public List<buyer_seller> getBuyerList(long seller_id, int status) {
        return buyer_seller.dao.findBy("seller_id = ? and status = ?", seller_id, status);
    }
}
