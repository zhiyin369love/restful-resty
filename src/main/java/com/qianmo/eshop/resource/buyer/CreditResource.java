package com.qianmo.eshop.resource.buyer;

import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.page.Page;
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
import java.util.Map;

/**
 * 获取赊账信息
 * @author :wss
 */

@API("/credit")
public class CreditResource extends BuyerResource {

    /**
     * @param page_start  第几条开始
     * @param page_step 返回多少条
     * @return
     */
    @GET
    public HashMap getCredit(Integer creditStatus, Integer page_start,Integer page_step) {
            long buyer_id = SessionUtil.getUserId();
            HashMap resulttall_count = new HashMap();
            //赊账实体
            String sqlCredit = YamlRead.getSQL("getCreditByBuyerId","buyer/credit");
            //订单实体
            String sqlOrder = YamlRead.getSQL("getFieldCreditOrderAll","buyer/credit");

            //订单备注列表
            String sqlOrderRemark = YamlRead.getSQL("getFirldOrderRemarkAll","buyer/order");
            //商品信息
           //分页
            List<HashMap> creditsList = new ArrayList<HashMap>();
            FullPage<credit> creditOrderList = null;
        //List<credit>  CreditOrderList =  credit.dao.find(sqlcredit,buyer_id);
            if(page_start == null || page_start ==0) {
                 page_start = ConstantsUtils.DEFAULT_PAGE_START;
            }
            if(page_step == null || page_step == 0) {
                page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
            }
            int pageNumber = page_start/page_step + 1;
            if(creditStatus != null) {
                sqlCredit += " and status = ? " ;
                sqlCredit += " order by created_at desc";
                creditOrderList  =  credit.dao.fullPaginate(pageNumber,page_step,sqlCredit,buyer_id,creditStatus);
            } else {
                sqlCredit += " order by created_at desc";
                creditOrderList  =  credit.dao.fullPaginate(pageNumber,page_step,sqlCredit,buyer_id);
            }
            //List<HashMap> resultMap = new ArrayList<HashMap>();

            if(creditOrderList.getList() != null && creditOrderList.getList().size() >0) {
                for (credit credit_list : creditOrderList.getList()) {
                    HashMap resultall = new HashMap();
                    HashMap result = new HashMap();
                    HashMap result_goods =  new HashMap();
                    HashMap result_buyer = new HashMap();
                    //一个订单对应一个赊账
                    long id = credit_list.get("id");
                    int status = credit_list.get("status");
                    long credit_id_list = credit_list.<Long>get("order_num");
                    order_info orderInfoId = order_info.dao.findFirst("select id from order_info where num = ?", credit_id_list);
                    long orderId = orderInfoId.get("id");
                    OrderResource resource = new OrderResource();
                    List<HashMap> resultMapGood = resource.getOrderHashMaps(orderId);
                    //用户信息
                    String sqlbuyerinfo = YamlRead.getSQL("getFieldBuyerInfoAll", "buyer/order");
                    String sqlbuyerreceive = YamlRead.getSQL("getFieldBuyerReceiveAll", "buyer/order");
                    order_user o = order_user.dao.findFirst(sqlbuyerinfo, orderId);
                /*if(order_users_list!=null && order_users_list.size()>0){
                    o = order_user.dao.find(sqlbuyerinfo,buyer_id).get(0);
                }*/
                    result_buyer.put("buyer_id", o.get("buyer_id"));
                    result_buyer.put("name", o.get("name"));
                    result_buyer.put("buyer_receive", buyer_receive_address.dao.findFirst(sqlbuyerreceive, orderId));
                    //一个买家对应对个订单实体
                    result.put("buyer_info", result_buyer);
                    result.put("goods_list", resultMapGood);
                    result.put("order_info", order_info.dao.findFirst(sqlOrder, credit_id_list, credit_list.get("seller_id")));
                    result.put("order_remark_list", order_remark.dao.find(sqlOrderRemark, orderId));
                    result_goods.put("order", result);
                    //赊账实体表
                    resultall.put("credit_id", String.valueOf(id));
                    resultall.put("credit_status", status);
                    resultall.put("order", result);

                    creditsList.add(resultall);
                }
            }
            //分页信息
            resulttall_count.put("total_count",creditOrderList.getTotalRow());
            resulttall_count.put("credit_list",creditsList);
            return resulttall_count;
    }

    /**
     * 获取赊账总数
     * @return Map
     */
    @GET("/creditCount")
    public Map getCreditCount() {
        long buyer_id = SessionUtil.getUserId();
        Map resulttall_count = new HashMap();
        //赊账实体
        String creditCountSql = YamlRead.getSQL("getCreditCount","buyer/credit");
        credit creditModel = new credit();
        //已销账金额
        long alreadyPayCount = creditModel.getCountByUserIdAndStatus(creditCountSql,buyer_id,ConstantsUtils.CREDIT_ALREADY_STATUS);
        //未销账金额
        long borrowPayCount = creditModel.getCountByUserIdAndStatus(creditCountSql,buyer_id,ConstantsUtils.CREDIT_CANCEL_STATUS);
        resulttall_count.put("alreadyPayCount",alreadyPayCount);
        resulttall_count.put("borrowPayCount",borrowPayCount);
        return resulttall_count;
    }
}

