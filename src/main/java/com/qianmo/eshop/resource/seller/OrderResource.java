package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.common.util.Maper;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.PUT;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.credit.credit;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.order.order_goods;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.resource.z_common.ApiResource;
import org.apache.poi.ss.formula.functions.T;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 买家获取单个订单详情   和   操作订单
 * author:wss
 * 传入参数：
 */

@API("/order")
public class OrderResource extends SellerResource {
    //单个订单详情
    @GET("/:id")
    public HashMap getList(int id) {
        HashMap result = new HashMap();
        try {

            String sqlorder_info = YamlRead.getSQL("getFieldOrderInfoAll","seller/order");
            String sqlorder_remark = YamlRead.getSQL("getFirldOrderRemarkAll","seller/order");
//商品信息
            HashMap result2 =  new HashMap();
            /*String sql2_1 = YamlRead.getSQL("getFirldGoodsInfoAll","seller/order");
            String sql2_2 = YamlRead.getSQL("getFieldGoodsSkuListAll","seller/order");
            String sql2_3 = YamlRead.getSQL("getFieldGoodsTypeALL","seller/order");
            result2.put("goods_info", goods_info.dao.find(sql2_1,id));
            result2.put("goods_sku_list", goods_sku.dao.find(sql2_2,id));
            result2.put("goods_type", goods_type.dao.find(sql2_3,id));*/
            com.qianmo.eshop.resource.buyer.OrderResource resource = new com.qianmo.eshop.resource.buyer.OrderResource();
            List<HashMap> resultMap = resource.getOrderHashMaps(id);
//用户信息
            HashMap result_buyer_info = new HashMap();
            String sqlbuyer_info = YamlRead.getSQL("getFieldBuyerInfoAll","seller/order");
            String sqlbuyer_receive = YamlRead.getSQL("getFieldBuyerReceiveAll","seller/order");
            order_user o = new order_user();
            if(order_user.dao.find(sqlbuyer_info,id)!=null && order_user.dao.find(sqlbuyer_info,id).size()>0){
                o = order_user.dao.find(sqlbuyer_info,id).get(0);
            }
            result_buyer_info.put("buyer_id",o.get("buyer_id"));
            result_buyer_info.put("name",o.get("name"));
            result_buyer_info.put("buyer_receive", buyer_receive_address.dao.find(sqlbuyer_receive,id));

            result.put("buyer_info",result_buyer_info);
            result.put("goods_list",resultMap);
            result.put("order_info",order_info.dao.find(sqlorder_info,id));
            result.put("order_remark_list", order_remark.dao.find(sqlorder_remark,id));
            return result;
        }catch (Exception e){
            return  null;
        }
    }

    //卖家操作订单
    @PUT
    public WebResult opOrder(Integer id, int op, String remark){
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
