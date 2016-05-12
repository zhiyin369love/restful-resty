package com.qianmo.eshop.resource.buyer;


import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.buyer.buyer_seller;
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
     * @param cart_id 购物车id
     */
    @DELETE("/:cart_id")
    @Transaction
    public Map deleteCartGoods(Long cart_id) {
        // try {
        if (buyer_id != 0 && cart_id != null) {
            cart.dao.deleteById(cart_id);
            return CommonUtils.getCodeMessage(true, "删除购物车商品成功");
        } else {
            return CommonUtils.getCodeMessage(false, "输入参数有误");
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
    public Map addCartGoods(List<Map> goods) {
        //try
        List<cart> carts = new ArrayList<cart>();
        if (goods != null && goods.size() > 0) {
            for (Map good : goods) {
                goods_info goodsInfo = goods_info.dao.findGoodsInfo(buyer_id,
                        Long.parseLong(good.get("goods_sku_id").toString()));
                //判断该规格商品是否可购买
                if (goodsInfo!=null){
                    cart tempCart = new cart();
                    //买家id
                    tempCart.set("buyer_id", buyer_id);
                    //商品id
                    tempCart.set("goods_num", good.get("goods_num"));
                    //商品型号id
                    tempCart.set("goods_sku_id", good.get("goods_sku_id"));
                    cart cartTemp = cart.dao.findFirstBy(" goods_sku_id = ? and buyer_id = ? ", good.get("goods_sku_id"), buyer_id);
                    if (cartTemp != null) {
                        long cartCounts = Long.valueOf(cartTemp.get("goods_sku_count").toString()) + Long.valueOf(good.get("goods_sku_count").toString());
                        cartTemp.set("goods_sku_count", cartCounts).update();
                    } else {
                        //订购数量
                        tempCart.set("goods_sku_count", good.get("goods_sku_count"));
                        //卖家id
                        long seller_id = Long.valueOf(good.get("seller_id").toString());
                        tempCart.set("seller_id", seller_id);
                        //卖家name
                        tempCart.set("seller_name", user_info.dao.findById(seller_id).get("nickname"));
                        //商品规格状态
                        tempCart.set("status",ConstantsUtils.RELEASE_STATUS_ON);
                        //区域id
                        tempCart.set("area_id", ConstantsUtils.ALL_AREA_ID);
                    }
                    carts.add(tempCart);
                }
//                cart tempCart = new cart();
//                //买家id
//                tempCart.set("buyer_id", buyer_id);
//                //商品id
//                tempCart.set("goods_num", good.get("goods_num"));
//                //商品型号id
//                tempCart.set("goods_sku_id", good.get("goods_sku_id"));
//                cart cartTemp = cart.dao.findFirstBy(" goods_sku_id = ? and buyer_id = ? ", good.get("goods_sku_id"), buyer_id);
//                if (cartTemp != null) {
//                    long cartCounts = Long.valueOf(cartTemp.get("goods_sku_count").toString()) + Long.valueOf(good.get("goods_sku_count").toString());
//                    cartTemp.set("goods_sku_count", cartCounts).update();
//                } else {
//                    //订购数量
//                    tempCart.set("goods_sku_count", good.get("goods_sku_count"));
//                    //卖家id
//                    long seller_id = Long.valueOf(good.get("seller_id").toString());
//                    tempCart.set("seller_id", seller_id);
//                    //卖家name
//                    tempCart.set("seller_name", user_info.dao.findById(seller_id).get("nickname"));
//                    goods_sku goodsSku = goods_sku.dao.findById(good.get("goods_sku_id"));
//                    //商品规格状态
//                    tempCart.set("status", goodsSku.get("status"));
//                    //区域id
//                    tempCart.set("area_id", ConstantsUtils.ALL_AREA_ID);
//                    //cart.dao.save(tempCart);
//                    carts.add(tempCart);
//                }
            }
            if (carts.size() > 0) {
                cart.dao.save(carts);
                if(carts.size()==goods.size()){
                    return CommonUtils.getCodeMessage(true,"该订单商品已全部加入购物车");
                }else{
                    return CommonUtils.getCodeMessage(true,"该订单未下架的商品已加入购物车");
                }
            } else {
                return CommonUtils.getCodeMessage(false,"该订单商品已下架，无法加入购物车");
            }

        } else {
            return CommonUtils.getCodeMessage(false, "输入参数有误");
        }
/*    } catch (Exception e) {
      return new WebResult(HttpStatus.EXPECTATION_FAILED, "添加商品到购物车失败");
    }*/
    }

    /**
     * 编辑购物车
     *
     * @param cart_list 购物车列表
     */
    @PUT
    @Transaction
    public Map updateCartGoods(List<Map> cart_list) {
        // try {
        for (Map cartTemp : cart_list) {
            long cart_id = Long.valueOf(cartTemp.get("cart_id").toString());
            int count = Integer.valueOf(cartTemp.get("sku_count").toString());
            if (buyer_id != 0 && cart_id != 0 && count != 0) {
                cart.dao.update("update cart set goods_sku_count = ?  where id = ? and buyer_id = ?", count, cart_id, buyer_id);
            }
        }
        return setResult("编辑购物车成功");
    /*} catch (Exception e) {
      return new WebResult(HttpStatus.EXPECTATION_FAILED, "编辑购物车失败");
    }*/
    }


    /**
     * 获取用户购物车信息列表
     */
    @GET
    public Map getCartList(String cart_id_list) {
        HashMap resultMap = new HashMap();
        List<Map> resultCartList = new ArrayList<Map>();
        // try
        List<cart> cartlist;
        if (!StringUtils.isEmpty(cart_id_list)) {
            String sql = " buyer_id = ? and id in (" + cart_id_list + ") order by seller_id,goods_num";
            cartlist = cart.dao.findBy(sql, buyer_id);
        } else {
            cartlist = cart.dao.findBy("buyer_id = ?  order by seller_id,goods_num", buyer_id);
        }
        Map cartResult = new HashMap();
        goods_sku_price goodsSkuPriceModel = new goods_sku_price();
        if (cartlist != null && cartlist.size() > 0) {
            for (cart tempCart : cartlist) {
                //如果卖家id之前已经存在了，那么表示在同一个cart里
                if (cartResult.get("seller_id") != null && cartResult.get("seller_id").equals(tempCart.get("seller_id"))) {
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
                            JSONObject goodssku = getGoods_sku(buyer_id, tempCart, goodsSkuPriceModel);
                            goosSkuTempList.add(goodssku);
                            ((JSONObject) goodsInfoTemp.get("goods_info")).put("goods_sku_list", goosSkuTempList);
                        }
                    }
                    //如果只是卖家id一致，表示已经是不同的商品了，那么需要往商品list里加
                    JSONObject goods = new JSONObject();
                    if (!goodsNumIsExist) {
                        getGoodsInfo(buyer_id, goods, tempCart, goodsSkuPriceModel);
                        goods_infoList.add(goods);
                        cartResult.put("goods_list", goods_infoList);
                    }
                } else {
                    //如果卖家id之前都不存在，意味着是全新的一个cart
                    List<JSONObject> goodInfoList = new ArrayList<JSONObject>();
                    JSONObject goods = new JSONObject();
                    cartResult = new HashMap();
                    //如果卖家和商品编号都一样的话，那么去将商品规格信息收集起来
                    getGoodsInfo(buyer_id, goods, tempCart, goodsSkuPriceModel);
                    goodInfoList.add(goods);
                    //卖家id
                    cartResult.put("seller_id", tempCart.get("seller_id"));
                    //卖家名称
                    String SellerName = user_info.dao.findById(tempCart.get("seller_id")).get("nickname");
                    cartResult.put("seller_name", SellerName);
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



    /**
     * 获取用户购物车信息列表
     */
    @GET("/skuCount")
    public Map getCartSkuCount() {
        HashMap resultMap = new HashMap();
        String sql = "select count(*) cn from cart where buyer_id = ? ";
        Long count = cart.dao.findFirst(sql,buyer_id).<Long>get("cn");
        resultMap.put("count",count == null ? 0 : count);
        return resultMap;
    }

    private void getGoodsInfo(long buyer_id, JSONObject goods, cart tempCart, goods_sku_price goodsSkuPriceModel) {
        goods_info goods_infotemp = goods_info.dao.findFirstBy("num = ?", tempCart.get("goods_num"));
        JSONObject jsonObject = new JSONObject();
        List<JSONObject> goodsskulist = new ArrayList<JSONObject>();
        JSONObject goodssku = getGoods_sku(buyer_id, tempCart, goodsSkuPriceModel);
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

    private JSONObject getGoods_sku(long buyer_id, cart tempCart, goods_sku_price goodsSkuPriceModel) {
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
        BigDecimal skuPrice = goodsSkuPriceModel.getSkuPrice(buyer_id, tempCart.<Long>get("seller_id"), tempCart.<Long>get("goods_sku_id"));
        jsonObject.put("price", skuPrice);
        //小计
        jsonObject.put("single_total_price", new BigDecimal(skuCount).multiply(skuPrice).doubleValue());
        //规格id
        jsonObject.put("sku_id", goodssku.get("id"));
        //规格名称
        jsonObject.put("sku_name", goodssku.get("name"));
        goods_sku_price goodsSkuPrice = goods_sku_price.dao.findFirstBy(" sku_id = ? and buyer_id = ?", goodsSkuId, buyer_id);
        Long selerId = tempCart.get("seller_id");
        Integer status = buyer_seller.dao.findFirstBy("buyer_id = ? and seller_id = ?",buyer_id,selerId).<Integer>get("status");
        //商品下架或者不可购买，都是属于下架状态
        if (status.equals(ConstantsUtils.BUYER_SELLER_STATUS_BIDING_CANCEL) || goodssku.<Integer>get("status").equals(ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE) || (goodsSkuPrice != null && goodsSkuPrice.<Integer>get("status").equals(ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE))) {
            jsonObject.put("status", ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE);
        } else {
            jsonObject.put("status", ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
        }
        return jsonObject;
    }

    private Map setResult(String message) {
        Map resultMap = new HashMap();
        resultMap.put("code", ConstantsUtils.HTTP_STATUS_OK_200);
        resultMap.put("message", message);
        return resultMap;
    }
}