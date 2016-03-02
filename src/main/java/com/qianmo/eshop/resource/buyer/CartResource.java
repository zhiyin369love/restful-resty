package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.route.annotation.*;
import com.qianmo.eshop.common.ConstantsUtils;
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


/**
 *
 * <p>
 * 购物车api
 * Created by zhangyang on 16-03-01
 * </p>
 */
@API("/cart")
public class CartResource extends ApiResource {


  /**
   *
   * 删除购物车商品，此处是用买家id+商品+商品型号id来联合删除的，实际上也可以用购物车id来做删除
   * @param buyer_id   买家用户id
   * @param goods_id 商品id
   * @param goods_sku_id 商品型号id
   */
  @DELETE
  public WebResult deleteCartGoods(int buyer_id, int goods_id, int goods_sku_id) {
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
   * @param buyer_id   买家用户id
   * @param goods 商品 array<Object>
   */
  @POST
  public WebResult addCartGoods(int buyer_id, List<goods_sku> goods) {
    try {
      List<cart> carts = new ArrayList<cart>();
      for(goods_sku good : goods) {
        cart tempCart = new cart();
        //买家id
        tempCart.set("buyer_id",buyer_id);
        //商品id
        tempCart.set("goods_num",good.get("goods_id"));
        //商品型号id
        tempCart.set("goods_sku_id",good.get("goods_sku_id"));
        //订购数量
        tempCart.set("goods_sku_count",good.get("goods_count"));
        //区域id
        tempCart.set("area_id",ConstantsUtils.ALL_AREA_ID);
        carts.add(tempCart);
      }
      cart.dao.save(carts);
      return new WebResult(HttpStatus.OK, "添加商品到购物车成功");
    } catch (Exception e) {
      //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
      return new WebResult(HttpStatus.EXPECTATION_FAILED, "添加商品到购物车失败");
    }
  }

  /**
   *
   *  编辑购物车
   * @param buyer_id   买家用户id
   * @param cart_id 购物车id
   * @param count 订购数量
   */
  @PUT
  public WebResult updateCartGoods(int buyer_id, int cart_id, int count) {
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
   * @param buyer_id   买家用户id
   */
  @GET
  public HashMap getCartList(int buyer_id) {
    HashMap resultMap = new HashMap();

    try {
      List<cart> cartlist = cart.dao.findBy("buyer_id = ?  group by seller_id,goods_num",buyer_id);
      for(cart tempCart : cartlist) {
        cart cartResult = new cart();
        List<goods_info> goodsInfolist = new ArrayList<goods_info>();
        goods_info goods = goods_info.dao.findFirstBy("num = ?",tempCart.get("goods_num"));
        //如果卖家和商品编号都一样的话，那么去将商品规格信息收集起来
        if (cartResult.get("seller_id") != null && cartResult.<Long>get("seller_id") == tempCart.<Long>get("seller_id")
                && cartResult.get("goods_num") != null && cartResult.<Long>get("goods_num") == tempCart.<Long>get("goods_num")) {

        }


        //卖家id
        if (cartResult.get("seller_id") != null && cartResult.<Long>get("seller_id") == tempCart.<Long>get("seller_id")) {

          cartResult.<List<goods_info>>get("goods_list").add(goods);
        } else {
          cartResult.set("seller_id",tempCart.get("seller_id"));
          cartResult.set("goods_num",tempCart.get("goods_num"));
          goods_info goods_infotemp = goods_info.dao.findFirstBy("num = ?",tempCart.get("goods_num"));
          List<goods_sku> goodsskulist = new ArrayList<goods_sku>();
          goods_sku goodssku = goods_sku.dao.findById(tempCart.get("goods_sku_id"));
          //购物车id
          goodssku.set("cart_id",tempCart.get("id"));
          //购买数量
          goodssku.set("count",tempCart.get("goods_sku_count"));
          //单价
          goods_sku_price goodsskuprice = goods_sku_price.dao.findFirstBy("goods_num = ? and sku_id = ? and buyer_id = ?",tempCart.get("goods_num"),tempCart.get("goods_sku_id"),buyer_id);
          BigDecimal skuPrice = goodsskuprice.get("price");
          goodssku.set("price",goodsskuprice.get("price"));
          //小计


          //规格id
          goodssku.set("sku_id",goodssku.get("id"));
          //规格名称
          goodssku.set("sku_name",goodssku.get("name"));
          goodsskulist.add(goodssku);
          goods_infotemp.set("goods_sku_list",goodsskulist);
          goods.set("goods_info",goods_infotemp);
          goodsInfolist.add(goods);
          cartResult.set("goods_list",goodsInfolist);
        }
        cartResult.set("seller_id",tempCart.get("seller_id"));
        //卖家名称
        cartResult.set("seller_name",tempCart.get("seller_name"));
        //商品编号
        long goodsNum = tempCart.<Long>get("goods_num");
        cartResult.set("goods_num",goodsNum);




        //商品型号id
        long goodsSkuId = tempCart.get("goods_sku_id");
        cartResult.set("goods_sku_id",goodsSkuId);
        //订购数量
        cartResult.set("goods_sku_count",tempCart.get("goods_sku_count"));
        //购物车ID
        cartResult.set("cart_id",tempCart.get("id"));


      }
     // resultMap.put("cart_list",cartMap);
      return resultMap;
    } catch (Exception e) {
      //异常情况，按理说需要记录日志 TODO
      resultMap.put("cart_list",null);
      return resultMap;
    }
  }

}