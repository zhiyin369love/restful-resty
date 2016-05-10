package com.qianmo.eshop.resource.seller;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.bean.goods.GoodsCategory;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.goods.goods_form;
import com.qianmo.eshop.model.goods.goods_sku_unit;

import java.util.List;

/**
 * 商品分类
 * Created by fxg06 on 2016/3/1.
 */
@API("/category")
public class GoodsCategoryRescource extends GoodsResource {
    //获取用户最高权限ID
    private Long seller_id = SessionUtil.getAdminId();

    /**
     * 获取商品分类
     *
     * @return
     */
    @GET
    public List goodsCategory() {
        return goods_category.dao.getList();
    }

    /**
     * 获取商品分类及该商品分类下的商品总数
     *
     * @param goods_name 商品名称
     * @return
     */
    @GET("/count")
    public List countList(String goods_name) {
        List<GoodsCategory> list = goods_category.dao.getCountList(goods_name,seller_id);
        return list;
    }

    /**
     * 获取商品规格单位信息
     *
     * @return
     */
    @GET("/unit")
    public List skuUint() {
        return goods_sku_unit.dao.getList();
    }

    /**
     * 获取商品剂型
     *
     * @param category_id 商品分类id
     * @return
     */
    @GET("/form")
    public List form(Long category_id) {
        List list = goods_form.dao.getFirmList(category_id);
        return list;
    }
}
