package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "goods_info")
public class goods_info extends Model<goods_info> {
    public final static goods_info dao = new goods_info();

    /**
     * 根据规格ID查询商品
     * @param buyerId 买家ID
     * @param skuId  规格ID
     * @return
     */
    public goods_info findGoodsInfo(Long buyerId,Long skuId){
        goods_info goodsInfo = new goods_info();
        String sql = YamlRead.getSQL("findGoodsInfo", "buyer/goods");
        sql = sql + " AND e.id = ?";
        goodsInfo = goods_info.dao.findFirst(sql,buyerId,buyerId,buyerId,skuId);
        return goodsInfo;
    }
}
