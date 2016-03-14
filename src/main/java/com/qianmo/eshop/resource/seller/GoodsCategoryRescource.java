package com.qianmo.eshop.resource.seller;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.bean.goods.GoodsCategory;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.goods.goods_form;
import com.qianmo.eshop.model.goods.goods_info;
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
        //获取商品分类
        List<GoodsCategory> list = goods_category.dao.getList();
        String sql = YamlRead.getSQL("findGoodsCount", "seller/goods");
        if (list != null && list.size() > 0) {
            for (GoodsCategory category : list) {
                long count = 0;
                //获取商品子分类
                List<GoodsCategory> childList = (List) category.getGoods_category_list();
                if (childList != null && childList.size() > 0) {
                    //获取子分类下商品总数
                    for (GoodsCategory childCategory : childList) {
                        long childCount = 0;
                        if (goods_name != null && !"".equals(goods_name)) {
                            sql = sql + " AND name like '%" + goods_name + "%' ";
                        }
                        childCount = goods_info.dao.queryFirst(sql, childCategory.getCategory_id(), seller_id);
                        childCategory.setGoods_count(childCount);
                        count += childCount;
                    }
                }
                category.setGoods_count(count);
            }
        }
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
        List list = goods_form.dao.find(YamlRead.getSQL("findGoodsForm", "seller/goods"), category_id);
        return list;
    }
}
