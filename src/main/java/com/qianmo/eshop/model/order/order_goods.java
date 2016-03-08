package com.qianmo.eshop.model.order;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

import java.util.List;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "order_goods")
public class order_goods extends Model<order_goods> {
    public final static order_goods dao = new order_goods();

    public List<order_goods> getProducts() {
        if (this.get("id") == null) {
            this.put("id", order_goods.dao.findBy("order_id=?", this.get("id")));
        }
        String sql = YamlRead.getSQL("getFieldActivityAll","yeryer");

        return this.get("id");
    }

}
