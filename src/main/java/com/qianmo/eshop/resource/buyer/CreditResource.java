package com.qianmo.eshop.resource.buyer;

import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.alibaba.fastjson.JSONObject;
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
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 获取赊账信息
 * author:wss
 * 传入参数说明：buyer_id 买家id 、page_start 第几条开始 、	page_step 返回多少条
 */
@API("/credit")
public class CreditResource extends BuyerResource {
    @GET
    public HashMap getCredit(int page_start,int page_step) {
        long buyer_id = SessionUtil.getUserId();
        HashMap resulttall_count = new HashMap();
        try {
            HashMap resultall = new HashMap();
            HashMap result = new HashMap();
            //赊账实体
            String sqlcredit = YamlRead.getSQL("getFieldCreditOrderAll","buyer/credit");
            //订单备注列表
            String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","buyer/order");
            //商品信息
            HashMap result_goods =  new HashMap();
            List<credit>  CreditOrderList =  credit.dao.find(sqlcredit,buyer_id);
            List<HashMap> resultMap = new ArrayList<HashMap>();
            HashMap result_buyer = new HashMap();
            for (credit credit_list : CreditOrderList){
                result.clear();
                resultall.clear();
                result_goods.clear();
                //一个订单对应一个赊账
                int id = credit_list.get("id");
                int status = credit_list.get("status");
                int credit_id_list = credit_list.get("order_num");
                OrderResource resource = new OrderResource();
                List<HashMap> resultMap2 = resource.getOrderHashMaps(credit_id_list);
                //用户信息
                String sqlbuyerinfo = YamlRead.getSQL("getFieldBuyerInfoAll","buyer/order");
                String sqlbuyerreceive = YamlRead.getSQL("getFieldBuyerReceiveAll","buyer/order");
                order_user o = new order_user();
                if(order_user.dao.find(sqlbuyerinfo,buyer_id)!=null && order_user.dao.find(sqlbuyerinfo,buyer_id).size()>0){
                    o = order_user.dao.find(sqlbuyerinfo,buyer_id).get(0);
                }
                result_buyer.put("buyer_id",o.get("buyer_id"));
                result_buyer.put("name",o.get("name"));
                result_buyer.put("buyer_receive", buyer_receive_address.dao.find(sqlbuyerreceive,buyer_id));
                //一个买家对应对个订单实体
                result.put("buyer_info",result_buyer);
                result.put("goods_list",resultMap2);
                result.put("order_info", order_info.dao.find(sqlcredit,buyer_id));
                result.put("order_remark_list", order_remark.dao.find(sql4,buyer_id));
                result_goods.put("order",result);
                //赊账实体表
                FullPage<order_user> inviteCodeList  =  order_user.dao.fullPaginateBy(page_start/page_step + 1,page_step,"page_start = ? and page_step = ?",o.get("seller_id"), ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
                HashMap count =  new HashMap();
                count.put("total_count",inviteCodeList.getTotalRow());
                resultall.put("credit_id",id);
                resultall.put("credit_status",status);
                resultall.put("order",result);
                resultall.put("page_info",count);
            }

           /* String sql2_1 = YamlRead.getSQL("getFirldGoodsInfoAll","buyer/order");
            String sql2_2 = YamlRead.getSQL("getFieldGoodsSkuListAll","buyer/order");
            String sql2_3 = YamlRead.getSQL("getFieldGoodsTypeALL","buyer/order");
            result2.put("goods_info", goods_info.dao.find(sql2_1,buyer_id));
            result2.put("goods_sku_list", goods_sku.dao.find(sql2_2,buyer_id));
            result2.put("goods_type", goods_type.dao.find(sql2_3,buyer_id));*/

            //分页信息

            resulttall_count.put("credit_list",resultall);
            return resulttall_count;
        }catch (Exception e){
            return null;
        }
    }
}

