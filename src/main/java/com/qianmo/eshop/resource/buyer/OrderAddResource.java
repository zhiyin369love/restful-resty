package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.POST;
import com.qianmo.eshop.common.CodeUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.order.order_goods;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.resource.z_common.ApiResource;

import cn.dreampie.common.http.result.WebResult;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 *  买家添加订单
 *  author：wss
 *  传入参数说明：
 *   buyer_id 买家id 、buyer_receive_id 买家收货地址ID、
 *   cart_list 购物车ID数组 String类型 ［1，2，3，4，5］、seller_id 卖家id
 */

@API("/order")
public class OrderAddResource extends BuyerResource {


    //添加订单
    @POST
    @Transaction
    public WebResult addOrder(int buyer_receive_id, String cart_list,Long seller_id) {
        long buyer_id = SessionUtil.getUserId();
        try {
           HashMap result = new HashMap();
           //订单编号组成的规则、年月日时分秒+4位随机数
           long l = System.currentTimeMillis();
           Date date = new Date(l);
           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
           //订单编号
//           String num = dateFormat.format(date) + "";
           String num = CodeUtils.code(dateFormat.format(date),ConstantsUtils.ORDER_NUM_TYPE);
          //根据购物车ID，从购物车中选取买家购买信息
           String sql3 = YamlRead.getSQL("getFieldCartAll", "buyer/cart");
           //当添加订单时应该要往订单相关表中插入数据，order_info、order_goods、order_user
           List<cart> results = cart.dao.find(sql3, cart_list);
           //商品表中订单总价
           double total_price = 0;
           //遍历购物车
           if (results != null && results.size() > 0) {
               for (cart cart : results) {
                   //商品单价
                   String sqlprice = YamlRead.getSQL("getFieldGoodsSkuPriceAll", "buyer/goods");
                   List<goods_sku_price> results2 = goods_sku_price.dao.find(sqlprice, cart.get("goods_sku_id"), cart.get("buyer_id"), cart.get("seller_id"));
                   if (results2 != null && results2.size() > 0) {
                       for (goods_sku_price gsp : results2) {
                           long goods_sku_count = cart.get("goods_sku_count");
                           double goods_sku_price = gsp.get("price");
                           double single_total_price = goods_sku_count * goods_sku_price;

                           for (int i = 0; i < results2.size(); i++) {
                               total_price += single_total_price;
                           }
                           //插入订单商品表和订单用户表
                           new order_goods().set("area_id", cart.get("area_id")).set("goods_num", cart.get("goods_num")).set("sku_id", cart.get("goods_sku_id")).set("order_num", num).set("goods_sku_price", goods_sku_price).set("goods_sku_count", cart.get("goods_sku_count")).set("single_total_price", single_total_price).set("created_at", cart.get("created_at")).set("updated_at", cart.get("updated_at")).set("deleted_at", cart.get("deleted_at")).save();
                           new order_user().set("area_id", cart.get("area_id")).set("order_num", num).set("buyer_id", buyer_id).set("seller_id", seller_id).set("created_at", cart.get("created_at")).set("updated_at", cart.get("updated_at")).set("deleted_at", cart.get("deleted_at")).save();
                       }
                   }
                   // order_info aa = order_info.dao.findById("1");
                   // order_info.dao.deleteById("1");
                   // aa.set("goods_num","1").update();
               }
               //插入订单表
               new order_info().set("area_id", ConstantsUtils.ALL_AREA_ID).set("num", num).set("status", "1").set("pay_status", "1").set("total_price", total_price).set("buyer_receive_id", buyer_receive_id).set("created_at", dateFormat.format(date)).set("updated_at", dateFormat.format(date)).set("deleted_at", dateFormat.format(date)).save();
           }
           HashMap hash = new  HashMap();
           //根据订单编号查订单ID
           String order_id = YamlRead.getSQL("getFieldOrderIdAll", "buyer/order");
           //返回订单ID 和 订单编号
           hash.put("order_id",order_info.dao.find(order_id,num));
           hash.put("order_num",num);
           return new WebResult(HttpStatus.OK, "添加订单成功");
       } catch (Exception e) {
           //异常情况，按理说需要记录日志，也可考虑做统一的日志拦截 TODO
           return new WebResult(HttpStatus.EXPECTATION_FAILED, "添加订单失败");
       }
    }

}
