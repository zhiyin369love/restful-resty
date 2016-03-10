package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.security.Subject;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.bean.goods.GoodsCategory;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.goods.goods_form;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku_unit;
import com.qianmo.eshop.model.user.user_info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fxg06 on 2016/3/1.
 */
@API("/category")
public class GoodsCategoryRescource extends GoodsResource {
    /**
     * 获取商品分类
     * @return
     */
    @GET
    public WebResult goodsCategory(){
        return new WebResult(HttpStatus.OK,goods_category.dao.getList());
    }
    /**
     * 获取商品分类及该商品分类下的商品总数
     * @param goods_name 商品名称
     * @return
     */
    @GET("/count")
    public WebResult countList(String goods_name){
        Long seller_id = SessionUtil.getAdminId();
        //获取商品分类
        List<GoodsCategory> list = goods_category.dao.getList();
        String sql = YamlRead.getSQL("findGoodsCount","seller/goods");
        if(list!=null && list.size()>0){
            for(GoodsCategory category:list){
                long count = 0;
                List<GoodsCategory> childList = (List)category.getGoods_category_list();
                if(childList!=null && childList.size()>0){
                    for(GoodsCategory childCategory:childList){
                        long childCount = 0;
                        if (goods_name!=null && !"".equals(goods_name)){
                            sql = sql + " AND name like '%"+goods_name+"%' ";
                        }
                        childCount = goods_info.dao.queryFirst(sql,childCategory.getCategory_id(),seller_id);
                        childCategory.setGoods_count(childCount);
                        count += childCount;
                    }
                }
                category.setGoods_count(count);
            }
        }
        return new WebResult(HttpStatus.OK, list);
    }
    /**
     * 获取商品规格单位信息
     * @return
     */
    @GET("/unit")
    public WebResult skuUint(){
        return new WebResult(HttpStatus.OK, goods_sku_unit.dao.getList());
    }

    /**
     * 获取商品剂型
     * @param category_id 商品分类id
     * @return
     */
    @GET("/form")
    public WebResult form(Long category_id){
        List list = goods_form.dao.find(YamlRead.getSQL("findGoodsForm","seller/goods"),category_id);
        return new WebResult(HttpStatus.OK, list);
    }
}
