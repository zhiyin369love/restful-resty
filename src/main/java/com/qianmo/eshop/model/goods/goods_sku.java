package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

import java.util.Date;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "goods_sku")
public class goods_sku extends Model<goods_sku> {
    public final static goods_sku dao = new goods_sku();

    /**
     * 商品或规格上下架
     * @param skuId 商品规格ID
     * @param goodsNum 商品编号
     * @param status    上下架状态 1：上架 0下架
     * @param sellerId  卖家ID
     * @return
     */
    public boolean updown(Long skuId,Long goodsNum,Integer status,Long sellerId){
        boolean flag = false;
        /*
          当商品规格id不为空时，表示只修改单个商品规格的上下架信息
          否则表示修改一个或多个商品的商品规格上下架信息
        */
        if (skuId!=null) {
            flag = updateColsBy("status,release_date", "id=? AND seller_id=? AND deleted_at is null",status, new Date(), skuId, sellerId);
        } else {
            flag = updateColsBy("status,release_date", "goods_num=? AND seller_id=? AND deleted_at is null",status, new Date(), goodsNum, sellerId);
        }
        return flag;
    }
}
