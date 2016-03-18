package com.qianmo.eshop.bean.goods;

import java.util.Collection;

/**
 * Created by fxg06 on 2016/3/7.
 */
public class GoodsInfo {
    private Long goods_id;//商品ID
    private String goods_name;//商品名称
    private Long goods_num;//商品编号
    private Long category_id;//商品分类ID
    private Long form_id;//商品剂型ID
    private String generic_name;//商品通用名称
    private String ingredient;//主要成分
    private String producer;//生产厂家
    private String detials;//商品详情描述
    private String licenser_num;//登记证号
    private String main_pic_url;//商品主图url
    private String pic_url_list;//商品详情图片url
    private String standard;//商品执行标准
    private Long seller_id;//商品销售厂家ID
    private String seller_name;//商品销售厂家名称

    private Collection<GoodsSku> goods_sku_list;//商品规格信息

    public Long getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Long goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public Long getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(Long goods_num) {
        this.goods_num = goods_num;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public Long getForm_id() {
        return form_id;
    }

    public void setForm_id(Long form_id) {
        this.form_id = form_id;
    }

    public String getGeneric_name() {
        return generic_name;
    }

    public void setGeneric_name(String generic_name) {
        this.generic_name = generic_name;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getDetials() {
        return detials;
    }

    public void setDetials(String detials) {
        this.detials = detials;
    }

    public String getLicenser_num() {
        return licenser_num;
    }

    public void setLicenser_num(String licenser_num) {
        this.licenser_num = licenser_num;
    }

    public String getMain_pic_url() {
        return main_pic_url;
    }

    public void setMain_pic_url(String main_pic_url) {
        this.main_pic_url = main_pic_url;
    }

    public String getPic_url_list() {
        return pic_url_list;
    }

    public void setPic_url_list(String pic_url_list) {
        this.pic_url_list = pic_url_list;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public Long getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(Long seller_id) {
        this.seller_id = seller_id;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public Collection<GoodsSku> getGoods_sku_list() {
        return goods_sku_list;
    }

    public void setGoods_sku_list(Collection<GoodsSku> goods_sku_list) {
        this.goods_sku_list = goods_sku_list;
    }
}
