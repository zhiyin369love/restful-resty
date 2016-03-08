package com.qianmo.eshop.resource.seller;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.bean.goods.GoodsCategory;
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
@API("/type")
public class GoodsCategoryRescource extends GoodsResource {
    /**
     * 获取商品分类
     * @return
     */
    @GET
    public List goodsCategory(){
        return goods_category.dao.getList();
    }
    /**
     * 获取商品分类及该商品分类下的商品总数
     * @param goods_name 商品名称
     * @return
     */
    @GET("/count")
    public List getList(String goods_name){
        user_info userInfo = (user_info) Subject.getPrincipal().getModel();
        long seller_id = 1;
        //判断登录用户是否为子账号，如果是则获取其父级id
        if(userInfo!=null){
            if(Long.parseLong(userInfo.get("pid").toString())==0){
                seller_id = Long.parseLong(userInfo.get("id").toString());
            }else{
                seller_id = Long.parseLong(userInfo.get("pid").toString());
            }
        }
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
                            sql = sql + " AND name like '%?%'";
                            childCount = goods_info.dao.queryFirst(sql,childCategory.getCategory_id(),seller_id,goods_name);
                        }else{
                            childCount = goods_info.dao.queryFirst(sql,childCategory.getCategory_id(),seller_id);
                        }
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
     * @return
     */
    @GET("/unit")
    public List getSkuUnit(){
        return goods_sku_unit.dao.getList();
    }

    /**
     * 获取商品剂型
     * @param category_id 商品分类id
     * @return
     */
    @GET("/form")
    public List getForm(long category_id){
        return goods_form.dao.find(YamlRead.getSQL("findGoodsForm","seller/goods"),category_id);
    }
}
