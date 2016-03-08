package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.DELETE;
import cn.dreampie.route.annotation.PUT;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.credit.credit;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.resource.z_common.ApiResource;
import com.qianmo.eshop.resource.buyer.CartResource;

import java.util.HashMap;
import java.util.List;

/**
 * 卖家操作订单
 * author:wss
 * id 订单编号，年月日时分秒 + 4位随机数
 * op 必填 0收到货款 1发货 2取消 3卖家备注订单 4同意买家赊账 5不同意买家赊账
 * remark   操作备注
 */
@API("/order")
public class OrderOpResource extends SellerResource {
    @PUT
    public WebResult opOrder(Integer id,int op,String remark){
        try {
            if (op == ConstantsUtils.SELLER_ORDER_OP_PAY_TYPE){
                //收到货款
                order_info.dao.update("update order_info set pay_status = ?  where num = ? ", ConstantsUtils.ORDER_PAYMENT_STATUS_RECEIVED, id);
            }else if(op == ConstantsUtils.SELLER_ORDER_OP_FAHUO){
                //发货
                order_info.dao.update("update order_info set status = ?  where num = ? ", ConstantsUtils.ORDER_INFO_STATUS_WAIT_RECEIVE, id);
                new order_remark().set("order_num",id).set("op",op).set("details",remark).save();
            }else if (op == ConstantsUtils.SELLER_ORDER_OP_PAY_STATUS){
                //取消
                order_info.dao.update("update order_info set status = ?  where id = ? ", ConstantsUtils.ORDER_INFO_STATUS_CACEL, id);
                new order_remark().set("order_num",id).set("op",op).set("details",remark).save();
            }else if (op == ConstantsUtils.SELLER_ORDER_OP_PAY_GOODS){
                //卖家备注订单
                new order_remark().set("order_num",id).set("op",op).set("details",remark).save();
            }else if(op == ConstantsUtils.SELLER_ORDER_OP_PAY_CELL){

                //当卖家同意买家赊账时

                HashMap result3 = new HashMap();
                String creditorder = YamlRead.getSQL("getFileCreditOrderUserAll","seller/order");
                order_user o = new order_user();
                if(order_user.dao.find(creditorder,id)!=null && order_user.dao.find(creditorder,id).size()>0){
                    o = order_user.dao.find(creditorder,id).get(0);
                }
                /*result3.put("buyer_id",o.get("buyer_id"));
                result3.put("seller_id",o.get("seller_id"));*/
                new credit().set("area_id",ConstantsUtils.ALL_AREA_ID).set("order_num",id).set("status",0).set("buyer_id",o.get("buyer_id")).set("seller_id",o.get("seller_id")).save();
            }else {
                //当卖家不同意买家赊账时订单取消 op==5时
                order_info.dao.update("update order_info set status = ? where id = ? ",ConstantsUtils.ORDER_INFO_STATUS_CACEL,id);    //注：除了要删除订单主表之外，可能还要删除其他关联表，“待开发”
                return new WebResult(HttpStatus.OK, "删除订单成功");
            }
            return new WebResult(HttpStatus.OK, "操作订单成功");
        } catch (Exception e) {
            //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "操作订单失败");
        }
    }

}
