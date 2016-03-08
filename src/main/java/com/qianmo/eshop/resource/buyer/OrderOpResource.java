package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.PUT;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.resource.z_common.ApiResource;
import com.qianmo.eshop.resource.buyer.CartResource;

import java.util.List;

/**
 * 操作订单
 * author:wss
 * 传入参数说明：
 *    bank_id ：选择的银行ID 选填 当支付方式选择银行汇款时需传此字段、buyer_id：卖家ID 必填 、 goods:商品实体 选填 当操作选择再买一次时，传入此array
 *    id:订单编号 、op:必填 0选择支付方式 1选择银行 2我已付款 3确认收货 4取消订单 5再买一次、 value：选填 操作值（取消订单时，传入订单取消原因）
 *
 */
@API("/buyer/order")
public class OrderOpResource extends ApiResource {
    @PUT
    public WebResult opOrder(Integer bank_id,int buyer_id,int id,int op,int value, List<JSONObject> goods){
        try {

              if(op == ConstantsUtils.ORDER_OP_PAY_TYPE){   // 0 选择支付方式
                  if(value==1){                               // 当支付方式选择银行支付的时候
                      if (bank_id != null){
                          order_info.dao.update("update order_info set pay_type_id = ?  where num = ? ", value, id);
                      }
                  }
              }else  if (op == ConstantsUtils.ORDER_OP_BANK){     // 1 选择银行  目前默认为农行
                  if (bank_id != null){
                      //待开发
                  }
              }else  if(op == ConstantsUtils.ORDER_OP_PAY_STATUS){    // 2 我已付款
                  order_info.dao.update("update order_info set pay_status = ?  where num = ? ",ConstantsUtils.ORDER_PAYMENT_STATUS_RECEIVED, id);
              }else  if(op == ConstantsUtils.ORDER_OP_PAY_GOODS){    // 3 确认收货
                  order_info.dao.update("update order_info set status = ?  where num = ? ", ConstantsUtils.ORDER_INFO_STATUS_FINISHED,id);

              }else if(op == ConstantsUtils.ORDER_OP_PAY_CELL){  // 4 取消订单
                 order_info.dao.update("update order_info set status = ?  where num = ? ", ConstantsUtils.ORDER_INFO_STATUS_CACEL,id);
                  new order_remark().set("order_num",id).set("op",op).set("reason",value).set("user_id",buyer_id).save();
              }else{  //5再买一次  添加一次购物车
                  CartResource  cartResource = new CartResource();
                  cartResource.addCartGoods(buyer_id,goods);
              }

            return new WebResult(HttpStatus.OK, "操作订单成功");
        } catch (Exception e) {
            //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "操作订单失败");
        }
    }

}
