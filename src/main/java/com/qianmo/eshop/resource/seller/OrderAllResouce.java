package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.util.Maper;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.ConstantsUtils;
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
    public HashMap getList(String buyer_name,String date_range,Integer id,Integer order_status,Integer page_num,Integer page_size,Integer seller_id,Integer sort_col,String sort_rule) {

        HashMap result = new HashMap();

        String sql3 = YamlRead.getSQL("getFieldOrderInfoAll","seller/order");
        String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","seller/order");

        //商品信息
        HashMap result2 =  new HashMap();
        /*String sql2_1 = YamlRead.getSQL("getFirldGoodsInfoAll","seller/order");
        String sql2_2 = YamlRead.getSQL("getFieldGoodsSkuListAll","seller/order");
        String sql2_3 = YamlRead.getSQL("getFieldGoodsTypeALL","seller/order");
        List<goods_info>  goods_infoList =  goods_info.dao.find(sql2_1,id);
        result2.put("goods_info", goods_info.dao.find(sql2_1,id));*/

        OrderResource resource = new OrderResource();
        List<HashMap> resultMap = resource.getOrderHashMaps(id);
        //用户信息
        HashMap result3 =  new HashMap();
        String sql1_1 = YamlRead.getSQL("getFieldBuyerInfoAll","seller/order");
        String sql1_2 = YamlRead.getSQL("getFieldBuyerReceiveAll","seller/order");
        order_user o = new order_user();
        if(order_user.dao.find(sql1_1,id)!=null && order_user.dao.find(sql1_1,id).size()>0){
            o = order_user.dao.find(sql1_1,id).get(0);
        }
        result3.put("buyer_id",o.get("buyer_id"));
        result3.put("name",o.get("name"));
        result3.put("buyer_receive", buyer_receive_address.dao.find(sql1_2,id));

        result.put("buyer_info",result3);
        result.put("goods_list",resultMap);

        if ( order_status != null){
            sql3 = sql3 + " and oi.status = ?";
            result.put("order_info",order_info.dao.find(sql3,id,order_status));
        }else{
            result.put("order_info",order_info.dao.find(sql3,id));
        }
        result.put("order_remark_list", order_remark.dao.find(sql4,id));

        String sqlcount = YamlRead.getSQL("getFieldOrderCountAll","seller/order");

        //今日订单数
        int orderNum = order_info.dao.findFirst("select count(*) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CACEL).<Integer>get("cn");
        //今日交易额
        double totalPrice = order_info.dao.findFirst("select sum(total_price) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CACEL).<Integer>get("cn");
        HashMap count2 = new HashMap();
        count2.put("count",orderNum);
        count2.put("total_price",totalPrice);
        result.put("order_total",count2);
        return result;
    }



}
