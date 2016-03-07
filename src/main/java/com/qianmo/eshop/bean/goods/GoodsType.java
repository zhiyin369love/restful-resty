package com.qianmo.eshop.bean.goods;

import java.util.Collection;

/**
 * @Class 商品分类
 * Created by fxg06 on 2016/3/2.
 */
public class GoodsType {

    private Collection goods_type_list;//商品分类子分类
    private long type_id;//商品分类id
    private String type_name;//商品分类名称
    private long goods_count;//该商品分类包含的商品总数

    public long getType_id() {
        return type_id;
    }
    public void setType_id(long type_id) {
        this.type_id = type_id;
    }
    public String getType_name() {
        return type_name;
    }
    public void setType_name(String type_name) {
        this.type_name = type_name;
    }
    public Collection getGoods_type_list() {
        return goods_type_list;
    }
    public void setGoods_type_list(Collection goods_type_list) {
        this.goods_type_list = goods_type_list;
    }
    public long getGoods_count(){
        return goods_count;
    }
    public void setGoods_count(long goods_count){
        this.goods_count = goods_count;
    }

}
