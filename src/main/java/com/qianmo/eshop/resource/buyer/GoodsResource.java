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
     * @return
     */
    @GET
    public HashMap goods(String goods_name, Integer category_id,
                         Integer page_start, Integer page_step, Integer sort) {
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

        String sql = "SELECT b.id goods_id,b.num goods_num,b.main_pic_url,b.name goods_name,b.producer,b.ingredient," +
                "b.seller_id,a.price,d.nickname seller_name,e.id sku_id,e.name sku_name " +
                "FROM (SELECT id sku_id,goods_num,list_price price FROM goods_sku a WHERE a.status = 1 " +
                "AND NOT EXISTS (SELECT 1 FROM goods_sku_price b WHERE b.sku_id = a.id AND b.status = 1) " +
                "UNION ALL SELECT sku_id,goods_num,price FROM goods_sku_price WHERE status = 1) a," +
                "goods_info b,buyer_seller c,user_info d,goods_sku e " +
                "WHERE a.goods_num = b.num AND a.sku_id = e.id AND b.seller_id = c.seller_id AND b.seller_id = d.id AND c.buyer_id = ?";

        String countSql = "SELECT DISTINCT b.id goods_id "+
                "FROM (SELECT id sku_id,goods_num,list_price price FROM goods_sku a WHERE a.status = 1 " +
                "AND NOT EXISTS (SELECT 1 FROM goods_sku_price b WHERE b.sku_id = a.id AND b.status = 1) " +
                "UNION ALL SELECT sku_id,goods_num,price FROM goods_sku_price WHERE status = 1) a," +
                "goods_info b,buyer_seller c,user_info d,goods_sku e " +
                "WHERE a.goods_num = b.num AND a.sku_id = e.id AND b.seller_id = c.seller_id AND b.seller_id = d.id AND c.buyer_id = ?";

         /*
        判断是否根据分类查找商品
         */
        if (category_id != null && category_id > 0) {
            sql = sql + " AND b.category_id=" + category_id;
            countSql = countSql + " AND b.category_id=" + category_id;
        }
        /*
        判断是否根据商品名称模糊搜索
         */
        if (goods_name != null && !"".equals(goods_name)) {
            sql = sql + " AND b.name like '%" + goods_name + "%'";
            countSql = countSql + " AND b.name like '%" + goods_name + "%'";
        }
        if (sort != null) {
            //是否按新品排序 目前是否为新品都是根据上架时间倒序查询
            if (sort == ConstantsUtils.SORT_NEW) {
                sql = sql + " ORDER BY e.release_date DESC";
            } else {
                sql = sql + " ORDER BY e.release_date DESC";
            }
        }
        FullPage<goods_info> list = goods_info.dao.fullPaginate(page_start / page_step + 1,
                page_step, sql, buyer_id);
        List countList = goods_info.dao.find(countSql,buyer_id);
        HashMap<Long,GoodsInfo> map = new HashMap<Long, GoodsInfo>();
        if (list!=null && list.getList().size()>0) {
            for (goods_info info:list.getList()){
                GoodsInfo goodsInfo = map.get(info.get("goods_id"));
                if(goodsInfo==null){
                    goodsInfo = new GoodsInfo();
                    goodsInfo.setGoods_id(info.<Long>get("goods_id"));
                    goodsInfo.setGoods_name(info.get("goods_name").toString());
                    goodsInfo.setGoods_num(info.<Long>get("goods_num"));
                    if(info.get("main_pic_url")!=null){
                        goodsInfo.setMain_pic_url(info.get("main_pic_url").toString());
                    }
                    goodsInfo.setProducer(info.get("producer").toString());
                    goodsInfo.setIngredient(info.get("ingredient").toString());
                    goodsInfo.setSeller_id(info.<Long>get("seller_id"));
                    goodsInfo.setSeller_name(info.get("seller_name").toString());

                    List<GoodsSku> skuList = (List)goodsInfo.getGoods_sku_list();
                    if(skuList==null){
                        skuList = new ArrayList<GoodsSku>();
                    }
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(info.<Long>get("sku_id"));
                    goodsSku.setSku_name(info.get("sku_name").toString());
                    goodsSku.setPrice(info.<BigDecimal>get("price"));
                    skuList.add(goodsSku);

                    goodsInfo.setGoods_sku_list(skuList);
                }else{
                    List<GoodsSku> skuList = (List)goodsInfo.getGoods_sku_list();
                    if(skuList==null){
                        skuList = new ArrayList<GoodsSku>();
                    }
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(info.<Long>get("sku_id"));
                    goodsSku.setSku_name(info.get("sku_name").toString());
                    goodsSku.setPrice(info.<BigDecimal>get("price"));
                    skuList.add(goodsSku);

                    goodsInfo.setGoods_sku_list(skuList);
                }
                map.put(info.<Long>get("goods_id"),goodsInfo);
            }
        }
        List<GoodsInfo> goodsList = new ArrayList<GoodsInfo>();
        if(map!=null && map.size()>0){
            for(Long goodsId:map.keySet()){
                GoodsInfo info = map.get(goodsId);
                if (info!=null){
                    goodsList.add(info);
                }
            }
        }
        resultMap.put("goods_list",goodsList);
        if(countList!=null){
            resultMap.put("total_count", countList.size());
        } else {
            resultMap.put("total_count", 0);
        }
        return resultMap;
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
