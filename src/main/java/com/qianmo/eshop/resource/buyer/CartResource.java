package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.route.annotation.*;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;


/**
 *
 * <p>
 * 购物车api
 * Created by zhangyang on 16-03-01
 * </p>
 */
@API("/cart")
public class CartResource extends BuyerResource {
   private long buyer_id = SessionUtil.getUserId();

  /**
   *
   * 删除购物车商品，此处是用买家id+商品+商品型号id来联合删除的，实际上也可以用购物车id来做删除
   * @param goods_id 商品id
   * @param goods_sku_id 商品型号id
   */
  @DELETE
  public WebResult deleteCartGoods(long goods_id, int goods_sku_id) {
    try {
      if (buyer_id != 0 && goods_id != 0 && goods_sku_id != 0) {
        cart.dao.deleteBy("buyer_id = ?  and goods_num = ? and goods_sku_id = ?", buyer_id, goods_id, goods_sku_id);
        return new WebResult(HttpStatus.OK, "删除购物车商品成功");
      } else {
        return new WebResult(HttpStatus.EXPECTATION_FAILED, "输入参数有误");
      }
    } catch (Exception e) {
        //异常情况，按理说需要记录日志 TODO
      return new WebResult(HttpStatus.EXPECTATION_FAILED, "删除购物车商品失败");
    }
  }


  /**
   *
   *  添加商品到购物车
   * @param goods 商品 array<Object>
   */
  @POST
  public WebResult addCartGoods(List<JSONObject> goods) {
    try {
      //List<cart> carts = new ArrayList<cart>();
      if(goods != null && goods.size() > 0) {
        for(JSONObject good : goods) {
          cart tempCart = new cart();
          //买家id
          tempCart.set("buyer_id",buyer_id);
          //商品id
          tempCart.set("goods_num",good.get("goods_num"));
          //商品型号id
          tempCart.set("goods_sku_id",good.get("goods_sku_id"));
          //订购数量
          tempCart.set("goods_sku_count",good.get("goods_sku_count"));
          //卖家id
          tempCart.set("seller_id",good.get("seller_id"));
          //卖家name
          tempCart.set("seller_name",good.get("seller_name"));
          goods_sku   goodsSku = goods_sku.dao.findById(good.get("goods_sku_id"));
          //商品规格状态
          tempCart.set("status",goodsSku.get("status"));
          //区域id
          tempCart.set("area_id",ConstantsUtils.ALL_AREA_ID);
          cart.dao.save(tempCart);
         // carts.add(tempCart);
        }
       //框架不支持批量保存
        //cart.dao.save(carts);
        return new WebResult(HttpStatus.OK, "添加商品到购物车成功");
      } else {
        return new WebResult(HttpStatus.OK, "输入参数有误");
      }
    } catch (Exception e) {
      //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
      return new WebResult(HttpStatus.EXPECTATION_FAILED, "添加商品到购物车失败");
    }
  }

  /**
   *
   *  编辑购物车
   * @param cart_id 购物车id
   * @param count  订购数量
   */
  @PUT
  public WebResult updateCartGoods(long cart_id, int count) {
    try {
      if (buyer_id != 0 && cart_id != 0 && count != 0) {
        cart.dao.update("update cart set goods_sku_count = ?  where id = ? and buyer_id = ?", count, cart_id, buyer_id);
        return new WebResult(HttpStatus.OK, "编辑购物车成功");
      } else {
        return new WebResult(HttpStatus.EXPECTATION_FAILED, "输入参数有误");
      }
    } catch (Exception e) {
      //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
      return new WebResult(HttpStatus.EXPECTATION_FAILED, "编辑购物车失败");
    }
  }


  /**
   *
   * 获取用户购物车信息列表
   *
   */
  @GET
  public HashMap getCartList() {
    HashMap resultMap = new HashMap();
    List<Map> resultCartList = new ArrayList<Map>();
    try {
      //group by 有利于组装新的数据结构，如果去掉 order by 下面的整个逻辑都需要变动
      List<cart> cartlist = cart.dao.findBy("buyer_id = ?  order by seller_id,goods_num",buyer_id);
      Map cartResult = new HashMap();

      if(cartlist != null && cartlist.size() > 0) {
        for(cart tempCart : cartlist) {
          //如果卖家id之前已经存在了，那么表示在同一个cart里
          if (cartResult.get("seller_id") != null  &&   cartResult.get("seller_id")  == tempCart.get("seller_id") ) {
            Long tempCartGoodsNum = tempCart.get("goods_num");
            List<JSONObject>  goods_infoList =  (List<JSONObject>)cartResult.get("goods_list");
           // List<JSONObject> goodInfoList = new ArrayList<JSONObject>();
            boolean goodsNumIsExist = false;
            for(JSONObject goodsInfoTemp : goods_infoList) {
              long goodsNum = (Long)((JSONObject)goodsInfoTemp.get("goods_info")).get("goods_num");
              if(tempCartGoodsNum == goodsNum)  {
                goodsNumIsExist = true;
                //如果卖家id和商品id已经存在了，那么表示需要往型号list里加型号
                List<JSONObject> goosSkuTempList = (List<JSONObject>)((JSONObject)goodsInfoTemp.get("goods_info")).get("goods_sku_list");
                JSONObject goodssku = getGoods_sku(buyer_id, tempCart);
                goosSkuTempList.add(goodssku);
                ((JSONObject)goodsInfoTemp.get("goods_info")).put("goods_sku_list",goosSkuTempList);
              }
            }
            //如果只是卖家id一致，表示已经是不同的商品了，那么需要往商品list里加
            JSONObject goods = new JSONObject();
            if(!goodsNumIsExist) {
              getGoodsInfo(buyer_id, goods, tempCart);
              goods_infoList.add(goods);
              cartResult.put("goods_list",goods_infoList);
            }
          } else {
            //如果卖家id之前都不存在，意味着是全新的一个cart
            List<JSONObject> goodInfoList = new ArrayList<JSONObject>();
            JSONObject goods = new JSONObject();
            cartResult = new HashMap();
            //如果卖家和商品编号都一样的话，那么去将商品规格信息收集起来
            getGoodsInfo(buyer_id, goods, tempCart);
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
        resultMap.put("cart_list",resultCartList);
        return resultMap;
      } else {
          return resultMap;
      }

    } catch (Exception e) {
      //异常情况，按理说需要记录日志 TODO
      resultMap.put("cart_list",null);
      return resultMap;
    }
  }

  private void getGoodsInfo(long buyer_id, JSONObject goods, cart tempCart) {
    goods_info goods_infotemp = goods_info.dao.findFirstBy("num = ?", tempCart.get("goods_num"));
    JSONObject jsonObject = new JSONObject();
    List<JSONObject> goodsskulist = new ArrayList<JSONObject>();
    JSONObject goodssku = getGoods_sku(buyer_id, tempCart);
    goodsskulist.add(goodssku);
    //商品型号列表
    jsonObject.put("goods_sku_list", goodsskulist);
    //商品id
    jsonObject.put("goods_num", goods_infotemp.get("num"));
    //商品名称
    jsonObject.put("goods_name", goods_infotemp.get("name"));
    //商品主图
    jsonObject.put("main_pic_url",goods_infotemp.get("main_pic_url"));
    //商品生产厂家
    jsonObject.put("producer",goods_infotemp.get("producer"));
    goods.put("goods_info", jsonObject);
  }

  private JSONObject getGoods_sku(long buyer_id, cart tempCart) {
    long goodsSkuId = tempCart.<Long>get("goods_sku_id");
    goods_sku goodssku = goods_sku.dao.findById(goodsSkuId);
    JSONObject jsonObject = new JSONObject();
    //购物车id
    jsonObject.put("cart_id", tempCart.get("id"));
    //购买数量
    String skuCount = tempCart.get("goods_sku_count") == null ? "0" : tempCart.get("goods_sku_count").toString();
    jsonObject.put("count", tempCart.get("goods_sku_count"));
    //单价
    goods_sku_price goodsskuprice = goods_sku_price.dao.findFirstBy("goods_num = ? and sku_id = ? and buyer_id = ?", tempCart.get("goods_num"), tempCart.get("goods_sku_id"), buyer_id);
    BigDecimal skuPrice = goodsskuprice.get("price") == null ? new BigDecimal("0") : goodsskuprice.<BigDecimal>get("price");
    jsonObject.put("price", goodsskuprice.get("price"));
    //小计
    jsonObject.put("single_total_price", new BigDecimal(skuCount).multiply(skuPrice).doubleValue());
    //规格id
    jsonObject.put("sku_id", goodssku.get("id"));
    //规格名称
    jsonObject.put("sku_name", goodssku.get("name"));
    goods_sku_price goodsSkuPrice = goods_sku_price.dao.findFirstBy(" sku_id = ? and buyer_id = ?", goodsSkuId,buyer_id);
    //商品下架或者不可购买，都是属于下架状态
    if (goodssku.<Long>get("status") ==0 || (goodsSkuPrice != null && goodsSkuPrice.<Long>get("status") == 0)) {
      jsonObject.put("status", 0);
    } else {
      jsonObject.put("status", 1);
    }
    return jsonObject;
  }
}