package com.qianmo.eshop.resource.seller;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_sku_unit;
import com.qianmo.eshop.model.goods.goods_type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fxg06 on 2016/3/1.
 */
@API("/type")
public class GoodsTypeRescource extends GoodsResource {
    /**
     * 获取商品分类
     * @return
     */
    @GET
    public List typeList(){
        return goods_type.dao.getList();
    }

    /**
     * 获取商品分类及该商品分类下的商品总数
     * @param goods_name
     * @return
     */
    @GET("/count")
    public List typeList(String goods_name){

        return null;
    }

    /**
     * 获取商品规格单位信息
     * @return
     */
    @GET("/unit")
    public List getSkuUnit(){
        return goods_sku_unit.dao.getList();
    }
}
