package com.qianmo.eshop.resource.seller;

import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.DateUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.resource.buyer.OrderResource;

import java.util.*;

/**
 * 卖家获取订单信息
 * wss
 */

@API("/order")
public class OrderAllResouce extends SellerResource {

    @GET
    public HashMap getList(String buyer_name_num,String data_end,String data_start ,Integer date_step, Integer order_status,Integer page_start,Integer page_step) {
        long seller_id = SessionUtil.getUserId();

        //判断buyer_name_num是订单号还是商家名称
        if (buyer_name_num != null) {
            boolean boo = buyer_name_num.matches("[0-9]+");
            if (boo == true) {
                System.out.println("该字符串是订单号");
                //去调用订单详情
                OrderResource orderresource = new OrderResource();
                orderresource.getList(Integer.parseInt(buyer_name_num));
            } else {
                System.out.println("该字符串是买家名称");
                String order_user_buyer_sql = YamlRead.getSQL("getFirldOrderUserBuyerAll", "seller/order");
                List<order_user> order_user_buyer_List = order_user.dao.find(order_user_buyer_sql, buyer_name_num);
                for (order_user or_us_list : order_user_buyer_List) {
                    long id = (Long) ((JSONObject) or_us_list.get("order_user")).get("id");
                    //再次调用单个订单详情获取这个买家的所以订单
                    OrderResource orderresource = new OrderResource();
                    orderresource.getList(Integer.parseInt(buyer_name_num));
                }

            }
        }


        //最终推送
        HashMap resulfinal = new HashMap();
        //获取所以订单
        //List<HashMap> tempResult = getHashMaps(order_status, seller_id);
        HashMap result = new HashMap();
        String sqlorderinfo = YamlRead.getSQL("getFieldOrderInfoAll", "seller/order");
        String sqlorderremark = YamlRead.getSQL("getFirldOrderRemarkAll", "seller/order");

        List<HashMap> tempResult = new LinkedList<HashMap>();
        String order_user_sql = YamlRead.getSQL("getFirldOrderUserAll", "seller/order");
        HashMap resultBuyerReceiveAddress = new HashMap();

        //用户信息
        String sqlbuyerinfo = YamlRead.getSQL("getFieldBuyerInfoAll", "seller/order");
        String sqlbuyerreceive = YamlRead.getSQL("getFieldBuyerReceiveAll", "seller/order");
        HashMap result2 = new HashMap();

        //按时间搜索   ,统一按照订单标识
        //根据时间去获取订单信息
        if( date_step != null){
            if (date_step == ConstantsUtils.ORDER_TIME_TODAY){  //1:表示今天
                    order_user_sql = order_user_sql + "and date(ou.created_at) = date(sysdate())";
            }else if (date_step == ConstantsUtils.ORDER_TIME_F){//   7  表示近7天
                          order_user_sql = order_user_sql + "and date(ou.created_at) > date_add(date(sysdate()), interval -6 day)";
            }else if(date_step == ConstantsUtils.ORDER_TIME_s){ // 30 表示近30天
                        order_user_sql = order_user_sql + "and date(ou.created_at) > date_add(date(sysdate()), interval -29 day)";
            }
        }
        //按照具体时间范围去查询
        if (data_start != null && data_end != null){
            order_user_sql = order_user_sql + " and ou.created_at > " + DateUtils.formatDate(data_start,DateUtils.format_yyyyMMdd) + " and ou.created_at <" + DateUtils.formatDate(data_end,DateUtils.format_yyyyMMdd) ;
        }

        List<order_user> order_userList = order_user.dao.find(order_user_sql, seller_id);
        for (order_user or_numlist : order_userList)
        {

            long order_numlist = (Long) ((JSONObject) or_numlist.get("order_user")).get("order_num");
            //商品信息
            OrderResource resource = new OrderResource();
            List<HashMap> resultMap = resource.getOrderHashMaps(order_numlist);

            order_user o = new order_user();
            if (order_user.dao.find(sqlbuyerinfo, order_numlist) != null && order_user.dao.find(sqlbuyerinfo, order_numlist).size() > 0) {
                o = order_user.dao.find(sqlbuyerinfo, order_numlist).get(0);
            }
            resultBuyerReceiveAddress.put("buyer_id", o.get("buyer_id"));
            resultBuyerReceiveAddress.put("name", o.get("name"));
            resultBuyerReceiveAddress.put("buyer_receive", buyer_receive_address.dao.find(sqlbuyerreceive, order_numlist));

            result.put("buyer_info", resultBuyerReceiveAddress);     //0
            result.put("goods_list", resultMap);

            if (order_status != null) {
                sqlorderinfo = sqlorderinfo + " and oi.status = ?";
                result.put("order_info", order_info.dao.find(sqlorderinfo, order_numlist, order_status));
            } else {
                result.put("order_info", order_info.dao.find(sqlorderinfo, order_numlist));  //1
            }
            result.put("order_remark_list", order_remark.dao.find(sqlorderremark, order_numlist));  //2
            tempResult.add(result);
            //分页
            FullPage<order_user> inviteCodeList = order_user.dao.fullPaginateBy(page_start / page_step + 1, page_step, "page_start = ? and page_step = ?", seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
            HashMap count = new HashMap();


            resulfinal.put("order_list", tempResult);                     //3
            resulfinal.put("total_count", inviteCodeList.getTotalRow());  //3

           // String sqlcount = YamlRead.getSQL("getFieldOrderCountAll", "seller/order");

            //今日订单数
            int orderNum = order_info.dao.findFirst("select count(*) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CANCEL).<Integer>get("cn");
            //今日交易额
            double totalPrice = order_info.dao.findFirst("select sum(total_price) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CANCEL).<Integer>get("cn");
            HashMap count2 = new HashMap();
            count2.put("count", orderNum);
            count2.put("total_price", totalPrice);
            resulfinal.put("order_total", count2);  //4
        }
        return resulfinal;
    }

}
