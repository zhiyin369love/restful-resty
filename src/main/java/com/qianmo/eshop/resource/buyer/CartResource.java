package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.DELETE;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.POST;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.resource.z_common.ApiResource;

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
  @POST
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
}