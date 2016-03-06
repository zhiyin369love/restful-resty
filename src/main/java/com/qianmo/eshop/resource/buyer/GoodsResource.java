package com.qianmo.eshop.resource.buyer;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_type;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.HashMap;
import java.util.List;

/**
 * Created by fxg06 on 2016/3/6.
 */
@API("/goods")
public class GoodsResource extends ApiResource {
    /**
     * 获取商品分类
     * @return
     */
    @GET("/type")
    public List goodsType(){
        return goods_type.dao.getList();
    }

    /**
     * 获取商品详情
     * @param id
     * @param buyer_id
     * @return
     */
    public HashMap goods(long id,long buyer_id){
        HashMap resultMap = new HashMap();
        goods_info goodsInfo = goods_info.dao.findFirst(YamlRead.getSQL("findGoods","buyer/goods"),id);
        resultMap.put("goods_info",goodsInfo);
        List<goods_sku> list = goods_sku.dao.find(YamlRead.getSQL("findGoodsSku","buyer/goods"),goodsInfo.get("goods_num"),buyer_id);
        resultMap.put("goods_sku_list",list);
        return resultMap;
    }
}
