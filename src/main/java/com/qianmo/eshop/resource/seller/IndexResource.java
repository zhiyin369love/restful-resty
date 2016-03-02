package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.POST;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.credit.credit;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 * <p>
 * 卖家首页api
 * Created by zhangyang on 16-03-02
 * </p>
 */
@API("/total")
public class IndexResource extends ApiResource {


  /**
   *
   * 获取首页汇总信息
   *
   * @param seller_id   买家用户id
   */
  @GET
  public HashMap getIndexSummary(int seller_id) {
    HashMap resultMap = new HashMap();
    HashMap total = new HashMap();
    try {
      //零售商数量
      int cartNum = cart.dao.findFirst("select count(*) cn from buyer_seller where seller_id = ?", seller_id).<Integer>get("cn");
      //赊账零售商数量
      int cancelStatusNum = credit.dao.findFirst("select count(*) cn from credit where seller_id = ? and status = ?",seller_id, ConstantsUtils.CREDIT_CANCEL_STATUS).<Integer>get("cn");
      //赊账总数量
      double cancelSum =  new credit().getTotalPriceBySellerIdStatus(seller_id,ConstantsUtils.CREDIT_CANCEL_STATUS).<BigDecimal>get("total").doubleValue();
      //客服电话
      String phone = user_info.dao.findById(seller_id).get("phone");
      //出售中的商品
      int sellingGoods = getGoodsBySellIdStatus(seller_id,ConstantsUtils.GOODS_SELLING);
      //今日订单数
      int orderNum = order_info.dao.findFirst("select count(*) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CACEL).<Integer>get("cn");
      //今日交易额
      double totalPrice = order_info.dao.findFirst("select sum(total_price) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CACEL).<Integer>get("cn");
      //待发货订单数
      int waitSendOrders = getOrderInfoBySellingStatus(seller_id, ConstantsUtils.ORDER_INFO_STATUS_WAIT_RECEIVE);
      //待付款订单数
      int waitPayOrders = getOrderInfoBySellingStatus(seller_id, ConstantsUtils.ORDER_INFO_STATUS_CREATED);
      //待出售商品
      int waitSellGoods = getGoodsBySellIdStatus(seller_id,ConstantsUtils.GOODS_WAIT_SELL);
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

  private Integer getOrderInfoBySellingStatus(int seller_id,int status) {
    return order_info.dao.findFirst("select count(*) cn from order_info where seller_id = ?  and status = ? ", seller_id,status).<Integer>get("cn");
  }

  private Integer getGoodsBySellIdStatus(int seller_id,int status) {
    return goods_info.dao.findFirst("select count(*) cn from goods_info where seller_id = ? and status = ?", seller_id, status).<Integer>get("cn");
  }

  public invite_verify_code getInviteByVerifyCode(int bindCode) {
     return new invite_verify_code().getInviteByCode(bindCode, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
  }
}