package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.common.util.Maper;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.POST;
import cn.dreampie.route.annotation.PUT;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.CodeUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.goods.goods_sku_price;
import com.qianmo.eshop.model.order.order_goods;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.resource.z_common.ApiResource;
import org.apache.poi.ss.formula.functions.T;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 查看单个订单详情
 * author:wss
 * 传入参数说明：id 订单ID
 */

/**
 * 操作订单
 * author:wss
 * 传入参数说明：
 *    bank_id ：选择的银行ID 选填 当支付方式选择银行汇款时需传此字段、buyer_id：卖家ID 必填 、 goods:商品实体 选填 当操作选择再买一次时，传入此array
 *    id:订单编号 、op:必填 0选择支付方式 1选择银行 2我已付款 3确认收货 4取消订单 5再买一次、 value：选填 操作值（取消订单时，传入订单取消原因）
 *
 */

/**
 * 获取所有订单信息 --买家订单列表
 *  author:wss
 *  传入参数：order_num：订单ID
 */
/**
 * 买家添加订单
 * author：wss
 * 传入参数说明：
 * buyer_id 买家id 、buyer_receive_id 买家收货地址ID、
 * cart_list 购物车ID数组 String类型 ［1，2，3，4，5］、seller_id 卖家id
 */


@API("/order")
public class OrderResource extends BuyerResource {

    @GET("/:id")
    public HashMap getOrderDetail(int id) {
       HashMap result = new HashMap();
       try {

        //订单实体查询sql
        String sqlOrderinfo = YamlRead.getSQL("getFieldOrderInfoAll","buyer/order");
        //订单备注列表查询sql
        String sqlOrderremark = YamlRead.getSQL("getFirldOrderRemarkAll","buyer/order");
        //商品实体列表查询sql
           List<HashMap> resultMap = getOrderHashMaps(id);

        //买家信息实体查询sql
        HashMap result_buyer = new HashMap();
        String sqlbuyer_info = YamlRead.getSQL("getFieldBuyerInfoAll","buyer/order");
        //买家收货人实体
        String sqlbuyer_receive = YamlRead.getSQL("getFieldBuyerReceiveAll","buyer/order");
        order_user o = new order_user();
           List<order_user> order_users_list = order_user.dao.find(sqlbuyer_info,id);
        if(order_users_list!=null && order_users_list.size()>0){
         o = order_users_list.get(0);
        }
           result_buyer.put("buyer_id",o.get("buyer_id"));
           result_buyer.put("buyer_name",o.get("name"));
           result_buyer.put("buyer_receive", buyer_receive_address.dao.find(sqlbuyer_receive,id));

        //返回json
        result.put("buyer_info",result_buyer);
        result.put("goods_list",resultMap);
        result.put("order_info",order_info.dao.find(sqlOrderinfo,id));
        result.put("order_remark_list", order_remark.dao.find(sqlOrderremark,id));
        return result;
       }catch (Exception e) {
        //异常情况，方便记录日志 TODO
        result.put("buyer_info",null);
        result.put("goods_list",null);
        result.put("order_info",null);
        result.put("order_remark_list",null);
        return result;
       }
    }
//商品实体封装
    public List<HashMap> getOrderHashMaps(long id) {
        HashMap resultgoods =  new HashMap();
        //商品信息
        String sqlgoods_info = YamlRead.getSQL("getFirldGoodsInfoAll","buyer/order");
        //商品规格列表
        String sqlgoodssku = YamlRead.getSQL("getFieldGoodsSkuAll","buyer/order");
        //商品分类
        String sqlgoodstype = YamlRead.getSQL("getFieldGoodsTypeALL","buyer/order");
        Map goodsResult = new HashMap();

        List<goods_info>  goods_infoList =  goods_info.dao.find(sqlgoods_info,id);
        List<HashMap> resultMap = new ArrayList<HashMap>();
        for (goods_info goodlist: goods_infoList ){
            resultgoods.clear();
            resultgoods.put("goods_info", goods_info.dao.find(sqlgoods_info,id));
            long goodsNum = (Long)((JSONObject)goodlist.get("goods_info")).get("id");
            long category_id = (Long)((JSONObject)goodlist.get("goods_info")).get("category_id");
            resultgoods.put("goods_sku_list", goods_sku.dao.find(sqlgoodssku,goodsNum));
            resultgoods.put("goods_type", goods_category.dao.find(sqlgoodstype,category_id));
            resultMap.add(resultgoods);
        }
        return resultMap;
    }

    //操作订单
    @PUT
    @Transaction
    public WebResult opOrder(Integer bank_id,int id,int op,String value, List<JSONObject> goods){
            long buyer_id = SessionUtil.getUserId();
            if(op == ConstantsUtils.ORDER_OP_PAY_TYPE){   // 0 选择支付方式
                if("1".equals(value)){                               // 当支付方式选择银行支付的时候
                    if (bank_id != null){
                        order_info.dao.update("update order_info set pay_type_id = ?  where num = ? ", op, id);
                    }
                }
            }else  if (op == ConstantsUtils.ORDER_OP_BANK){     // 1 选择银行  目前默认为农行
                if (bank_id != null){
                    //待开发
                }
            }else  if(op == ConstantsUtils.ORDER_OP_PAY_STATUS){    // 2 我已付款
                order_info.dao.update("update order_info set pay_status = ?  where num = ? ",ConstantsUtils.ORDER_PAYMENT_STATUS_RECEIVED, id);
            }else  if(op == ConstantsUtils.ORDER_OP_PAY_GOODS){    // 3 确认收货
                order_info.dao.update("update order_info set status = ?  where num = ? ", ConstantsUtils.ORDER_INFO_STATUS_FINISHED,id);

            }else if(op == ConstantsUtils.ORDER_OP_PAY_CELL){  // 4 取消订单
                order_info.dao.update("update order_info set status = ?  where num = ? ", ConstantsUtils.ORDER_INFO_STATUS_CANCEL,id);
                new order_remark().set("order_num",id).set("op",op).set("reason",value).set("user_id",buyer_id).save();
            }else{  //5再买一次  添加一次购物车
                CartResource  cartResource = new CartResource();
                cartResource.addCartGoods(goods);
            }
            return new WebResult(HttpStatus.OK, "操作订单成功");

    }

    //获取所有订单详情
    @GET
    public HashMap getOrderList(Integer order_num,Integer order_status,Integer page_start,Integer page_step) {
        //根据循环获取买家Id
        long buyerId = SessionUtil.getUserId();
        //根据买家id获取订单号列表
        List<order_user> orderUserList = null;
        //如果状态不为空，则需要根据状态去找order list
        if(order_status != null && order_status != 0) {
            String getOrderNumByStatusSql = YamlRead.getSQL("getOrderNumByStatus","buyer/order");
            orderUserList = order_user.dao.find(getOrderNumByStatusSql,order_status, buyerId);
        } else {
            orderUserList = order_user.dao.findBy("buyer_id = ?", buyerId);
        }
        //订单实体
        HashMap orderMap = new HashMap();
        //返回订单列表
        List<HashMap> resultMapList = new ArrayList<HashMap>();
        if (orderUserList != null && orderUserList.size() > 0) {
            for(order_user orderUser : orderUserList) {
                orderMap.clear();
                OrderResource resource = new OrderResource();
                orderMap = resource.getOrderDetail(order_num);
                resultMapList.add(orderMap);
            }
        }
        HashMap resultMap = new HashMap();
        resultMap.put("order_list",resultMapList);
        JSONObject pageInfo = new JSONObject();
        pageInfo.put("total_count",resultMapList.size());
        resultMap.put("page_info",pageInfo);
        return resultMap;
       /* HashMap result = new HashMap();
        String sql3 = YamlRead.getSQL("getFieldOrderInfoAll","buyer/order");
        String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","buyer/order");

        //商品信息
        HashMap result2 =  new HashMap();

       *//* String sql2_1 = YamlRead.getSQL("getFirldGoodsInfoAll","buyer/order");
        String sql2_2 = YamlRead.getSQL("getFieldGoodsSkuListAll","buyer/order");
        String sql2_3 = YamlRead.getSQL("getFieldGoodsTypeALL","buyer/order");
*//*

        OrderResource resource = new OrderResource();
        List<HashMap> resultMap = resource.getOrderHashMaps(order_num);


        //用户信息
        HashMap result3 =  new HashMap();
        String sql1_1 = YamlRead.getSQL("getFieldBuyerInfoAll","buyer/order");
        String sql1_2 = YamlRead.getSQL("getFieldBuyerReceiveAll","buyer/order");
        order_user o = new order_user();
        if(order_user.dao.find(sql1_1,order_num)!=null && order_user.dao.find(sql1_1,order_num).size()>0){
            o = order_user.dao.find(sql1_1,order_num).get(0);
        }
        result3.put("buyer_id",o.get("buyer_id"));
        result3.put("buyer_name",o.get("name"));
        result3.put("buyer_receive", buyer_receive_address.dao.find(sql1_2,order_num));
        //分页
        FullPage<order_user> inviteCodeList  =  order_user.dao.fullPaginateBy(page_start/page_step + 1,page_step,"page_start = ? and page_step = ?",o.get("seller_id"), ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);

        result.put("buyer_info",result3);
        result.put("goods_list",resultMap);
        if ( order_status != null){
            sql3 = sql3 + " and oi.status = ?";
            result.put("order_info",order_info.dao.find(sql3,order_num,order_status));
        }else{
            result.put("order_info",order_info.dao.find(sql3,order_num));
        }
        HashMap count =  new HashMap();
        count.put("total_count",inviteCodeList.getTotalRow());

        result.put("order_remark_list", order_remark.dao.find(sql4,order_num));
        result.put("page_info",count);

        return result;*/
    }


    //添加订单
    @POST
    @Transaction
    public WebResult addOrder(int buyer_receive_id, String cart_list, Long seller_id) {
        long buyer_id = SessionUtil.getUserId();
        HashMap result = new HashMap();
        //订单编号组成的规则、年月日时分秒+4位随机数
        long ltime = System.currentTimeMillis();
        Date date = new Date(ltime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //订单编号
        //String num = dateFormat.format(date) + "";
        String num = CodeUtils.code(dateFormat.format(date), ConstantsUtils.ORDER_NUM_TYPE);
        //根据购物车ID，从购物车中选取买家购买信息
        String sql3 = YamlRead.getSQL("getFieldCartAll", "buyer/cart");
        List<cart> results = cart.dao.findBy( "id in (" + cart_list +")");
        //商品表中订单总价
        double total_price = 0;
        //遍历购物车
        if (results != null && results.size() > 0) {
            for (cart cart : results) {
                //商品单价
                String sqlprice = YamlRead.getSQL("getBuyerPrice", "buyer/order");
                goods_sku_price results_goods = goods_sku_price.dao.findFirst(sqlprice, cart.get("buyer_id"), cart.get("seller_id"),cart.get("goods_sku_id"),cart.get("goods_sku_id"));
                //if (results2 != null && results2.size() > 0) {
                //  for (goods_sku_price gsp : results2) {
                long goods_sku_count = cart.get("goods_sku_count");
                double goods_sku_price = results_goods.get("price");
                double single_total_price = goods_sku_count * goods_sku_price;
                //for (int i = 0; i < results2.size(); i++) {
                total_price += single_total_price;
                //}
                //插入订单商品表和订单用户表
                new order_goods().set("area_id", cart.get("area_id")).set("goods_num", cart.get("goods_num")).set("sku_id", cart.get("goods_sku_id")).set("order_num", num).set("goods_sku_price", goods_sku_price).set("goods_sku_count", cart.get("goods_sku_count")).set("single_total_price", single_total_price).save();
                new order_user().set("area_id", cart.get("area_id")).set("order_num", num).set("buyer_id", buyer_id).set("seller_id", seller_id).save();
            }
            //}
            // order_info aa = order_info.dao.findById("1");
            // order_info.dao.deleteById("1");
            // aa.set("goods_num","1").update();
            //插入订单表

            new order_info().set("area_id", ConstantsUtils.ALL_AREA_ID).set("num", num).set("status", ConstantsUtils.ORDER_INFO_STATUS_CREATED).set("pay_status", ConstantsUtils.ORDER_PAYMENT_STATUS_WAITE).set("total_price", total_price).set("buyer_receive_id", buyer_receive_id).set("created_at", dateFormat.format(date)).set("updated_at", dateFormat.format(date)).set("deleted_at", dateFormat.format(date)).save();
        }
        HashMap hash = new HashMap();
        //根据订单编号查订单ID
        String order_id = YamlRead.getSQL("getFieldOrderIdAll", "buyer/order");
        //返回订单ID 和 订单编号
        hash.put("order_id", order_info.dao.find(order_id, num));
        hash.put("order_num", num);
        return new WebResult(HttpStatus.OK, "添加订单成功");
    }

}
