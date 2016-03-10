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
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.buyer.*;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.qianmo.eshop.resource.buyer.OrderResource;

/**
 * Created by Administrator on 2016/3/3 0003.
 */
@API("/credit")
public class CreditResource extends SellerResource {
    @GET
    public HashMap getCredit(int page_start, int page_step, int show_type, Integer status) {
        long seller_id = SessionUtil.getUserId();
        //long buyer_id = SessionUtil.getUserId();
        HashMap all = new HashMap();
        HashMap result_buyer_credit = new HashMap();
        List<HashMap> creditsList = new ArrayList<HashMap>();
        if (show_type == 0) {
            //用户信息
            List<credit> order_users_list = new ArrayList<credit>();
            HashMap result_buyerinfo = new HashMap();
            List<HashMap> resultMapBuyer = new ArrayList<HashMap>();
            String sqlbuyer_info = YamlRead.getSQL("getFieldBuyerInfoAll", "seller/credit");
            //分页
            FullPage<credit> creditFullPagelist = null;
            if (status != null) {
                sqlbuyer_info = sqlbuyer_info + "and c.status = ?";
               // order_users_list = credit.dao.find(sqlbuyer_info, seller_id, status);
                creditFullPagelist = credit.dao.fullPaginate(page_start/page_step + 1,page_step,sqlbuyer_info,seller_id,status);
            } else {
                creditFullPagelist = credit.dao.fullPaginate(page_start/page_step + 1,page_step,sqlbuyer_info,seller_id); //获取一个卖家对应的所有买家
            }
            if (creditFullPagelist != null)
            for (credit credit_list : creditFullPagelist.getList()) {
                long buyer_id_list = credit_list.get("buyer_id");
                String users_buyer_name = YamlRead.getSQL("getFieldUserInfoAll", "seller/credit");
                List<user_info> order_name_list = user_info.dao.find(users_buyer_name, buyer_id_list);
                user_info o = new user_info();
                if (order_name_list != null && order_name_list.size() > 0) {
                    o = order_name_list.get(0);
                }
                result_buyerinfo.put("name", o.get("name"));
                result_buyerinfo.put("buyer_id", buyer_id_list);
                resultMapBuyer.add(result_buyerinfo);

                String total_order_count = YamlRead.getSQL("getFirldCountOrderUserAll", "seller/credit");
                String total_price_count = YamlRead.getSQL("getFirldCountPriceOrderInfoAll", "seller/credit");
                //订单买家汇总赊账金额
                order_info order_info_list = new order_info();
                order_info order_info_count = new order_info();
                order_info credit_order_id = order_info.dao.findFirst(total_price_count, buyer_id_list, seller_id);
                order_info credit_order_count = order_info.dao.findFirst(total_order_count, buyer_id_list, seller_id);

                String sqlcre = YamlRead.getSQL("getFirldCreditAll", "seller/credit");
                credit order_name_list_credit = credit.dao.findFirst(sqlcre, seller_id);
                result_buyer_credit.put("total_order_count", order_info_count.get("num"));
                result_buyer_credit.put("total_price", order_info_list.get("total_price"));
                result_buyer_credit.put("buyer_info", resultMapBuyer);
                result_buyer_credit.put("credit_id", order_name_list_credit.get("id"));

                result_buyer_credit.put("status", order_name_list_credit.get("status"));
                creditsList.add(result_buyer_credit);

            }
            //分页
           // FullPage<credit> inviteCodeList = credit.dao.fullPaginateBy(page_start / page_step + 1, page_step, "id = ?", seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
            all.put("total_count", creditsList.size());

            all.put("credit_list", creditsList);
        } else if (show_type == 1) {
            HashMap resultall = new HashMap();
            HashMap result_buyer_info = new HashMap();
            HashMap result_goods_order = new HashMap();
            HashMap result_goods_list = new HashMap();
            //List<credit> creditOrderList = new ArrayList<credit>();
            //分页
            FullPage<credit> creditFullPagelist = null;
            String sqlcre = YamlRead.getSQL("getFirldSellerCreditAll", "seller/credit");
            if (status != null) {   //判断订单赊账是否
                sqlcre = sqlcre + "c.status = ?";
                //creditOrderList = credit.dao.find(sqlcre, seller_id, status);
                creditFullPagelist = credit.dao.fullPaginate(page_start/page_step + 1,page_step,sqlcre,seller_id,status);
            } else {
                creditFullPagelist = credit.dao.fullPaginate(page_start/page_step + 1,page_step,sqlcre,seller_id);
            }
            if (creditFullPagelist != null){
                for (credit credit_list : creditFullPagelist.getList()) {
                    //一个订单对应一个赊账
                    int id = credit_list.get("id");
                    int seller_status = credit_list.get("status");
                    int credit_id_list = credit_list.get("order_num");
                    OrderResource resource = new com.qianmo.eshop.resource.buyer.OrderResource();
                    List<HashMap> resultMap2 = resource.getOrderHashMaps(credit_id_list);
                    result_goods_order.put("goods_list", result_goods_list);                             //1
                    //买家信息实体
                    String sql_buyer_info = YamlRead.getSQL("getFieldBuyerInfoAll", "seller/order");
                    String sql_buyer_receive = YamlRead.getSQL("getFieldBuyerReceiveAll", "seller/order");
                    order_user o = order_user.dao.findFirst(sql_buyer_info, seller_id);

                    result_buyer_info.put("buyer_id", o.get("buyer_id"));
                    result_buyer_info.put("name", o.get("name"));
                    result_buyer_info.put("buyer_receive", buyer_receive_address.dao.find(sql_buyer_receive, seller_id));
                    result_goods_order.put("buyer_info", result_buyer_info);                        //2
                    //订单实体
                    String sql3 = YamlRead.getSQL("getFieldOrderInfoAll", "seller/order");
                    result_goods_order.put("order_info", order_info.dao.find(sql3, seller_id));   //3
                    //订单备注
                    String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll", "seller/order");
                    result_goods_order.put("order_remark_list", order_remark.dao.find(sql4, seller_id));  //4

                    //多个订单实体
                    result_buyer_credit.put("order", result_goods_order);
                    result_buyer_credit.put("credit_id", id);
                    result_buyer_credit.put("status", seller_status);
                    creditsList.add(result_buyer_credit);
                }
            }
            all.put("total_count", creditsList.size());
            all.put("credit_list", creditsList);

        }
        return all;
    }

    //销账
    @PUT
    public WebResult opOrder(List<credit> credits) {

            for (credit c2 : credits) {
                if (c2.get("buyer_id") != null) {
                    credit.dao.update("update credit set status = ?  where buyer_id = ? ", ConstantsUtils.CREDIT_ALREADY_STATUS, c2.get("buyer_id"));
                } else if (c2.get("credit_id") != null) {
                    credit.dao.update("update credit set status = ?  where id = ? ", ConstantsUtils.CREDIT_ALREADY_STATUS, c2.get("credit_id"));
                }
            }
            return new WebResult(HttpStatus.OK, "操作赊账成功");
    }
}

