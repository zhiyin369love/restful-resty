package com.qianmo.eshop.resource.seller;

import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.POST;
import cn.dreampie.route.annotation.PUT;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 商品价格
 * Created by fxg06 on 2016/3/6.
 */
@API("/price")
public class GoodsSkuPriceResource extends GoodsResource {
    //获取用户最高权限ID
    private Long seller_id = SessionUtil.getAdminId();

    /**
     * 获取商品价格
     *
     * @param sku_id           商品规格ID
     * @param sku_price_status 商品规格状态 0：已下架 1：已上架
     * @param name             零售商公司名称或者账号
     * @param page_start       从第几条开始
     * @param page_step        返回多少条
     * @return
     */
    @GET
    public HashMap price(Long sku_id, Integer sku_price_status, String name, Integer page_start, Integer page_step) {
        /*
        判断是否有分页信息，如果没有，给定默认值
         */
        if (page_start == null)
            page_start = ConstantsUtils.DEFAULT_PAGE_START;//默认从第1条开始
        if (page_step == null)
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;//默认返回10条

        String sql = YamlRead.getSQL("findUserAndPrice", "seller/goods");
        //判断是否根据账号，公司名称及姓名模糊查询
        if (name != null && !"".equals(name)) {
            sql = sql + " AND (a.username like '%" + name + "%' or a.nickname like '%"
                    + name + "%' or a.name like '%" + name + "%')";
        }
        FullPage<goods_sku_price> userList = goods_sku_price.dao.fullPaginate(page_start / page_step + 1, page_step, sql, sku_id, sku_price_status, seller_id);
        HashMap resultMap = new HashMap();
        resultMap.put("goods_price", userList.getList());
        resultMap.put("total_count", userList.getTotalRow());
        return resultMap;
    }

    /**
     * 批量修改商品价格
     *
     * @param goods_price_list 商品价格列表
     * @param goods_num        商品编号
     * @return
     */
    @POST("/batch")
    @Transaction
    public HashMap edit(List<JSONObject> goods_price_list, Long goods_num) {
        //判断参数列表是否为空
        if (goods_price_list != null && goods_price_list.size() > 0) {
            for (JSONObject obj : goods_price_list) {
                //sku_price_id不为空时修改价格，否则新增
                if (obj.get("sku_price_id") != null) {
                    goods_sku_price.dao.updateColsBy("price,status", "id=?",
                            obj.get("price"), obj.get("price_status"), obj.get("sku_price_id"));
                } else {
                    goods_sku_price sku_price = new goods_sku_price();
                    sku_price.set("area_id", ConstantsUtils.ALL_AREA_ID);
                    sku_price.set("price", obj.get("price"));
                    sku_price.set("status", obj.get("price_status"));
                    sku_price.set("sku_id", obj.get("sku_id"));
                    sku_price.set("buyer_id", obj.get("buyer_id"));
                    sku_price.set("seller_id", seller_id);
                    sku_price.set("goods_num", goods_num);
                    sku_price.save();
                }
            }
            return CommonUtils.getCodeMessage(true, "修改商品价格成功");
        } else {
            return CommonUtils.getCodeMessage(false, "修改商品价格失败");
        }
    }

    /**
     * 获取经销商下单个商品所有用户可购买不可购买总数
     *
     * @param sku_id 商品规格ID
     * @return
     */
    @GET("/count")
    public List count(Long sku_id) {
        List list = new ArrayList();
        //查询经销商下所有零售商总数
        long userCount = buyer_seller.dao.queryFirst(YamlRead.getSQL("findAllUserCount", "seller/goods"), seller_id);
        //查询不可购买的零售商总数
        long count = goods_sku_price.dao.queryFirst(YamlRead.getSQL("findPriceCount", "seller/goods"), seller_id, sku_id);
        /*
        不可购买
         */
        HashMap notBuyMap = new HashMap();
        notBuyMap.put("status", ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE);
        notBuyMap.put("count", 1);
        list.add(notBuyMap);
        /*
        可购买
         */
        HashMap buyMap = new HashMap();
        buyMap.put("status", ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
        buyMap.put("count", userCount - count);
        list.add(buyMap);

        return list;
    }

    /**
     * 修改商品默认价格
     *
     * @param sku_id 商品规格ID
     * @param price  价格
     * @return
     */
    @PUT
    public HashMap edit(Long sku_id, BigDecimal price) {
        //查询商品规格信息
        goods_sku sku = goods_sku.dao.findById(sku_id);
        //判断商品规格是否为空，不为空时修改价格
        if (sku != null) {
            sku.set("list_price", price);
            sku.update();
            return CommonUtils.getCodeMessage(true, "修改商品默认价格成功");
        } else {
            return CommonUtils.getCodeMessage(false, "修改商品默认价格失败");
        }
    }
}
