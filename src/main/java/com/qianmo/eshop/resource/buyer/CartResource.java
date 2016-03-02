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
import java.util.Map;


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
    List<Map> resultCartList = new ArrayList<Map>();

    try {
      //group by 有利于组装新的数据结构，如果去掉 group by 下面的整个逻辑都需要变动
      List<cart> cartlist = cart.dao.findBy("buyer_id = ?  group by seller_id,goods_num",buyer_id);
      Map cartResult = new HashMap();
      goods_info goods = null;
      for(cart tempCart : cartlist) {
        //如果卖家id之前已经存在了，那么表示在同一个cart里
        if (cartResult.get("seller_id") != null  &&   cartResult.get("seller_id")  == tempCart.get("seller_id") ) {
          Long tempCartGoodsNum = tempCart.get("goods_num");
          List<goods_info>  goods_infoList =  (List<goods_info>)cartResult.get("goods_list");
          boolean goodsNumIsExist = false;
          for(goods_info goodsInfoTemp : goods_infoList) {
              long goodsNum = goodsInfoTemp.<goods_info>get("goods_info").<Long>get("goods_id");
              if(tempCartGoodsNum == goodsNum)  {
                goodsNumIsExist = true;
                //如果卖家id和商品id已经存在了，那么表示需要往型号list里加型号
                List<goods_sku> goosSkuTempList = goodsInfoTemp.get("goods_sku_list");
                goods_sku goodssku = getGoods_sku(buyer_id, tempCart);
                goosSkuTempList.add(goodssku);
                break;
             }
          }
          //如果只是卖家id一致，表示已经是不同的商品了，那么需要往商品list里加
          if(!goodsNumIsExist) {
            getGoodsInfo(buyer_id, goods, tempCart);
            goods_infoList.add(goods);
          }
        } else {
          //如果卖家id之前都不存在，意味着是全新的一个cart
          List<goods_info> goodsInfolist = new ArrayList<goods_info>();
          cartResult = new HashMap();
          //如果卖家和商品编号都一样的话，那么去将商品规格信息收集起来
          getGoodsInfo(buyer_id, goods, tempCart);
          goodsInfolist.add(goods);
          //卖家id
          cartResult.put("seller_id", tempCart.get("seller_id"));
          //卖家名称
          cartResult.put("seller_name", tempCart.get("seller_name"));
          //商品列表
          cartResult.put("goods_list", goodsInfolist);
          resultCartList.add(cartResult);
        }
      }
     resultMap.put("cart_list",resultCartList);
      return resultMap;
    } catch (Exception e) {
      //异常情况，按理说需要记录日志 TODO
      resultMap.put("cart_list",null);
      return resultMap;
    }
  }

  private void getGoodsInfo(int buyer_id, goods_info goods, cart tempCart) {
    goods = new goods_info();
    goods_info goods_infotemp = goods_info.dao.findFirstBy("num = ?", tempCart.get("goods_num"));
    List<goods_sku> goodsskulist = new ArrayList<goods_sku>();
    goods_sku goodssku = getGoods_sku(buyer_id, tempCart);
    goodsskulist.add(goodssku);
    //商品型号列表
    goods_infotemp.set("goods_sku_list", goodsskulist);

    //商品id
    goods_infotemp.set("goods_id", goods_infotemp.get("goods_num"));
    //商品名称
    goods_infotemp.set("goods_name", goods_infotemp.get("name"));
    //商品主图,商品货号，商品生产厂家都已经存在goods_infotemp中了。
    goods.set("goods_info", goods_infotemp);
  }

  private goods_sku getGoods_sku(int buyer_id, cart tempCart) {
    goods_sku goodssku = goods_sku.dao.findById(tempCart.get("goods_sku_id"));
    //购物车id
    goodssku.set("cart_id", tempCart.get("id"));
    //购买数量
    String skuCount = tempCart.get("goods_sku_count") == null ? "0" : tempCart.get("goods_sku_count").toString();
    goodssku.set("count", tempCart.get("goods_sku_count"));
    //单价
    goods_sku_price goodsskuprice = goods_sku_price.dao.findFirstBy("goods_num = ? and sku_id = ? and buyer_id = ?", tempCart.get("goods_num"), tempCart.get("goods_sku_id"), buyer_id);
    BigDecimal skuPrice = goodsskuprice.get("price") == null ? new BigDecimal("0") : goodsskuprice.<BigDecimal>get("price");
    goodssku.set("price", goodsskuprice.get("price"));
    //小计
    goodssku.set("single_total_price", new BigDecimal(skuCount).multiply(skuPrice).doubleValue());
    //规格id
    goodssku.set("sku_id", goodssku.get("id"));
    //规格名称
    goodssku.set("sku_name", goodssku.get("name"));
    return goodssku;
  }

}