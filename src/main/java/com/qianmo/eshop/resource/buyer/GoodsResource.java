package com.qianmo.eshop.resource.buyer;

import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.bean.goods.GoodsInfo;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;

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
    public HashMap goods(String goods_name, Integer category_id,Integer parent_category_id,
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
        FullPage<goods_info> goodsNumList = goods_info.dao.getGoodsNumList(goods_name, category_id,parent_category_id,page_start, page_step, buyer_id);
        String goodsNum = "";
        if (goodsNumList!=null && goodsNumList.getList().size()>0){//非空判断
            for (goods_info goods:goodsNumList.getList()){
                if ("".equals(goodsNum)){
                    goodsNum = goods.get("goods_num").toString();
                }else{
                    goodsNum = goodsNum + "," + goods.get("goods_num");
                }
            }
        }
        List<GoodsInfo> goodsInfoList = goods_info.dao.goodsInfoList(goodsNum,sort,buyer_id);

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
        goods_info goodsInfo = goods_info.dao.findGoodsInfo(id);
        resultMap.put("goods_info", goodsInfo);
        List<goods_sku> list = goods_sku.dao.getGoodsSku(buyer_id,goodsInfo.<Long>get("goods_num"));
        resultMap.put("goods_sku_list", list);
        return resultMap;
    }

    /**
     * 根据关键字联想商品名称
     * @param goods_name
     * @return
     */
    @GET("/name")
    public HashMap goodsName(String goods_name) {
        HashMap resultMap = new HashMap();
        int page_start = ConstantsUtils.DEFAULT_PAGE_START; //默认从第1条开始
        int page_step = ConstantsUtils.DEFAULT_PAGE_STEP;  //默认返回10条
        FullPage goodsNameList = goods_info.dao.getGoodsName(goods_name,page_start,page_step,buyer_id);
        if (goodsNameList != null){
            resultMap.put("goods_name_list", goodsNameList.getList());
        } else {
            resultMap.put("goods_name_list", "");
        }
        return resultMap;
    }


    /**
     * 获取商品列表
     *
     * @param goods_name  商品名称
     * @param category_id 商品分类ID
     * @param page_start  第几页开始
     * @param page_step   返回多少条
     * @param seller_id   卖家id
     * @return
     */
    @GET("/sellerGoods")
    public HashMap getSellergoods(String goods_name, Integer category_id,Integer parent_category_id,
                         Integer page_start, Integer page_step,long seller_id) {
        /*
        判断是否有分页信息，如果没有，给定默认值
         */
        if (page_start == null) {
            page_start = ConstantsUtils.DEFAULT_PAGE_START; //默认从第1条开始
        }
        if (page_step == null) {
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;  //默认返回10条
        }

        String goodsNum = "";
        //查询商品编号
        FullPage<goods_info> goodsNumList = goods_info.dao.findSellerGoodsNumList(goods_name,category_id,
                parent_category_id,page_start,page_step,seller_id,buyer_id);
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
        List<GoodsInfo> goodsInfoList = goods_info.dao.goodsInfoList(goodsNum,ConstantsUtils.SORT_NEW,buyer_id);
        HashMap resultMap = new HashMap();
        resultMap.put("goods_list",goodsInfoList);//商品信息
        resultMap.put("total_count", goodsNumList.getTotalRow());//商品总条数
        return resultMap;
    }


}
