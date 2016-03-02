package com.qianmo.eshop.bean.goods;

import java.util.Collection;

/**
 * Created by fxg06 on 2016/3/2.
 */
public class GoodsType {

    private Collection goods_type_list;
    private int type_id;
    private String type_name;

    public int getType_id() {
        return type_id;
    }
    public void setType_id(int type_id) {
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
}
