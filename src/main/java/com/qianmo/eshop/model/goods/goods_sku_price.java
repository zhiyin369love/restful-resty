package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "goods_sku_price")
public class goods_sku_price extends Model<goods_sku_price> {
    public final static goods_sku_price dao = new goods_sku_price();

    public BigDecimal getSkuPrice(long buyerId,long sellerID, long skuId) {
        String getSkuPriceSql = YamlRead.getSQL("getBuyerPrice","buyer/order");
        goods_sku_price goodsSkuPrice = goods_sku_price.dao.findFirst(getSkuPriceSql, buyerId, sellerID, skuId,skuId);
        return goodsSkuPrice.get("price") == null ? new BigDecimal("0") : goodsSkuPrice.<BigDecimal>get("price");
    }

    /**
     * 查询不可购买的零售商总数
     * @param skuId
     * @param sellerId
     * @return
     */
    public long getCount(Long skuId,Long sellerId){
        long count = queryFirst(YamlRead.getSQL("findPriceCount", "seller/goods"), skuId,sellerId);
        return count;
    }

    /**
     * 获取商品规格价格
     * @param goodsNum  商品编号
     * @param skuId  商品规格ID
     * @return
     */
    public String getSkuPrice(Long goodsNum,Long skuId){
        String price = null;
        String sql = YamlRead.getSQL("findGoodsSkuPrice", "seller/goods");
        if (skuId != null) {
            sql += " a.id = " + skuId;
        } else {
            sql += " a.goods_num = " + goodsNum;
        }
        Object obj = queryFirst(sql);
        if (obj!=null){
            price = obj.toString();
        }
        return price;
    }
}
