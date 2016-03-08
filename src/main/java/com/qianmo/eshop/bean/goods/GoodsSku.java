package com.qianmo.eshop.bean.goods;

import java.util.Date;

/**
 * Created by fxg06 on 2016/3/7.
 */
public class GoodsSku {
    private long sku_id;//商品规格ID
    private String sku_name;//商品规格名称
    private int status;//商品规格上下架状态 1：已上架 0：已下架
    private double price;//商品规格价格
    private String release_date;//商品规格上架时间
    private int seller_count;//商品规格已售总数

    public long getSku_id() {
        return sku_id;
    }

    public void setSku_id(long sku_id) {
        this.sku_id = sku_id;
    }

    public String getSku_name() {
        return sku_name;
    }

    public void setSku_name(String sku_name) {
        this.sku_name = sku_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getSeller_count() {
        return seller_count;
    }

    public void setSeller_count(int seller_count) {
        this.seller_count = seller_count;
    }
}
