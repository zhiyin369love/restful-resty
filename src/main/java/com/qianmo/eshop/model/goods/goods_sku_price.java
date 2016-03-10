package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

import java.math.BigDecimal;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "goods_sku_price")
public class goods_sku_price extends Model<goods_sku_price> {
    public final static goods_sku_price dao = new goods_sku_price();

    public BigDecimal getSkuPrice(long buyerId,long sellerID, long skuId) {
        String getSkuPriceSql = YamlRead.getSQL("getBuyerPrice","buyer/order");
        goods_sku_price goodsskuprice = goods_sku_price.dao.findFirst(getSkuPriceSql, buyerId, sellerID, skuId,skuId);
        return goodsskuprice.get("price") == null ? new BigDecimal("0") : goodsskuprice.<BigDecimal>get("price");
    }
}
