package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.util.Maper;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.order.order_goods;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.resource.buyer.*;
import com.qianmo.eshop.resource.buyer.OrderResource;
import com.qianmo.eshop.resource.z_common.ApiResource;
import org.apache.poi.ss.formula.functions.T;
import  com.qianmo.eshop.resource.buyer.OrderResource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 卖家获取订单信息
 * wss
 */

@API("/order")
public class OrderAllResouce extends SellerResource {

    @GET
    public HashMap getList(String buyer_name_num,String date_range,Integer order_status,Integer page_start,Integer page_step,Integer sort_col,String sort_rule) {
            long seller_id = SessionUtil.getUserId();

        //判断buyer_name_num是订单号还是商家名称
        if(buyer_name_num != null){
            boolean boo = buyer_name_num.matches("[0-9]+");
            if (boo == true){
                System.out.println("该字符串是订单号");
                //去调用订单详情
                OrderResource orderresource = new OrderResource();
                orderresource.getList(Integer.parseInt(buyer_name_num));
            }else {
                System.out.println("该字符串是买家名称");
                String order_user_buyer_sql = YamlRead.getSQL("getFirldOrderUserBuyerAll","seller/order");
                List<order_user>  order_user_buyer_List =  order_user.dao.find(order_user_buyer_sql,buyer_name_num);
                for(order_user or_us_list:order_user_buyer_List){
                    long id = (Long)((JSONObject)or_us_list.get("order_user")).get("id");
                    //再次调用单个订单详情获取这个买家的所以订单
                    OrderResource orderresource = new OrderResource();
                    orderresource.getList(Integer.parseInt(buyer_name_num));
                }

            }
        }
        //按时间排序：

        //最终推送
        HashMap resulfinal = new HashMap();
        //获取所以订单
        List<HashMap> tempResult = getHashMaps(order_status, seller_id);
        //分页
        FullPage<order_user> inviteCodeList  =  order_user.dao.fullPaginateBy(page_start/page_step + 1,page_step,"page_start = ? and page_step = ?",seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
        HashMap count =  new HashMap();


        resulfinal.put("order_list",tempResult);
        resulfinal.put("total_count",inviteCodeList.getTotalRow());  //3

        String sqlcount = YamlRead.getSQL("getFieldOrderCountAll","seller/order");

        //今日订单数
        int orderNum = order_info.dao.findFirst("select count(*) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CACEL).<Integer>get("cn");
        //今日交易额
        double totalPrice = order_info.dao.findFirst("select sum(total_price) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CACEL).<Integer>get("cn");
        HashMap count2 = new HashMap();
        count2.put("count",orderNum);
        count2.put("total_price",totalPrice);
        resulfinal.put("order_total",count2);  //4

        return resulfinal;
    }

    private List<HashMap> getHashMaps(Integer order_status, long seller_id) {
        HashMap result = new HashMap();
        String sql3 = YamlRead.getSQL("getFieldOrderInfoAll","seller/order");
        String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","seller/order");

        List<HashMap> tempResult = new LinkedList<HashMap>();
        String order_user_sql = YamlRead.getSQL("getFirldOrderUserAll","seller/order");
        List<order_user>  order_userList =  order_user.dao.find(order_user_sql,seller_id);
        for (order_user or_numlist : order_userList)
        {
            long order_numlist = (Long)((JSONObject)or_numlist.get("order_user")).get("order_num");
            //商品信息
            HashMap result2 =  new HashMap();
            OrderResource resource = new OrderResource();
            List<HashMap> resultMap = resource.getOrderHashMaps(order_numlist);
            //用户信息
            HashMap result3 =  new HashMap();
            String sql1_1 = YamlRead.getSQL("getFieldBuyerInfoAll","seller/order");
            String sql1_2 = YamlRead.getSQL("getFieldBuyerReceiveAll","seller/order");
            order_user o = new order_user();
            if(order_user.dao.find(sql1_1,order_numlist)!=null && order_user.dao.find(sql1_1,order_numlist).size()>0){
                o = order_user.dao.find(sql1_1,order_numlist).get(0);
            }
            result3.put("buyer_id",o.get("buyer_id"));
            result3.put("name",o.get("name"));
            result3.put("buyer_receive", buyer_receive_address.dao.find(sql1_2,order_numlist));

            result.put("buyer_info",result3);     //0
            result.put("goods_list",resultMap);

            if ( order_status != null){
                sql3 = sql3 + " and oi.status = ?";
                result.put("order_info", order_info.dao.find(sql3,order_numlist,order_status));
            }else{
                result.put("order_info",order_info.dao.find(sql3,order_numlist));  //1
            }
            result.put("order_remark_list", order_remark.dao.find(sql4,order_numlist));  //2
            tempResult.add(result);
        }
        return tempResult;
    }


}
