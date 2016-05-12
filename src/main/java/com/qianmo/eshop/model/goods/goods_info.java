package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import cn.dreampie.orm.page.FullPage;
import com.qianmo.eshop.bean.goods.GoodsInfo;
import com.qianmo.eshop.bean.goods.GoodsSku;
import com.qianmo.eshop.common.CodeUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "goods_info")
public class goods_info extends Model<goods_info> {
    public final static goods_info dao = new goods_info();

    /**
     * 根据id查询商品
     * @param id
     * @return
     */
    public goods_info findGoodsInfo(Long id){
        goods_info goodsInfo = findFirst(YamlRead.getSQL("findGoods", "buyer/goods"), id);
        return goodsInfo;
    }

    /**
     * 根据关键字联想商品名称
     * @param goodsName  商品名称
     * @param pageStart  第几条开始
     * @param pageStep 每页多少条
     * @param buyerId 买家ID
     * @return
     */
    public FullPage getGoodsName(String goodsName,Integer pageStart,Integer pageStep,Long buyerId){
        String goodsNameSql = YamlRead.getSQL("findGoodsName","buyer/goods");
        if(goodsName!=null && !"".equals(goodsName)){
            goodsNameSql = goodsNameSql + " AND b.name like '%"+goodsName+"%'";
        }
        FullPage<goods_info> goodsNameList = fullPaginate(pageStart / pageStep + 1,pageStep,goodsNameSql, buyerId, buyerId, buyerId);
        return goodsNameList;
    }
    /**
     * 根据规格ID查询商品
     * @param buyerId 买家ID
     * @param skuId  规格ID
     * @return
     */
    public goods_info findGoodsInfo(Long buyerId,Long skuId){
        String sql = YamlRead.getSQL("findGoodsInfo", "buyer/goods");
        sql = sql + " AND e.id = ?";
        goods_info goodsInfo = findFirst(sql,buyerId,buyerId,buyerId,skuId);
        return goodsInfo;
    }

    /**
     * 获取商品编号信息
     * @param goodsName  商品名称
     * @param categoryId  商品二级分类ID
     * @param parentCategoryId  商品一级分类ID
     * @param pageStart  第几条开始
     * @param pageStep  返回多少条
     * @param buyerId  买家ID
     * @return
     */
    public FullPage getGoodsNumList(String goodsName, Integer categoryId,Integer parentCategoryId,Integer pageStart, Integer pageStep, Long buyerId){
        //查询商品编号
        String goodsNumSql = YamlRead.getSQL("findGoodsNum","buyer/goods");
         //判断是否根据分类查找商品
        if (categoryId != null && categoryId > 0) {
            goodsNumSql = goodsNumSql + " AND b.category_id=" + categoryId;
        }
        if (parentCategoryId!=null && parentCategoryId>0){
            goodsNumSql = goodsNumSql + " AND b.category_id in (SELECT id from goods_category where pid="+parentCategoryId+")";
        }
         /*
        判断是否根据商品名称模糊搜索
         */
        if (goodsName != null && !"".equals(goodsName)) {
            goodsNumSql = goodsNumSql + " AND b.name like '%" + goodsName + "%'";
        }
        //查询商品编号
        FullPage<goods_info> goodsNumList = goods_info.dao.fullPaginate(pageStart / pageStep + 1,pageStep,
                goodsNumSql, buyerId,buyerId,buyerId);
        return goodsNumList;
    }

    /**
     * 根据卖家ID获取商品编号信息
     * @param goodsName  商品名称
     * @param categoryId  商品二级分类ID
     * @param parentCategoryId  商品一级分类ID
     * @param pageStart  第几条开始
     * @param pageStep  返回多少条
     * @param sellerId 卖家ID
     * @param buyerId 买家ID
     * @return
     */
    public FullPage findSellerGoodsNumList(String goodsName, Integer categoryId,Integer parentCategoryId,
              Integer pageStart, Integer pageStep,Long sellerId,Long buyerId){
        //查询商品编号
        String goodsNumSql = YamlRead.getSQL("findSellerGoodsNum","buyer/goods");
        //判断是否根据分类查找商品
        if (categoryId != null && categoryId > 0) {
            goodsNumSql = goodsNumSql + " AND b.category_id=" + categoryId;
        }
        if (parentCategoryId!=null && parentCategoryId>0){
            goodsNumSql = goodsNumSql + " AND b.category_id in (SELECT id from goods_category where pid="+parentCategoryId+")";
        }
        //判断是否根据商品名称模糊搜索
        if (goodsName != null && !"".equals(goodsName)) {
            goodsNumSql = goodsNumSql + " AND b.name like '%" + goodsName + "%'";
        }
        //查询商品编号
        FullPage<goods_info> goodsNumList = goods_info.dao.fullPaginate(pageStart / pageStep + 1,pageStep,
                goodsNumSql, buyerId,sellerId,buyerId,sellerId,buyerId,sellerId);
        return goodsNumList;
    }

    /**
     * 买家获取商品信息
     * @param goodsNum 商品编号
     * @param sort 排序
     * @param buyerId 买家ID
     * @return
     */
    public List goodsInfoList(String goodsNum,Integer sort,Long buyerId){
        //查询商品、规格、价格
        String goodsSql = YamlRead.getSQL("findGoodsInfo","buyer/goods");
        if (sort != null) {
            //是否按新品排序 目前是否为新品都是根据上架时间倒序查询
            if (sort == ConstantsUtils.SORT_NEW) {
                goodsSql = goodsSql + " ORDER BY e.release_date DESC";
            } else {
                goodsSql = goodsSql + " ORDER BY e.release_date DESC";
            }
        }

        List<goods_info> list = null;
        //如果商品编号不为空时查询商品、规格、价格信息
        if (!"".equals(goodsNum)){
            goodsSql = goodsSql + "  AND b.num in ("+goodsNum+")";
            list = goods_info.dao.find(goodsSql,buyerId,buyerId,buyerId);
        }
        HashMap<Long,GoodsInfo> map = new HashMap<Long, GoodsInfo>();
        //非空判断
        if (list!=null && list.size()>0) {
            for (goods_info info:list){
                GoodsInfo goodsInfo = map.get(info.get("goods_id"));
                //如果商品为空，新建商品
                if(goodsInfo==null){
                    goodsInfo = buildGoodsInfo(info);
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
        List<GoodsInfo> goodsInfoList = buildGoodsList(map);
        return goodsInfoList;
    }


    /**
     * 获取商品id信息
     * @param goodsName 商品名称
     * @param goodsStatus    商品上下架状态
     * @param categoryId     商品一级分类ID
     * @param subCategoryId   商品二级分类ID
     * @param pageStart   第几条开始
     * @param pageStep    返回多少条
     * @param sellerId    卖家ID
     * @return
     */
    public FullPage getGoodsIdList(String goodsName,Integer goodsStatus,Integer categoryId,Integer subCategoryId, Integer pageStart, Integer pageStep,Long sellerId){
        String idSql = YamlRead.getSQL("findGoodsId","seller/goods");
        /*
        判断是根据一级分类查商品还是二级分类查商品
         */
        if (subCategoryId != null && subCategoryId > 0) {
            idSql = idSql + " AND a.category_id=" + subCategoryId;
        } else {
            idSql = idSql + " AND a.category_id in (SELECT id from goods_category where pid=" + categoryId + ")";
        }
        /*
        判断是否根据商品上下架状态查商品
         */
        if (goodsStatus != null) {
            idSql = idSql + " AND b.status=" + goodsStatus;
        }
        /*
        判断是否根据商品名称模糊搜索
         */
        if (goodsName != null && !"".equals(goodsName)) {
            idSql = idSql + " AND a.name like '%" + goodsName + "%'";
        }
        //查询商品ID
        FullPage<goods_info> idList = fullPaginate(pageStart / pageStep + 1, pageStep,
                idSql, sellerId);
        return idList;
    }
    /**
     * 卖家获取商品列表
     * @param goodsIds  商品id拼接的字符串
     * @param sellerId  卖家ID
     * @return
     */
    public List goodsList(String goodsIds,Long sellerId){
        String sql = YamlRead.getSQL("findGoodsInfo", "seller/goods");
        List<goods_info> list = null;
        //如果商品ID不为空时查询商品、规格、价格信息
        if (!"".equals(goodsIds)) {
            sql = sql + "  AND a.id in (" + goodsIds + ")";
            list = find(sql, sellerId);
        }
        HashMap<Long, GoodsInfo> map = new HashMap<Long, GoodsInfo>();
        //查询结果非空判断
        if (list != null && list.size() > 0) {
            for (goods_info goodsInfo : list) {
                GoodsInfo goods = map.get(goodsInfo.<Long>get("goods_id")); //
                if (goods == null) {
                    goods = buildGoodsInfo(goodsInfo);
                    //商品规格信息
                    List<GoodsSku> skuList = new ArrayList<GoodsSku>();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(goodsInfo.<Long>get("sku_id"));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(goodsInfo.<Integer>get("status"));
                    //判断是否有价格信息
                    if (goodsInfo.get("list_price") != null) {
                        goodsSku.setPrice(goodsInfo.<BigDecimal>get("list_price"));
                    }
                    //判断是否有上架时间信息
                    if (goodsInfo.get("release_date") != null
                            && goodsInfo.<Integer>get("status") == 1) {
                        goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    }
                    goodsSku.setSell_count(0);
                    //判断该商品规格是否有售出
                    if (goodsInfo.get("sell_count") != null) {
                        goodsSku.setSell_count(Integer.parseInt(goodsInfo.get("sell_count").toString()));
                    }
                    skuList.add(goodsSku);
                    goods.setGoods_sku_list(skuList);
                } else {
                    List<GoodsSku> skuList = (List) goods.getGoods_sku_list();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(goodsInfo.<Long>get("sku_id"));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(goodsInfo.<Integer>get("status"));
                    //判断是否有价格信息
                    if (goodsInfo.get("list_price") != null) {
                        goodsSku.setPrice(goodsInfo.<BigDecimal>get("list_price"));
                    }
                    //判断是否有上架时间信息
                    if (goodsInfo.get("release_date") != null
                            && goodsInfo.<Integer>get("status") == 1) {
                        goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    }
                    goodsSku.setSell_count(0);
                    //判断该商品规格是否有售出
                    if (goodsInfo.get("sell_count") != null) {
                        goodsSku.setSell_count(Integer.parseInt(goodsInfo.get("sell_count").toString()));
                    }
                    skuList.add(goodsSku);
                    goods.setGoods_sku_list(skuList);
                }
                map.put(goods.getGoods_id(), goods);
            }
        }
        //将商品Map转为商品List
        List<GoodsInfo> goodsInfoList = buildGoodsList(map);
        return goodsInfoList;
    }


    /**
     * 构造商品实体
     * @param goods
     * @return
     */
    public GoodsInfo buildGoodsInfo(goods_info goods){
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setGoods_id(goods.<Long>get("goods_id"));
        goodsInfo.setGoods_name((String)goods.get("goods_name"));
        goodsInfo.setGoods_num(goods.<Long>get("goods_num"));
        goodsInfo.setMain_pic_url((String)goods.get("main_pic_url"));
        goodsInfo.setProducer((String)goods.get("producer"));
        goodsInfo.setIngredient((String)goods.get("ingredient"));
        return goodsInfo;
    }

    /**
     * 构造商品列表
     * @param map
     * @return
     */
    public List buildGoodsList(HashMap<Long,GoodsInfo> map){
        //将商品Map转为商品List
        List<GoodsInfo> goodsInfoList = new ArrayList<GoodsInfo>();
        if (map!=null && map.size()>0){
            for(Long goodsId:map.keySet()){
                GoodsInfo info = map.get(goodsId);
                if(info != null){
                    goodsInfoList.add(info);
                }
            }
        }
        return goodsInfoList;
    }
}
