package com.qianmo.eshop.resource.seller;

import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.page.Page;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.PUT;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.credit.credit;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.model.user.user_info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 赊账 和 销账
 *
 * @author wss
 */
@API("/credit")
public class CreditResource extends SellerResource {
    /**
     * @param page_start
     * @param page_step
     * @param show_type
     * @param status
     * @return
     */
    @GET
    public HashMap getCredit(Integer page_start, Integer page_step, int show_type, Integer status) {
        //System.out.print("进来");
        if (status == null || StringUtils.isEmpty(String.valueOf(status))) {
            status = ConstantsUtils.CREDIT_STATUS;
        }
        long seller_id = SessionUtil.getUserId();
        //long buyer_id = SessionUtil.getUserId();
        HashMap all = new HashMap();
        List<HashMap> creditsList = new ArrayList<HashMap>();
        FullPage<credit> creditFullPagelist;
        if (show_type == 0) {
            //用户信息
            //List<credit> order_users_list = new ArrayList<credit>();
            //List<HashMap> resultMapBuyer = new ArrayList<HashMap>();
            String sqlbuyer_info = YamlRead.getSQL("getFieldBuyerInfoAll", "seller/credit");
            //分页
            if (page_start == null || page_start == 0) {
                page_start = ConstantsUtils.DEFAULT_PAGE_START;
            }
            if (page_step == null || page_step == 0) {
                page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
            }
            int pageNumber = page_start / page_step + 1;

            //if (status != null) {
            // order_users_list = credit.dao.find(sqlbuyer_info, seller_id, status);
            creditFullPagelist = credit.dao.fullPaginate(pageNumber, page_step, sqlbuyer_info, seller_id, status);

           /* } else {
                creditFullPagelist = credit.dao.paginate(pageNumber, page_step, sqlbuyer_info, seller_id); //获取一个卖家对应的所有买家
            }*/
            if (creditFullPagelist != null && creditFullPagelist.getList() != null && creditFullPagelist.getList().size() > 0) {
                for (credit credit_list : creditFullPagelist.getList()) {
                    HashMap result_buyer_credit = new HashMap();
                    HashMap result_buyerinfo = new HashMap();
                    long buyer_id_list = credit_list.get("buyer_id");
                    String users_buyer_name = YamlRead.getSQL("getFieldUserInfoAll", "seller/credit");
                    user_info o = user_info.dao.findFirst(users_buyer_name, buyer_id_list);
                    /*user_info o = new user_info();
                    if (order_name_list != null && order_name_list.size() > 0) {
                        o = order_name_list.get(0);
                    }*/
                    result_buyerinfo.put("buyer_nickname", o.get("nickname"));
                    result_buyerinfo.put("buyer_id", buyer_id_list);
                    //resultMapBuyer.add(result_buyerinfo);

                    String total_order_count = YamlRead.getSQL("getFirldCountOrderUserAll", "seller/credit");
                    String total_price_count = YamlRead.getSQL("getFirldCountPriceOrderInfoAll", "seller/credit");
                    //订单买家汇总赊账金额
                    //  order_info order_info_list = new order_info();
                    //   order_info order_info_count = new order_info();
                    order_info credit_order_id = order_info.dao.findFirst(total_price_count, status, buyer_id_list, seller_id);
                    order_info credit_order_count = order_info.dao.findFirst(total_order_count, status, buyer_id_list, seller_id);

                    String sqlcre = YamlRead.getSQL("getFirldCreditAll", "seller/credit");
                    // credit order_name_list_credit = credit.dao.findFirst(sqlcre, seller_id);
                    result_buyer_credit.put("total_order_count", credit_order_count.get("num"));
                    result_buyer_credit.put("total_price", credit_order_id.get("total_price"));
                    result_buyer_credit.put("buyer_info", result_buyerinfo);
                    // result_buyer_credit.put("credit_id", order_name_list_credit.get("id"));
                    result_buyer_credit.put("status", status);
                    creditsList.add(result_buyer_credit);
                }
            }
            //分页
            // FullPage<credit> inviteCodeList = credit.dao.fullPaginateBy(page_start / page_step + 1, page_step, "id = ?", seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
            all.put("total_count", creditFullPagelist.getTotalRow());

            all.put("credit_list", creditsList);
        } else if (show_type == 1) {
            //分页
            //分页
            if (page_start == null || page_start == 0) {
                page_start = ConstantsUtils.DEFAULT_PAGE_START;
            }
            if (page_step == null || page_step == 0) {
                page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
            }
            int pageNumber = page_start / page_step + 1;
            String sqlcre = YamlRead.getSQL("getFirldSellerCreditAll", "seller/credit");
            //if (status != null) {   //判断订单赊账是否
            sqlcre += " and  c.status = ?  order by c.created_at desc";
            //creditOrderList = credit.dao.find(sqlcre, seller_id, status);
            creditFullPagelist = credit.dao.fullPaginate(pageNumber, page_step, sqlcre, seller_id, status);
            /*} else {
                creditFullPagelist = credit.dao.paginate(pageNumber, page_step, sqlcre, seller_id);
            }*/
            if (creditFullPagelist != null && creditFullPagelist.getList() != null && creditFullPagelist.getList().size() > 0) {
                for (credit credit_list : creditFullPagelist.getList()) {
                    HashMap result_goods_order = new HashMap();
                    //HashMap result_buyerinfo = new HashMap();
                    HashMap result_buyer_info = new HashMap();
                    HashMap result_buyer_credit = new HashMap();
                    //一个订单对应一个赊账
                    long id = credit_list.get("id");
                    int seller_status = credit_list.get("status");
                    long credit_id_list = credit_list.get("order_num");
                    order_info orderInfoId = order_info.dao.findFirst("select id from order_info where num = ?", credit_id_list);
                    long orderId = orderInfoId.get("id");
                    OrderResource resource = new OrderResource();
                    List<HashMap> resultMapGood = resource.getOrderHashMaps(orderId);
                    result_goods_order.put("goods_list", resultMapGood);
                    //买家信息实体
                    //String sql_buyer_info = YamlRead.getSQL("getFieldBuyerInfoAll", "seller/order");
                    String sql_buyer_receive = YamlRead.getSQL("getFieldBuyerReceiveAll", "seller/order");
                    order_user o = order_user.dao.findFirst("select distinct c.buyer_id from credit c where c.seller_id = ? and c.order_num = ?", seller_id, credit_id_list);
                    long buyer_id = o.get("buyer_id");
                    result_buyer_info.put("buyer_id", buyer_id);
                    user_info buyer_name = user_info.dao.findFirst("select nickname from user_info where id = ? ", buyer_id);
                    result_buyer_info.put("buyer_nickname", buyer_name.get("nickname"));
                    result_buyer_info.put("buyer_receive", buyer_receive_address.dao.findFirst(sql_buyer_receive, orderId));
                    result_goods_order.put("buyer_info", result_buyer_info);                        //2
                    //订单实体
                    String sqlOrderInfo = YamlRead.getSQL("getFieldOrderInfoAll", "seller/order");
                    result_goods_order.put("order_info", order_info.dao.findFirst(sqlOrderInfo, orderId));   //3

                    //订单备注
                    String sqlOrderRemark = YamlRead.getSQL("getFirldOrderRemarkAll", "seller/order");
                    result_goods_order.put("order_remark_list", order_remark.dao.find(sqlOrderRemark, orderId));  //4
                    //多个订单实体
                    result_buyer_credit.put("order", result_goods_order);
                    result_buyer_credit.put("credit_id", String.valueOf(id));
                    result_buyer_credit.put("status", seller_status);
                    creditsList.add(result_buyer_credit);
                }
            }
            all.put("total_count", creditFullPagelist.getTotalRow());
            all.put("credit_list", creditsList);
        }
        return all;
    }

    /**
     * 销账
     *
     * @param credit_list
     * @return
     */
    @PUT
    public Map opOrder(List<JSONObject> credit_list) {
        for (JSONObject c2 : credit_list) {
            if (c2.get("buyer_id") != null) {
                credit.dao.update("update credit set status = ?  where buyer_id = ? ", ConstantsUtils.CREDIT_ALREADY_STATUS, c2.get("buyer_id"));
            } else if (c2.get("credit_id") != null) {
                credit.dao.update("update credit set status = ?  where id = ? ", ConstantsUtils.CREDIT_ALREADY_STATUS, c2.get("credit_id"));
            }
        }
        return CommonUtils.getCodeMessage(true, "操作销账成功");
    }

    /**
     * 获取赊账总数
     *
     * @return Map
     */
    @GET("/creditCount")
    public Map getCreditCount() {
        long seller_id = SessionUtil.getUserId();
        Map resulttall_count = new HashMap();
        credit creditModel = new credit();
        //赊账实体
        String creditCountSql = YamlRead.getSQL("getCreditCount", "seller/credit");
        //已销账金额
        long alreadyPayCount = creditModel.getCountByUserIdAndStatus(creditCountSql, seller_id, ConstantsUtils.CREDIT_ALREADY_STATUS);
        //未销账金额
        long borrowPayCount = creditModel.getCountByUserIdAndStatus(creditCountSql, seller_id, ConstantsUtils.CREDIT_CANCEL_STATUS);
        resulttall_count.put("alreadyPayCount", alreadyPayCount);
        resulttall_count.put("borrowPayCount", borrowPayCount);
        return resulttall_count;
    }
}

