package com.qianmo.eshop.bean.user;

import java.math.BigDecimal;

/**
 * Created by fxg06 on 2016/3/17.
 */
public class UserInfo {
    private Long buyer_id;
    private String nickname;
    private String buyer_address;
    private BigDecimal price;
    private Long sku_id;
    private String sku_name;
    private Long goods_num;
    private Long sku_price_id;
    private int sku_price_status;

    public Long getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(Long buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBuyer_address() {
        return buyer_address;
    }

    public void setBuyer_address(String buyer_address) {
        this.buyer_address = buyer_address;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

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

    public Long getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(Long goods_num) {
        this.goods_num = goods_num;
    }

    public Long getSku_price_id() {
        return sku_price_id;
    }

    public void setSku_price_id(Long sku_price_id) {
        this.sku_price_id = sku_price_id;
    }

    public int getSku_price_status() {
        return sku_price_status;
    }

    public void setSku_price_status(int sku_price_status) {
        this.sku_price_status = sku_price_status;
    }
}
