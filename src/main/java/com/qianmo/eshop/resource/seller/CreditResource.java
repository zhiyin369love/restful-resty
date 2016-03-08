package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.PUT;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.model.credit.credit;
import com.qianmo.eshop.resource.buyer.*;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.HashMap;
import java.util.List;
import com.qianmo.eshop.resource.buyer.OrderResource;
/**
 * Created by Administrator on 2016/3/3 0003.
 */
@API("/credit")
public class CreditResource extends SellerResource {
    @GET
    public HashMap getCredit(int page_start,int page_step,int show_type,int status) {
        long seller_id = SessionUtil.getUserId();
       long buyer_id = SessionUtil.getUserId();
        HashMap all = new HashMap();
        HashMap result2 = new HashMap();
     if(show_type == 0){
         //用户信息
         HashMap result3 = new HashMap();
         String sql1_1 = YamlRead.getSQL("getFieldBuyerInfoAll","seller/order");
         order_user o = new order_user();
         if(order_user.dao.find(sql1_1,buyer_id)!=null && order_user.dao.find(sql1_1,buyer_id).size()>0){
             o = order_user.dao.find(sql1_1,buyer_id).get(0);
         }
         result3.put("buyer_id",o.get("buyer_id"));
         result3.put("name",o.get("name"));

         FullPage<order_user> inviteCodeList  =  order_user.dao.fullPaginateBy(page_start/page_step + 1,page_step,"page_start = ? and page_step = ?",o.get("seller_id"), ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
         HashMap count =  new HashMap();
         count.put("total_count",inviteCodeList.getTotalRow());

         String sqlcre = YamlRead.getSQL("getFirldCreditAll","seller/credit");
         credit cc = new credit();
         if(credit.dao.find(sqlcre,seller_id) !=null && cc.dao.find(sqlcre,seller_id).size()>0){
             cc = credit.dao.find(sqlcre,seller_id).get(0);
         }
         String total_order_count = YamlRead.getSQL("getFirldCountOrderUserAll","seller/cart");
         String total_price_count = YamlRead.getSQL("getFirldCountPriceOrderInfoAll","seller/cart");

         result2.put("buyer_info",result3);
         result2.put("credit_id",cc.get("id"));
         result2.put("page_info",count);
         result2.put("status",cc.get("status"));
         result2.put("total_order_count",order_user.dao.find(total_order_count,seller_id));
         result2.put("total_price",order_info.dao.find(total_price_count,buyer_id));

         all.put("credit_list",result2);
     }else if (show_type == 1)
     {
         HashMap resultall = new HashMap();

         HashMap result3 = new HashMap();
         HashMap result4 = new HashMap();
         HashMap result5 =  new HashMap();


         String sqlcre = YamlRead.getSQL("getFirldSellerCreditAll","seller/credit");
         List<credit>  CreditOrderList =  credit.dao.find(sqlcre,seller_id);
         for (credit credit_list : CreditOrderList){
             //一个订单对应一个赊账
             int id = credit_list.get("id");
             int seller_status = credit_list.get("status");
             int credit_id_list = credit_list.get("order_num");
             OrderResource resource = new com.qianmo.eshop.resource.buyer.OrderResource();
             List<HashMap> resultMap2 = resource.getOrderHashMaps(credit_id_list);
             result4.put("goods_list",result5);                             //1
             //买家信息实体
             String sql1_1 = YamlRead.getSQL("getFieldBuyerInfoAll","seller/order");
             String sql1_2 = YamlRead.getSQL("getFieldBuyerReceiveAll","seller/order");
             order_user o = new order_user();
             if(order_user.dao.find(sql1_1,seller_id)!=null && order_user.dao.find(sql1_1,seller_id).size()>0){
                 o = order_user.dao.find(sql1_1,seller_id).get(0);
             }
             result3.put("buyer_id",o.get("buyer_id"));
             result3.put("name",o.get("name"));
             result3.put("buyer_receive", buyer_receive_address.dao.find(sql1_2,seller_id));
             result4.put("buyer_info",result3);                        //2
             //订单实体
             String sql3 = YamlRead.getSQL("getFieldOrderInfoAll","seller/order");
             result4.put("order_info",order_info.dao.find(sql3,seller_id));   //3
             //订单备注
             String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","seller/order");
             result4.put("order_remark_list", order_remark.dao.find(sql4,seller_id));  //4

             //多个订单实体
             result2.put("order",result4);
             result2.put("credit_id",id);
             result2.put("status",seller_status);
         }
         //分页
         FullPage<order_user> inviteCodeList
                 =  order_user.dao.fullPaginateBy(page_start/page_step + 1,page_step,"page_start = ? and page_step = ?",
                 seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
         result2.put("total_count",inviteCodeList.getTotalRow());
         all.put("credit_list",result2);
     }
        return  all;
    }
    @PUT
    public WebResult opOrder(List<credit> credits){
        try {
             for (credit c2 : credits){
                 if (c2.get("buyer_id")!=null) {
                     credit.dao.update("update credit set status = ?  where buyer_id = ? ", ConstantsUtils.CREDIT_ALREADY_STATUS, c2.get("buyer_id"));
                 }else if (c2.get("credit_id")!=null){
                     credit.dao.update("update credit set status = ?  where id = ? ", ConstantsUtils.CREDIT_ALREADY_STATUS, c2.get("credit_id"));
                 }
             }
            return new WebResult(HttpStatus.OK, "操作赊账成功");
        } catch (Exception e) {
            //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "操作赊账失败");
        }
    }


}

