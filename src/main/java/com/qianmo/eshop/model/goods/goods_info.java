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
     * 根据规格ID查询商品
     * @param buyerId 买家ID
     * @param skuId  规格ID
     * @return
     */
    public goods_info findGoodsInfo(Long buyerId,Long skuId){
        String sql = YamlRead.getSQL("findGoodsInfo", "buyer/goods");
        sql = sql + " AND e.id = ?";
        goods_info goodsInfo = goods_info.dao.findFirst(sql,buyerId,buyerId,buyerId,skuId);
        return goodsInfo;
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
                    goods = new GoodsInfo();
                    goods.setGoods_id(goodsInfo.<Long>get("goods_id"));
                    goods.setGoods_name(goodsInfo.get("goods_name").toString());
                    goods.setGoods_num(goodsInfo.<Long>get("goods_num"));
                    //判断是否有主图
                    if (goodsInfo.get("main_pic_url") != null) {
                        goods.setMain_pic_url(goodsInfo.get("main_pic_url").toString());
                    }
                    goods.setProducer(goodsInfo.get("producer").toString());
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
        List<GoodsInfo> goodsInfoList = new ArrayList<GoodsInfo>();
        //非空判断
        if (map != null && map.size() > 0) {
            for (Long goodsId : map.keySet()) {
                goodsInfoList.add(map.get(goodsId));
            }
        }
        return goodsInfoList;
    }

}
