package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.util.Maper;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_type;
import com.qianmo.eshop.model.order.order_goods;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.resource.z_common.ApiResource;
import org.apache.poi.ss.formula.functions.T;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 买家获取单个订单详情
 * author:wss
 * 传入参数：id  订单id
 */

@API("/seller/order")
public class OrderResource extends ApiResource {

    @GET("/:id")
    public HashMap getList(int id) {
        HashMap result = new HashMap();
        try {

            String sql3 = YamlRead.getSQL("getFieldOrderInfoAll","seller/order");
            String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","seller/order");
//商品信息
            HashMap result2 =  new HashMap();
            String sql2_1 = YamlRead.getSQL("getFirldGoodsInfoAll","seller/order");
            String sql2_2 = YamlRead.getSQL("getFieldGoodsSkuListAll","seller/order");
            String sql2_3 = YamlRead.getSQL("getFieldGoodsTypeALL","seller/order");
            result2.put("goods_info", goods_info.dao.find(sql2_1,id));
            result2.put("goods_sku_list", goods_sku.dao.find(sql2_2,id));
            result2.put("goods_type", goods_type.dao.find(sql2_3,id));
//用户信息
            HashMap result3 = new HashMap();
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
            result.put("goods_list",result2);
            result.put("order_info",order_info.dao.find(sql3,id));
            result.put("order_remark_list", order_remark.dao.find(sql4,id));
            return result;
        }catch (Exception e){
            return  null;
        }
    }
}
