package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.PUT;
import com.qianmo.eshop.common.ConstantsUtils;
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
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/3/3 0003.
 */
@API("/credit")
public class CreditResource extends SellerResource {
    @GET
    public HashMap getCredit(int buyer_id,int page_start,int 	page_step,int seller_id,int show_type,int status) {
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
     }else if (show_type == 1){
         HashMap resultall = new HashMap();

         HashMap result3 = new HashMap();
         HashMap result4 = new HashMap();
         HashMap result5 =  new HashMap();


         String sqlcre = YamlRead.getSQL("getFirldCreditAll","seller/credit");
         credit cc = new credit();
         if(credit.dao.find(sqlcre,seller_id) !=null && cc.dao.find(sqlcre,seller_id).size()>0){
             cc = credit.dao.find(sqlcre,seller_id).get(0);
         }
         result2.put("credit_id",cc.get("id"));
         result2.put("status",cc.get("status"));
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
         result4.put("buyer_info",result3);
      //商品实体列表
         String sql2_1 = YamlRead.getSQL("getFirldGoodsInfoAll","seller/order");
         String sql2_2 = YamlRead.getSQL("getFieldGoodsSkuListAll","seller/order");
         String sql2_3 = YamlRead.getSQL("getFieldGoodsTypeALL","seller/order");
         result5.put("goods_info", goods_info.dao.find(sql2_1,buyer_id));
         result5.put("goods_sku_list", goods_sku.dao.find(sql2_2,buyer_id));
         result5.put("goods_type", goods_category.dao.find(sql2_3,buyer_id));
         result4.put("goods_list",result5);
      //订单实体
         String sql3 = YamlRead.getSQL("getFieldOrderInfoAll","buyer/order");
         result4.put("order_info",order_info.dao.find(sql3,seller_id));
         String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","seller/order");
         result4.put("order_remark_list", order_remark.dao.find(sql4,seller_id));

         result2.put("order",result4);
      //分页
         FullPage<order_user> inviteCodeList  =  order_user.dao.fullPaginateBy(page_start/page_step + 1,page_step,"page_start = ? and page_step = ?",o.get("seller_id"), ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
         HashMap count =  new HashMap();
         count.put("total_count",inviteCodeList.getTotalRow());
         result2.put("page_info",count);

     }
        return  result2;
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

