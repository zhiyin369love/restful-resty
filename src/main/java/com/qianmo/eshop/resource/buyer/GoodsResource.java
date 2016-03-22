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
        /*
        判断是否有分页信息，如果没有，给定默认值
         */
        if (page_start == null) {
            page_start = ConstantsUtils.DEFAULT_PAGE_START; //默认从第1条开始
        }
        if (page_step == null) {
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;  //默认返回10条
        }
        //查询商品编号
        String goodsNumSql = YamlRead.getSQL("findGoodsNum","buyer/goods");
        //查询商品、规格、价格
        String goodsSql = YamlRead.getSQL("findGoodsInfo","buyer/goods");
         /*
        判断是否根据分类查找商品
         */
        if (category_id != null && category_id > 0) {
            goodsNumSql = goodsNumSql + " AND b.category_id=" + category_id;
        }
        /*
        判断是否根据商品名称模糊搜索
         */
        if (goods_name != null && !"".equals(goods_name)) {
            goodsNumSql = goodsNumSql + " AND b.name like '%" + goods_name + "%'";
        }
        if (sort != null) {
            //是否按新品排序 目前是否为新品都是根据上架时间倒序查询
            if (sort == ConstantsUtils.SORT_NEW) {
                goodsSql = goodsSql + " ORDER BY e.release_date DESC";
            } else {
                goodsSql = goodsSql + " ORDER BY e.release_date DESC";
            }
        }
        String goodsNum = "";
        //查询商品编号
        FullPage<goods_info> goodsNumList = goods_info.dao.fullPaginate(page_start / page_step + 1,page_step, goodsNumSql, buyer_id);
        //非空判断
        if (goodsNumList!=null && goodsNumList.getList().size()>0){
             for(goods_info goods:goodsNumList.getList()){
                 if("".equals(goodsNum)){
                     goodsNum = goods.get("goods_num").toString();
                 } else {
                     goodsNum = goodsNum + "," + goods.get("goods_num");
                 }
             }
        }
        List<goods_info> list = null;
        //如果商品编号不为空时查询商品、规格、价格信息
        if (!"".equals(goodsNum)){
            goodsSql = goodsSql + "  AND b.num in ("+goodsNum+")";
            list = goods_info.dao.find(goodsSql,buyer_id);
        }
        HashMap<Long,GoodsInfo> map = new HashMap<Long, GoodsInfo>();
        //非空判断
        if (list!=null && list.size()>0) {
            for (goods_info info:list){
                GoodsInfo goodsInfo = map.get(info.get("goods_id"));
                //如果商品为空，新建商品
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
                }

                //商品规格及价格信息
                List<GoodsSku> skuList = (List)goodsInfo.getGoods_sku_list();
                /*
                商品规格价格集合为空时，新建商品规格价格集合
                将查询的商品规格及价格信息存入集合中
                 */
                if(skuList==null){
                    skuList = new ArrayList<GoodsSku>();
                }
                GoodsSku goodsSku = new GoodsSku();
                goodsSku.setSku_id(info.<Long>get("sku_id"));
                goodsSku.setSku_name(info.get("sku_name").toString());
                goodsSku.setPrice(info.<BigDecimal>get("price"));
                skuList.add(goodsSku);
                goodsInfo.setGoods_sku_list(skuList);
                //将商品存入map中
                map.put(info.<Long>get("goods_id"),goodsInfo);
            }
        }
        //将商品Map转为商品List
        List<GoodsInfo> goodsInfoList = new ArrayList<GoodsInfo>();
        if(map!=null && map.size()>0){
            for(Long goodsId:map.keySet()){
                GoodsInfo info = map.get(goodsId);
                if (info!=null){
                    goodsInfoList.add(info);
                }
            }
        }
        HashMap resultMap = new HashMap();
        resultMap.put("goods_list",goodsInfoList);//商品信息
        resultMap.put("total_count", goodsNumList.getTotalRow());//商品总条数
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
