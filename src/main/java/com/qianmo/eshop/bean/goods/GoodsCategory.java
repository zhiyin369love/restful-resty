package com.qianmo.eshop.bean.goods;

import java.util.Collection;

/**
 * @Class 商品分类
 * Created by fxg06 on 2016/3/2.
 */
public class GoodsCategory {

    private Collection goods_category_list;//商品分类子分类
    private long category_id;//商品分类id
    private String category_name;//商品分类名称
    private Long goods_count;//该商品分类包含的商品总数

    public long getCategory_id() {
        return category_id;
    }
    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }
    public String getCategory_name() {
        return category_name;
    }
    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
    public Collection getGoods_category_list() {
        return goods_category_list;
    }
    public void setGoods_category_list(Collection goods_category_list) {
        this.goods_category_list = goods_category_list;
    }
    public Long getGoods_count(){
        return goods_count;
    }
    public void setGoods_count(Long goods_count){
        this.goods_count = goods_count;
    }


}
