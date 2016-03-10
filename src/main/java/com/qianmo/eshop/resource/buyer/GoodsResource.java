package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.bean.goods.GoodsInfo;
import com.qianmo.eshop.bean.goods.GoodsSku;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fxg06 on 2016/3/6.
 */
@API("/goods")
public class GoodsResource extends BuyerResource {
    /**
     * 获取商品列表
     *
     * @param goods_name      商品名称
     * @param category_id     商品一级分类ID
     * @param sub_category_id 商品二级分类ID
     * @param page_start      第几页开始
     * @param page_step       返回多少条
     * @param sort            排序 1:新品
     * @param sort_style      排序方式
     * @param sort_type       排序类型
     * @return
     */
    @GET
    public WebResult goods(String goods_name, Integer category_id, Integer sub_category_id,
                           Integer page_start, Integer page_step, Integer sort, Integer sort_style, Integer sort_type) {
        if (category_id == null) {
            return new WebResult(HttpStatus.INTERNAL_SERVER_ERROR, "查询不到商品信息");
        }
        //获取用户ID
        Long buyer_id = SessionUtil.getUserId();
        /*
        判断是否有分页信息，如果没有，给定默认值
         */
        if (page_start == null) {
            page_start = ConstantsUtils.DEFAULT_PAGE_START; //默认从第1条开始
        }
        if (page_step == null) {
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;  //默认返回10条
        }

        String sql = YamlRead.getSQL("findGoodsInfo", "buyer/goods");
        /*
        判断是根据一级分类查商品还是二级分类查商品
         */
        if (sub_category_id != null && sub_category_id > 0) {
            sql = sql + " AND a.category_id=" + sub_category_id;
        } else {
            sql = sql + " AND a.category_id in (SELECT id from goods_category where pid=" + category_id + ")";
        }
        /*
        判断是否根据商品名称模糊搜索
         */
        if (goods_name != null && !"".equals(goods_name)) {
            sql = sql + " AND a.name like '%" + goods_name + "%'";
        }
        if (sort != null) {
            //是否按新品排序 目前是否为新品都是根据上架时间倒序查询
            if (sort == ConstantsUtils.SORT_NEW) {
                sql = sql + " ORDER BY b.release_date DESC";
            } else {
                sql = sql + " ORDER BY b.release_date DESC";
            }
            //按价格排序
            if (sort_style != null && sort_style == ConstantsUtils.SORT_PRICE) {
                sql = sql + ",c.price";
                if (sort_type != null && sort_type == ConstantsUtils.SORT_ASC) {
                    sql = sql + " ASC";//升序
                } else {
                    sql = sql + " DESC";//降序
                }

            }
        } else {
            //按价格排序
            if (sort_style != null && sort_style == ConstantsUtils.SORT_PRICE) {
                sql = sql + "c.price";
                //是否升序
                if (sort_type != null && sort_type == ConstantsUtils.SORT_ASC) {
                    sql = sql + " ASC";//升序
                } else {
                    sql = sql + " DESC";//降序
                }
            }
        }
        HashMap resultMap = new HashMap();
        HashMap<Long, GoodsInfo> map = new HashMap<Long, GoodsInfo>();
        FullPage<goods_info> list = goods_info.dao.fullPaginate(page_start / page_step + 1,
                page_step, sql, buyer_id);
        //非空判断
        if (list != null && list.getTotalRow() > 0) {
            for (goods_info goodsInfo : list.getList()) {
                GoodsInfo goods = map.get(Long.parseLong(goodsInfo.get("goods_id").toString()));
                if (goods == null) {
                    goods = new GoodsInfo();
                    goods.setGoods_id(Long.parseLong(goodsInfo.get("goods_id").toString()));
                    goods.setGoods_name(goodsInfo.get("goods_name").toString());
                    goods.setGoods_num(Long.parseLong(goodsInfo.get("goods_num").toString()));
                    //判断是否有主图
                    if (goodsInfo.get("main_pic_url") != null) {
                        goods.setMain_pic_url(goodsInfo.get("main_pic_url").toString());
                    }
                    goods.setProducer(goodsInfo.get("producer").toString());
                    goods.setSeller_name(goodsInfo.get("seller_name").toString());
                    goods.setIngredient(goodsInfo.get("ingredient").toString());

                    //商品规格信息
                    List<GoodsSku> skuList = new ArrayList<GoodsSku>();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(Long.parseLong(goodsInfo.get("sku_id").toString()));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(goodsInfo.<Integer>get("status"));
                    goodsSku.setPrice(goodsInfo.<BigDecimal>get("price"));
                    goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    skuList.add(goodsSku);
                    goods.setSkuList(skuList);
                } else {
                    //商品规格信息
                    List<GoodsSku> skuList = (List) goods.getSkuList();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(Long.parseLong(goodsInfo.get("sku_id").toString()));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(goodsInfo.<Integer>get("status"));
                    goodsSku.setPrice(goodsInfo.<BigDecimal>get("price"));
                    goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    skuList.add(goodsSku);
                    goods.setSkuList(skuList);
                }
                map.put(goods.getGoods_id(), goods);
            }
        }
        List<GoodsInfo> goodsInfoList = new ArrayList<GoodsInfo>();
        //非空判断
        if (map != null && map.size() > 0) {
            for (Long goodsId : map.keySet()) {
                goodsInfoList.add(map.get(goodsId));
            }
        }
        resultMap.put("goods_list", goodsInfoList);
        resultMap.put("total_count", goodsInfoList.size());
        return new WebResult(HttpStatus.OK, resultMap);
    }

    /**
     * 获取商品分类
     *
     * @return
     */
    @GET("/category")
    public WebResult goodsCategory() {
        return new WebResult(HttpStatus.OK, goods_category.dao.getList());
    }

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    @GET("/:id")
    public WebResult goods(Long id) {
        if (id == null) {
            return new WebResult(HttpStatus.INTERNAL_SERVER_ERROR, "查询不到商品信息");
        }
        Long buyer_id = SessionUtil.getUserId();
        HashMap resultMap = new HashMap();
        goods_info goodsInfo = goods_info.dao.findFirst(YamlRead.getSQL("findGoods", "buyer/goods"), id);
        resultMap.put("goods_info", goodsInfo);
        List<goods_sku> list = goods_sku.dao.find(YamlRead.getSQL("findGoodsSku", "buyer/goods"), goodsInfo.get("goods_num"), buyer_id);
        resultMap.put("goods_sku_list", list);
        return new WebResult(HttpStatus.OK, resultMap);
    }
}
