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
import java.util.Map;


/**
 * <p>
 * 卖家首页api
 * Created by zhangyang on 16-03-02
 * </p>
 */
@API("/total")
public class IndexResource extends SellerResource {
    private long seller_id = SessionUtil.getUserId();

    /**
     * 获取首页汇总信息
     */
    @GET
    public Map getIndexSummary() {
        HashMap resultMap = new HashMap();
        HashMap total = new HashMap();
        //try {
        if (seller_id == 0) {
            return setResult("输入参数有误");
        }
        //零售商数量
        long cartNum = cart.dao.findFirst("select count(*) cn from buyer_seller where seller_id = ? and status = 1", seller_id).<Long>get("cn");
        //赊账零售商数量
        long cancelStatusNum = credit.dao.findFirst("select count(distinct(buyer_id)) cn from credit where seller_id = ? and status = ?", seller_id, ConstantsUtils.CREDIT_CANCEL_STATUS).<Long>get("cn");
        //赊账总数量
        double cancelSum = new credit().getTotalPriceBySellerIdStatus(seller_id, ConstantsUtils.CREDIT_CANCEL_STATUS).<BigDecimal>get("total").doubleValue();
        //客服电话
        String phone = user_info.dao.findById(seller_id).get("phone");
        //出售中的商品
        long sellingGoods = getGoodsBySellIdStatus(seller_id, ConstantsUtils.GOODS_SELLING);
        order_info orderInfo = new order_info();
        //今日订单数
        long orderNum =  orderInfo.getDayTotalOrder(seller_id);
        //今日交易额
        BigDecimal totalPrice = orderInfo.getDayTotalPrice(seller_id);
        //待发货订单数
        long waitSendOrders = getOrderInfoBySellingStatus(seller_id, ConstantsUtils.ORDER_INFO_STATUS_WAIT_RECEIVE);
        //待收款订单数
        long waitPayOrders = getOrderInfoBySellingStatus(seller_id, ConstantsUtils.ORDER_INFO_STATUS_CREATED);
        //待出售商品
        long waitSellGoods = getNoSellGoodsBySellIdStatus(seller_id, ConstantsUtils.GOODS_SELLING);
        //零售商数量
        total.put("buyer_count", cartNum);
        //赊账零售商数量
        total.put("credit_buyer_count", cancelStatusNum);
        //赊账总数量
        total.put("credit_buyer_price", cancelSum);
        //客服电话
        total.put("phone", phone);
        //出售中的商品
        total.put("selling_goods_count", sellingGoods);
        //今日订单数
        total.put("today_order_count", orderNum);
        //今日交易额
        total.put("today_order_price", totalPrice==null?0:totalPrice);
        //待发货订单数
        total.put("todo_delivery_count", waitSendOrders);
        //待付款订单数
        total.put("todo_pay_count", waitPayOrders);
        //待出售商品
        total.put("waiting_goods_count", waitSellGoods);
        resultMap.put("total", total);
        return resultMap;
/*    } catch (Exception e) {
      resultMap.put("total",null);
      return resultMap;
    }*/
    }

    private Long getOrderInfoBySellingStatus(long seller_id, int status) {
        return order_info.dao.findFirst("select count(*) cn from order_info oi left join order_user ou on oi.num = ou.order_num where ou.seller_id = ?   and oi.status = ? ", seller_id, status).<Long>get("cn");
    }

    private Long getGoodsBySellIdStatus(long seller_id, int status) {
        return goods_sku.dao.findFirst("select count(DISTINCT a.num) cn from goods_info a where 1=1 and EXISTS (select 1 from  goods_sku b where b.goods_num = a.num and  b.seller_id = ? and b.status = ? AND b.deleted_at IS NULL) AND a.deleted_at is null", seller_id, status).<Long>get("cn");
    }

    private Long getNoSellGoodsBySellIdStatus(long seller_id, int status) {
        return goods_sku.dao.findFirst("select count(DISTINCT a.num) cn FROM goods_info a LEFT JOIN goods_sku b ON a.num = b.goods_num  where 1=1 and  Not EXISTS (select 1 from  goods_sku b where b.goods_num = a.num and  b.seller_id = ? and b.status = ? AND b.deleted_at IS NULL)  AND b.seller_id = ?  AND a.deleted_at is null", seller_id, status, seller_id).<Long>get("cn");
    }

    private Map setResult(String message) {
        Map resultMap = new HashMap();
        resultMap.put("code",ConstantsUtils.HTTP_STATUS_OK_200);
        resultMap.put("message",message);
        return resultMap;
    }

}