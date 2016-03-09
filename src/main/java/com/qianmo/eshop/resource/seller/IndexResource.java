package com.qianmo.eshop.resource.seller;


import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.credit.credit;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.user.user_info;

import java.math.BigDecimal;
import java.util.HashMap;


/**
 *
 * <p>
 * 卖家首页api
 * Created by zhangyang on 16-03-02
 * </p>
 */
@API("/total")
public class IndexResource extends SellerResource {
  private long seller_id = SessionUtil.getUserId();

  /**
   *
   * 获取首页汇总信息
   *
   */
  @GET
  public HashMap getIndexSummary() {
    HashMap resultMap = new HashMap();
    HashMap total = new HashMap();
    try {
      if(seller_id ==0) {
        resultMap.put("total",null);
        return resultMap;
      }
      //零售商数量
      long cartNum = cart.dao.findFirst("select count(*) cn from buyer_seller where seller_id = ?", seller_id).<Long>get("cn");
      //赊账零售商数量
      long cancelStatusNum = credit.dao.findFirst("select count(*) cn from credit where seller_id = ? and status = ?",seller_id, ConstantsUtils.CREDIT_CANCEL_STATUS).<Long>get("cn");
      //赊账总数量
      double cancelSum =  new credit().getTotalPriceBySellerIdStatus(seller_id,ConstantsUtils.CREDIT_CANCEL_STATUS).<BigDecimal>get("total").doubleValue();
      //客服电话
      String phone = user_info.dao.findById(seller_id).get("phone");
      //出售中的商品
      long sellingGoods = getGoodsBySellIdStatus(seller_id,ConstantsUtils.GOODS_SELLING);
      //今日订单数
      long orderNum = order_info.dao.findFirst("select count(*) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CANCEL).<Long>get("cn");
      //今日交易额
      double totalPrice = order_info.dao.findFirst("select sum(total_price) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CANCEL).<Long>get("cn");
      //待发货订单数
      long waitSendOrders = getOrderInfoBySellingStatus(seller_id, ConstantsUtils.ORDER_INFO_STATUS_WAIT_RECEIVE);
      //待收款订单数
      long waitPayOrders = getOrderInfoBySellingStatus(seller_id, ConstantsUtils.ORDER_INFO_STATUS_CREATED);
      //待出售商品
      long waitSellGoods = getGoodsBySellIdStatus(seller_id,ConstantsUtils.GOODS_WAIT_SELL);
      //零售商数量
      total.put("buyer_count",cartNum);
      //赊账零售商数量
      total.put("credit_buyer_count",cancelStatusNum);
      //赊账总数量
      total.put("credit_buyer_price",cancelSum);
      //客服电话
      total.put("phone",phone);
      //出售中的商品
      total.put("selling_goods_count",sellingGoods);
      //今日订单数
      total.put("today_order_count",orderNum);
      //今日交易额
      total.put("today_order_price",totalPrice);
      //待发货订单数
      total.put("todo_delivery_count",waitSendOrders);
      //待付款订单数
      total.put("todo_pay_count",waitPayOrders);
      //待出售商品
      total.put("waiting_goods_count",waitSellGoods);
      resultMap.put("total",total);
      return resultMap;
    } catch (Exception e) {
      //异常情况，按理说需要记录日志 TODO
      resultMap.put("total",null);
      return resultMap;
    }
  }

  private Long getOrderInfoBySellingStatus(long seller_id,int status) {
    return order_info.dao.findFirst("select count(*) cn from order_info where seller_id = ?  and status = ? ", seller_id,status).<Long>get("cn");
  }

  private Long getGoodsBySellIdStatus(long seller_id,int status) {
    return goods_sku.dao.findFirst("select count(*) cn from goods_sku where seller_id = ? and status = ?", seller_id, status).<Long>get("cn");
  }

}