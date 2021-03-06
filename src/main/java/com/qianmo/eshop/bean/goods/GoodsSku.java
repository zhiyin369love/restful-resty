package com.qianmo.eshop.bean.goods;

import java.math.BigDecimal;

/**
 * Created by fxg06 on 2016/3/7.
 */
public class GoodsSku {
    private Long sku_id;//商品规格ID
    private String sku_name;//商品规格名称
    private Integer status;//商品规格上下架状态 1：已上架 0：已下架
    private BigDecimal price;//商品规格价格
    private String release_date;//商品规格上架时间
    private Integer sell_count;//商品规格已售总数
    private Long seller_id;//卖家ID

    public Long getSku_id() {
        return sku_id;
    }

    public void setSku_id(Long sku_id) {
        this.sku_id = sku_id;
    }

    public String getSku_name() {
        return sku_name;
    }

    public void setSku_name(String sku_name) {
        this.sku_name = sku_name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public Integer getSell_count() {
        return sell_count;
    }

    public void setSell_count(Integer sell_count) {
        this.sell_count = sell_count;
    }

    public Long getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(Long seller_id) {
        this.seller_id = seller_id;
    }
}
