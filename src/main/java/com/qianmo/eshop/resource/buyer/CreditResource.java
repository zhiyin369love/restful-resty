package com.qianmo.eshop.resource.buyer;

import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_type;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.model.credit.credit;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.HashMap;

/**
 * 获取赊账信息
 * author:wss
 * 传入参数说明：buyer_id 买家id 、page_start 第几条开始 、	page_step 返回多少条
 */
@API("buyer/credit")
public class CreditResource extends ApiResource {
    @GET
    public HashMap getCredit(int buyer_id,int page_start,int 	page_step) {

        HashMap resulttall_count = new HashMap();
        try {
            HashMap resultall = new HashMap();
            HashMap result = new HashMap();
            //订单+赊账实体
            String sqlcredit = YamlRead.getSQL("getFieldCreditOrderAll","buyer/credit");
            //订单备注列表
            String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","seller/order");
            //商品信息
            HashMap result2 =  new HashMap();
            String sql2_1 = YamlRead.getSQL("getFirldGoodsInfoAll","seller/order");
            String sql2_2 = YamlRead.getSQL("getFieldGoodsSkuListAll","seller/order");
            String sql2_3 = YamlRead.getSQL("getFieldGoodsTypeALL","seller/order");
            result2.put("goods_info", goods_info.dao.find(sql2_1,buyer_id));
            result2.put("goods_sku_list", goods_sku.dao.find(sql2_2,buyer_id));
            result2.put("goods_type", goods_type.dao.find(sql2_3,buyer_id));
            //用户信息
            HashMap result3 = new HashMap();
            String sql1_1 = YamlRead.getSQL("getFieldBuyerInfoAll","buyer/order");
            String sql1_2 = YamlRead.getSQL("getFieldBuyerReceiveAll","buyer/order");
            order_user o = new order_user();
            if(order_user.dao.find(sql1_1,buyer_id)!=null && order_user.dao.find(sql1_1,buyer_id).size()>0){
                o = order_user.dao.find(sql1_1,buyer_id).get(0);
            }
            result3.put("buyer_id",o.get("buyer_id"));
            result3.put("name",o.get("name"));
            result3.put("buyer_receive", buyer_receive_address.dao.find(sql1_2,buyer_id));
            //分页信息
            FullPage<order_user> inviteCodeList  =  order_user.dao.fullPaginateBy(page_start/page_step + 1,page_step,"page_start = ? and page_step = ?",o.get("seller_id"), ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);

            result.put("buyer_info",result3);
            result.put("goods_list",result2);
            result.put("order_info", order_info.dao.find(sqlcredit,buyer_id));
            result.put("order_remark_list", order_remark.dao.find(sql4,buyer_id));

            HashMap count =  new HashMap();
            count.put("total_count",inviteCodeList.getTotalRow());
            //赊账实体表
            String sqlcre = YamlRead.getSQL("getFirldCreditAll","buyer/credit");
            credit cc = new credit();
            if(credit.dao.find(sqlcre,buyer_id) !=null && cc.dao.find(sqlcre,buyer_id).size()>0){
                cc = credit.dao.find(sqlcre,buyer_id).get(0);
            }
            resultall.put("credit_id",cc.get("id"));
            resultall.put("credit_status",cc.get("status"));
            resultall.put("order",result);
            resultall.put("page_info",count);
            resulttall_count.put("credit",resultall);
            return resulttall_count;
        }catch (Exception e){
            return null;
        }
    }
}

