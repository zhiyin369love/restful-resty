package com.qianmo.eshop.resource.buyer;


import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;
import com.qianmo.eshop.model.user.user_info;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 购物车api
 * Created by zhangyang on 16-03-01
 * </p>
 */
@API("/cart")
public class CartResource extends BuyerResource {
    private long buyer_id = SessionUtil.getUserId();

    /**
     * 删除购物车商品，此处是用买家id+商品+商品型号id来联合删除的，实际上也可以用购物车id来做删除
     *
     * @param goods_id     商品id
     * @param goods_sku_id 商品型号id
     */
    @DELETE
    @Transaction
    public Map deleteCartGoods(long goods_id, int goods_sku_id) {
        // try {
        if (buyer_id != 0 && goods_id != 0 && goods_sku_id != 0) {
            cart.dao.deleteBy("buyer_id = ?  and goods_num = ? and goods_sku_id = ?", buyer_id, goods_id, goods_sku_id);
            return setResult("删除购物车商品成功");
        } else {
            return setResult("输入参数有误");
        }
   /* } catch (Exception e) {
      return new WebResult(HttpStatus.BAD_REQUEST, "删除购物车商品失败");
    }*/
    }


    /**
     * 添加商品到购物车
     *
     * @param goods 商品 array<Object>
     */
    @POST
    @Transaction
    public Map addCartGoods(List<JSONObject> goods) {
        //try
        List<cart> carts = new ArrayList<cart>();
        if (goods != null && goods.size() > 0) {
            for (JSONObject good : goods) {
                cart tempCart = new cart();
                //买家id
                tempCart.set("buyer_id", buyer_id);
                //商品id
                tempCart.set("goods_num", good.get("goods_num"));
                //商品型号id
                tempCart.set("goods_sku_id", good.get("goods_sku_id"));
                //订购数量
                tempCart.set("goods_sku_count", good.get("goods_sku_count"));
                //卖家id
                long seller_id = (Long)good.get("seller_id");
                tempCart.set("seller_id", seller_id);
                //卖家name
                tempCart.set("seller_name", user_info.dao.findById(seller_id).get("name"));
                goods_sku goodsSku = goods_sku.dao.findById(good.get("goods_sku_id"));
                //商品规格状态
                tempCart.set("status", goodsSku.get("status"));
                //区域id
                tempCart.set("area_id", ConstantsUtils.ALL_AREA_ID);
                //cart.dao.save(tempCart);
                carts.add(tempCart);
            }
            cart.dao.save(carts);
            return setResult("添加商品到购物车成功");
        } else {
            return setResult("输入参数有误");
        }
/*    } catch (Exception e) {
      return new WebResult(HttpStatus.EXPECTATION_FAILED, "添加商品到购物车失败");
    }*/
    }

    /**
     * 编辑购物车
     *
     * @param cart_id 购物车id
     * @param count   订购数量
     */
    @PUT
    @Transaction
    public Map updateCartGoods(long cart_id, int count) {
        // try {
        if (buyer_id != 0 && cart_id != 0 && count != 0) {
            cart.dao.update("update cart set goods_sku_count = ?  where id = ? and buyer_id = ?", count, cart_id, buyer_id);
            return setResult("编辑购物车成功");
        } else {
            return setResult("输入参数有误");
        }
    /*} catch (Exception e) {
      return new WebResult(HttpStatus.EXPECTATION_FAILED, "编辑购物车失败");
    }*/
    }


    /**
     * 获取用户购物车信息列表
     */
    @GET
    public Map getCartList() {
        HashMap resultMap = new HashMap();
        List<Map> resultCartList = new ArrayList<Map>();
        // try
        List<cart> cartlist = cart.dao.findBy("buyer_id = ?  order by seller_id,goods_num", buyer_id);
        Map cartResult = new HashMap();
        goods_sku_price goodsSkuPriceModel = new goods_sku_price();
        if (cartlist != null && cartlist.size() > 0) {
            for (cart tempCart : cartlist) {
                //如果卖家id之前已经存在了，那么表示在同一个cart里
                if (cartResult.get("seller_id") != null && cartResult.get("seller_id") == tempCart.get("seller_id")) {
                    Long tempCartGoodsNum = tempCart.get("goods_num");
                    List<JSONObject> goods_infoList = (List<JSONObject>) cartResult.get("goods_list");
                    // List<JSONObject> goodInfoList = new ArrayList<JSONObject>();
                    boolean goodsNumIsExist = false;
                    for (JSONObject goodsInfoTemp : goods_infoList) {
                        long goodsNum = (Long) ((JSONObject) goodsInfoTemp.get("goods_info")).get("goods_num");
                        if (tempCartGoodsNum == goodsNum) {
                            goodsNumIsExist = true;
                            //如果卖家id和商品id已经存在了，那么表示需要往型号list里加型号
                            List<JSONObject> goosSkuTempList = (List<JSONObject>) ((JSONObject) goodsInfoTemp.get("goods_info")).get("goods_sku_list");
                            JSONObject goodssku = getGoods_sku(buyer_id, tempCart,goodsSkuPriceModel);
                            goosSkuTempList.add(goodssku);
                            ((JSONObject) goodsInfoTemp.get("goods_info")).put("goods_sku_list", goosSkuTempList);
                        }
                    }
                    //如果只是卖家id一致，表示已经是不同的商品了，那么需要往商品list里加
                    JSONObject goods = new JSONObject();
                    if (!goodsNumIsExist) {
                        getGoodsInfo(buyer_id, goods, tempCart,goodsSkuPriceModel);
                        goods_infoList.add(goods);
                        cartResult.put("goods_list", goods_infoList);
                    }
                } else {
                    //如果卖家id之前都不存在，意味着是全新的一个cart
                    List<JSONObject> goodInfoList = new ArrayList<JSONObject>();
                    JSONObject goods = new JSONObject();
                    cartResult = new HashMap();
                    //如果卖家和商品编号都一样的话，那么去将商品规格信息收集起来
                    getGoodsInfo(buyer_id, goods, tempCart,goodsSkuPriceModel);
                    goodInfoList.add(goods);
                    //卖家id
                    cartResult.put("seller_id", tempCart.get("seller_id"));
                    //卖家名称
                    cartResult.put("seller_name", tempCart.get("seller_name"));
                    //商品列表
                    cartResult.put("goods_list", goodInfoList);
                    resultCartList.add(cartResult);
                }
            }
            resultMap.put("cart_list", resultCartList);

        } /*else {
          return resultMap;
      }*/
        return resultMap;

   /* } catch (Exception e) {
      resultMap.put("cart_list",null);
      return resultMap;
    }*/
    }

    private void getGoodsInfo(long buyer_id, JSONObject goods, cart tempCart,goods_sku_price goodsSkuPriceModel) {
        goods_info goods_infotemp = goods_info.dao.findFirstBy("num = ?", tempCart.get("goods_num"));
        JSONObject jsonObject = new JSONObject();
        List<JSONObject> goodsskulist = new ArrayList<JSONObject>();
        JSONObject goodssku = getGoods_sku(buyer_id, tempCart,goodsSkuPriceModel);
        goodsskulist.add(goodssku);
        //商品型号列表
        jsonObject.put("goods_sku_list", goodsskulist);
        //商品id
        jsonObject.put("goods_num", goods_infotemp.get("num"));
        //商品名称
        jsonObject.put("goods_name", goods_infotemp.get("name"));
        //商品主图
        jsonObject.put("main_pic_url", goods_infotemp.get("main_pic_url"));
        //商品生产厂家
        jsonObject.put("producer", goods_infotemp.get("producer"));
        goods.put("goods_info", jsonObject);
    }

    private JSONObject getGoods_sku(long buyer_id, cart tempCart,goods_sku_price goodsSkuPriceModel) {
        long goodsSkuId = tempCart.<Long>get("goods_sku_id");
        goods_sku goodssku = goods_sku.dao.findById(goodsSkuId);
        JSONObject jsonObject = new JSONObject();
        //购物车id
        jsonObject.put("cart_id", tempCart.get("id"));
        //购买数量
        String skuCount = tempCart.get("goods_sku_count") == null ? "0" : tempCart.get("goods_sku_count").toString();
        jsonObject.put("count", tempCart.get("goods_sku_count"));
        //单价
/*        goods_sku_price goodsskuprice = goods_sku_price.dao.findFirstBy("goods_num = ? and sku_id = ? and buyer_id = ?", tempCart.get("goods_num"), tempCart.get("goods_sku_id"), buyer_id);
        BigDecimal skuPrice = goodsskuprice.get("price") == null ? new BigDecimal("0") : goodsskuprice.<BigDecimal>get("price");*/
        BigDecimal skuPrice = goodsSkuPriceModel.getSkuPrice(buyer_id,tempCart.<Long>get("seller_id"),tempCart.<Long>get("goods_sku_id"));
        jsonObject.put("price", skuPrice);
        //小计
        jsonObject.put("single_total_price", new BigDecimal(skuCount).multiply(skuPrice).doubleValue());
        //规格id
        jsonObject.put("sku_id", goodssku.get("id"));
        //规格名称
        jsonObject.put("sku_name", goodssku.get("name"));
        goods_sku_price goodsSkuPrice = goods_sku_price.dao.findFirstBy(" sku_id = ? and buyer_id = ?", goodsSkuId, buyer_id);
        //商品下架或者不可购买，都是属于下架状态
        if (goodssku.<Integer>get("status") == ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE || (goodsSkuPrice != null && goodsSkuPrice.<Integer>get("status") == ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE)) {
            jsonObject.put("status", ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE);
        } else {
            jsonObject.put("status", ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
        }
        return jsonObject;
    }

    private Map setResult(String message) {
        Map resultMap = new HashMap();
        resultMap.put("code",ConstantsUtils.HTTP_STATUS_OK_200);
        resultMap.put("message",message);
        return resultMap;
    }
}