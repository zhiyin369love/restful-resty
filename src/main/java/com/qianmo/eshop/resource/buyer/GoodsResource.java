package com.qianmo.eshop.resource.buyer;

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
    //获取买家ID
    private Long buyer_id = SessionUtil.getUserId();

    /**
     * 获取商品列表
     *
     * @param goods_name  商品名称
     * @param category_id 商品分类ID
     * @param page_start  第几页开始
     * @param page_step   返回多少条
     * @param sort        排序 1:新品
     * @param sort_style  排序方式
     * @param sort_type   排序类型
     * @return
     */
    @GET
    public HashMap goods(String goods_name, Integer category_id,
                         Integer page_start, Integer page_step, Integer sort, Integer sort_style, Integer sort_type) {
        HashMap resultMap = new HashMap();
        /*
        判断是否有分页信息，如果没有，给定默认值
         */
        if (page_start == null) {
            page_start = ConstantsUtils.DEFAULT_PAGE_START; //默认从第1条开始
        }
        if (page_step == null) {
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;  //默认返回10条
        }
        String sql = "SELECT a.id goods_id,a.num goods_num,a.main_pic_url,a.name goods_name,a.producer," +
                " a.ingredient,a.seller_id,b.nickname seller_name " +
                " FROM goods_info a" +
                " INNER JOIN user_info b ON a.seller_id = b.id " +
                " INNER JOIN buyer_seller c ON a.seller_id = c.seller_id AND c.status = 1 WHERE c.buyer_id = ?";
         /*
        判断是否根据分类查找商品
         */
        if (category_id != null && category_id > 0) {
            sql = sql + " AND a.category_id=" + category_id;
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
//            if (sort_style != null && sort_style == ConstantsUtils.SORT_PRICE) {
//                sql = sql + ",c.price";
//                if (sort_type != null && sort_type == ConstantsUtils.SORT_ASC) {
//                    sql = sql + " ASC";//升序
//                } else {
//                    sql = sql + " DESC";//降序
//                }
//
//            }
        }
//        else {
//            //按价格排序
//            if (sort_style != null && sort_style == ConstantsUtils.SORT_PRICE) {
//                sql = sql + "c.price";
//                //是否升序
//                if (sort_type != null && sort_type == ConstantsUtils.SORT_ASC) {
//                    sql = sql + " ASC";//升序
//                } else {
//                    sql = sql + " DESC";//降序
//                }
//            }
//        }
        List<GoodsInfo> goodsList = new ArrayList<GoodsInfo>();
        FullPage<goods_info> list = goods_info.dao.fullPaginate(page_start / page_step + 1,page_step, sql, buyer_id);
        int total_count = list.getTotalRow();
        String skuSql = "SELECT a.id sku_id,a.name sku_name, IFNULL(b.price,a.list_price) price, " +
                " IFNULL(b.status,1) status FROM goods_sku a " +
                " LEFT JOIN goods_sku_price b ON a.id = b.sku_id AND b.buyer_id = ? " +
                " WHERE a.goods_num = ? AND a.status = 1";
        //非空判断
        if(list!=null && list.getList().size()>0){
            for (goods_info info:list.getList()){
                GoodsInfo goodsInfo = new GoodsInfo();
                goodsInfo.setGoods_id(Long.parseLong(info.get("goods_id").toString()));
                goodsInfo.setGoods_name(info.get("goods_name").toString());
                goodsInfo.setGoods_num(Long.parseLong(info.get("goods_num").toString()));
                //判断是否有主图
                if (info.get("main_pic_url") != null) {
                    goodsInfo.setMain_pic_url(info.get("main_pic_url").toString());
                }
                goodsInfo.setProducer(info.get("producer").toString());
                goodsInfo.setSeller_name(info.get("seller_name").toString());
                goodsInfo.setIngredient(info.get("ingredient").toString());
                goodsInfo.setSeller_id(info.<Long>get("seller_id"));

                List<goods_sku> skuList = goods_sku.dao.find(skuSql,buyer_id,info.get("goods_num"));
                List<GoodsSku> goodsSkuList = new ArrayList<GoodsSku>();
                if(skuList!=null && skuList.size()>0){
                    for (goods_sku sku:skuList){
                        if(sku.get("status").equals(Long.parseLong(ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE.toString()))){
                            GoodsSku goodsSku = new GoodsSku();
                            goodsSku.setSku_id(Long.parseLong(sku.get("sku_id").toString()));
                            goodsSku.setSku_name(sku.get("sku_name").toString());
                            goodsSku.setStatus(Integer.valueOf(sku.get("status").toString()));
                            goodsSku.setPrice(sku.<BigDecimal>get("price"));
                            goodsSkuList.add(goodsSku);
                        }
                    }
                }
                if (goodsSkuList!=null && goodsSkuList.size()>0){
                    goodsInfo.setGoods_sku_list(goodsSkuList);
                    goodsList.add(goodsInfo);
                }else{
                    total_count--;
                }
            }
        }
        resultMap.put("goods_list", goodsList);
        resultMap.put("total_count", total_count);
        return resultMap;


//        HashMap<Long, GoodsInfo> map = new HashMap<Long, GoodsInfo>();
//
//        //非空判断
//        if (list != null && list.getTotalRow() > 0) {
//            for (goods_info goodsInfo : list.getList()) {
//                GoodsInfo goods = map.get(Long.parseLong(goodsInfo.get("goods_id").toString()));
//                if (goods == null) {
//                    goods = new GoodsInfo();
//                    goods.setGoods_id(Long.parseLong(goodsInfo.get("goods_id").toString()));
//                    goods.setGoods_name(goodsInfo.get("goods_name").toString());
//                    goods.setGoods_num(Long.parseLong(goodsInfo.get("goods_num").toString()));
//                    //判断是否有主图
//                    if (goodsInfo.get("main_pic_url") != null) {
//                        goods.setMain_pic_url(goodsInfo.get("main_pic_url").toString());
//                    }
//                    goods.setProducer(goodsInfo.get("producer").toString());
//                    goods.setSeller_name(goodsInfo.get("seller_name").toString());
//                    goods.setIngredient(goodsInfo.get("ingredient").toString());
//                    goods.setSeller_id(goodsInfo.<Long>get("seller_id"));
//
//                    //商品规格信息
//                    List<GoodsSku> skuList = new ArrayList<GoodsSku>();
//                    GoodsSku goodsSku = new GoodsSku();
//                    goodsSku.setSku_id(Long.parseLong(goodsInfo.get("sku_id").toString()));
//                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
//                    goodsSku.setStatus(goodsInfo.<Integer>get("status"));
//                    goodsSku.setSeller_id(goodsInfo.<Long>get("seller_id"));
//                    goodsSku.setPrice(goodsInfo.<BigDecimal>get("price"));
//                    skuList.add(goodsSku);
//                    goods.setGoods_sku_list(skuList);
//                } else {
//                    //商品规格信息
//                    List<GoodsSku> skuList = (List) goods.getGoods_sku_list();
//                    GoodsSku goodsSku = new GoodsSku();
//                    goodsSku.setSku_id(Long.parseLong(goodsInfo.get("sku_id").toString()));
//                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
//                    goodsSku.setStatus(goodsInfo.<Integer>get("status"));
//                    goodsSku.setSeller_id(goodsInfo.<Long>get("seller_id"));
//                    goodsSku.setPrice(goodsInfo.<BigDecimal>get("price"));
//                    skuList.add(goodsSku);
//                    goods.setGoods_sku_list(skuList);
//                }
//                map.put(goods.getGoods_id(), goods);
//            }
//        }
//        List<GoodsInfo> goodsInfoList = new ArrayList<GoodsInfo>();
//        //非空判断
//        if (map != null && map.size() > 0) {
//            for (Long goodsId : map.keySet()) {
//                goodsInfoList.add(map.get(goodsId));
//            }
//        }
//        resultMap.put("goods_list", goodsInfoList);
//        resultMap.put("total_count", goodsInfoList.size());
//        return resultMap;
    }

    /**
     * 获取商品分类
     *
     * @return
     */
    @GET("/category")
    public List goodsCategory() {
        return goods_category.dao.getList();
    }

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    @GET("/:id")
    public HashMap goods(Long id) {
        HashMap resultMap = new HashMap();
        if (id == null) {
            return resultMap;
        }
        goods_info goodsInfo = goods_info.dao.findFirst(YamlRead.getSQL("findGoods", "buyer/goods"), id);
        resultMap.put("goods_info", goodsInfo);

        String skuSql = "SELECT a.id sku_id,a.name sku_name, IFNULL(b.price,a.list_price) price, " +
                "IFNULL(b.status,1) status FROM goods_sku a " +
                "LEFT JOIN goods_sku_price b ON a.id = b.sku_id AND b.buyer_id = ? " +
                "WHERE a.goods_num = ? AND a.status = 1";
        List<goods_sku> list = goods_sku.dao.find(skuSql, buyer_id, goodsInfo.get("goods_num"));
        List<goods_sku> skuList = new ArrayList<goods_sku>();
        if(list!=null && list.size()>0){
            for(goods_sku sku : list){
                if (sku.get("status").equals(Long.parseLong(ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE.toString()))){
                    skuList.add(sku);
                }
            }
        }
        resultMap.put("goods_sku_list", skuList);
        return resultMap;
    }
}
