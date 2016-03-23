package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.page.Page;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.POST;
import cn.dreampie.route.annotation.PUT;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.*;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;
import com.qianmo.eshop.model.order.order_goods;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单
 * author:wss
 */
@API("/order")
public class OrderResource extends BuyerResource {
    /**
     * 查看单个订单详情
     *
     * @param id 订单ID
     * @return
     */
    @GET("/:id")
    public HashMap getOrderDetail(long id) {
        HashMap result = new HashMap();
        //订单实体查询sql
        String sqlOrderinfo = YamlRead.getSQL("getFieldOrderInfoAll", "buyer/order");
        //订单备注列表查询sql
        String sqlOrderremark = YamlRead.getSQL("getFirldOrderRemarkAll", "buyer/order");
        //商品实体列表查询sql
        List<HashMap> resultMap = getOrderHashMaps(id);
        //买家信息实体查询sql
        HashMap result_buyer = new HashMap();
        String sqlbuyer_info = YamlRead.getSQL("getFieldBuyerInfoAll", "buyer/order");
        //买家收货人实体
        String sqlbuyer_receive = YamlRead.getSQL("getFieldBuyerReceiveAll", "buyer/order");
        order_user o = order_user.dao.findFirst(sqlbuyer_info, id);
        result_buyer.put("buyer_id", o.get("buyer_id"));
        result_buyer.put("buyer_name", o.get("name"));
        result_buyer.put("buyer_receive", buyer_receive_address.dao.findFirst(sqlbuyer_receive, id));

        //返回json
        result.put("buyer_info", result_buyer);
        result.put("goods_list", resultMap);
        result.put("order_info", order_info.dao.findFirst(sqlOrderinfo, id));
        result.put("order_remark_list", order_remark.dao.find(sqlOrderremark, id));
        return result;
    }

    //商品实体封装
    public List<HashMap> getOrderHashMaps(long id) {

        //商品信息
        String sqlGoodInfo = YamlRead.getSQL("getFirldGoodsInfoAll", "buyer/order");
        //商品规格列表
        String sqlGoodsSku = YamlRead.getSQL("getFieldGoodsSkuAll", "buyer/order");
        //订单编号
        order_info num = order_info.dao.findFirst("select num from order_info where id = ? ", id);
        //商品分类
        String sqlGoodType = YamlRead.getSQL("getFieldGoodsTypeALL", "seller/order");

        //Map goodsResult = new HashMap();
        List<goods_info> goods_infoList = goods_info.dao.find(sqlGoodInfo, id);
        List<HashMap> resultMap = new ArrayList<HashMap>();
        for (goods_info goodlist : goods_infoList) {
            HashMap resultGoods = new HashMap();
            // goods_info goods_info_list = goods_info.dao.findFirst(sqlGoodInfo,id);
            long goodsNum = goodlist.get("number");
            long orderNum = num.get("num");
            long category_id = goodlist.get("category_id");
            resultGoods.put("goods_sku_list", goods_sku.dao.find(sqlGoodsSku, goodsNum, orderNum));
            resultGoods.put("goods_type", goods_category.dao.findFirst(sqlGoodType, category_id));
            resultGoods.put("goods_info", goods_info.dao.findFirstBy(" num = ? ", goodsNum));
            resultMap.add(resultGoods);
        }
        return resultMap;
    }


    /**
     * 操作订单
     * author:wss
     *
     * @param bank_id   选择的银行ID 选填 当支付方式选择银行汇款时需传此字段
     * @param goods     商品实体 选填 当操作选择再买一次时，传入此array
     * @param order_num 订单编号
     * @param op        必填 0选择支付方式 1选择银行 2我已付款 3确认收货 4取消订单 5再买一次
     * @param value     选填 操作值（取消订单时，传入订单取消原因）
     * @return
     */
    @PUT
    @Transaction
    public Map opOrder(Long bank_id, long order_num, int op, String value, List<Map> goods) {
        long buyer_id = SessionUtil.getUserId();
        boolean isSuccess = false;
        Map result = new HashMap();
        switch (op) {
            case ConstantsUtils.ORDER_OP_PAY_TYPE:

                //1：银行汇款 2：货到付款  3：在线支付 4：赊账
                if ("1".equals(value)) {                               // 当支付方式选择银行支付的时候
                    if (bank_id != null) {
                        isSuccess = order_info.dao.update("update order_info set pay_type_id = ?  where num = ? ", Long.valueOf(value), order_num);
                    }
                } else {
                    isSuccess = order_info.dao.update("update order_info set pay_type_id = ?  where num = ? ", Long.valueOf(value), order_num);
                }
                break;
            case ConstantsUtils.ORDER_OP_BANK:
                /*if (bank_id != null) {  // 1 选择银行  目前默认为农行
                    //待开发
                }*/
                break;
            case ConstantsUtils.ORDER_OP_PAY_STATUS: // 2 我已付款
                isSuccess = order_info.dao.update("update order_info set pay_status = ?  where num = ? ", ConstantsUtils.ORDER_PAYMENT_STATUS_WAITE_TRUE, order_num);
                break;
            case ConstantsUtils.ORDER_OP_PAY_GOODS: // 3 确认收货
                isSuccess = order_info.dao.update("update order_info set status = ?  where num = ? ", ConstantsUtils.ORDER_INFO_STATUS_FINISHED, order_num);
                break;
            case ConstantsUtils.ORDER_OP_PAY_CELL: // 4 取消订单
                order_info o = order_info.dao.findFirstBy(" num = ?", order_num);
                if (o.get("status") == ConstantsUtils.ORDER_INFO_STATUS_CREATED
                        || o.get("pay_status") == ConstantsUtils.ORDER_PAYMENT_STATUS_WAITE) {
                    isSuccess = order_info.dao.update("update order_info set status = ?  where num = ? ", ConstantsUtils.ORDER_INFO_STATUS_CANCEL, order_num);
                    //isSuccess = new order_remark().set("order_num", order_num).set("op", op).set("reason", value).set("user_id", buyer_id).set("area_id", ConstantsUtils.ALL_AREA_ID).set("details", "").save();
                } else {
                    return CommonUtils.getCodeMessage(false, "不允许取消");
                }
                break;
            case ConstantsUtils.ORDER_OP_BUYER_AGAIN:  //5再买一次  添加一次购物车
                CartResource cartResource = new CartResource();
                result = cartResource.addCartGoods(goods);
                if("200".equals(result.get("code").toString())) {
                    isSuccess = true;
                }
                break;
        }
        if (isSuccess) {
            return setResult("操作订单成功");
        } else {
            return CommonUtils.getCodeMessage(false,"操作订单失败");
        }
    }

    private Map setResult(String message) {
        Map resultMap = new HashMap();
        resultMap.put("code", ConstantsUtils.HTTP_STATUS_OK_200);
        resultMap.put("message", message);
        return resultMap;
    }

    /**
     * 获取所有订单信息 --买家订单列表
     * author:wss
     *
     * @param order_status 订单状态
     * @param page_start
     * @param page_step
     * @return
     */
    @GET
    public HashMap getOrderList(Integer order_status, Integer page_start, Integer page_step) {
        //根据循环获取买家Id
        long buyerId = SessionUtil.getUserId();
        //根据买家id获取订单号列表
        List<order_info> orderUserList = null;
        //如果状态不为空，则需要根据状态去找order list
        if (page_start == null || page_start == 0) {
            page_start = ConstantsUtils.DEFAULT_PAGE_START;
        }
        if (page_step == null || page_step == 0) {
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
        }
        int pageNumber = page_start / page_step + 1;
        FullPage<order_info> orderUserPage = null;
        String getOrderNumByStatusSql = YamlRead.getSQL("getOrderNumByStatus", "buyer/order");
        if (order_status != null) {
            getOrderNumByStatusSql = getOrderNumByStatusSql + "  and a.status = ?";
            orderUserPage = order_info.dao.fullPaginate(pageNumber, page_step, getOrderNumByStatusSql, buyerId, order_status);
            orderUserList = orderUserPage == null ? new ArrayList<order_info>() : orderUserPage.getList();
        } else {
            orderUserPage = order_info.dao.fullPaginate(pageNumber, page_step, getOrderNumByStatusSql, buyerId);
            orderUserList = orderUserPage == null ? new ArrayList<order_info>() : orderUserPage.getList();
        }
        //订单实体

        //返回订单列表
        List<HashMap> resultMapList = new ArrayList<HashMap>();
        if (orderUserList != null && orderUserList.size() > 0) {
            for (order_info orderinfolist : orderUserList) {
                HashMap orderMap =  getOrderDetail(orderinfolist.<Long>get("id"));
                resultMapList.add(orderMap);
            }
        }
        HashMap resultMap = new HashMap();
        resultMap.put("order_list", resultMapList);
        JSONObject pageInfo = new JSONObject();
        pageInfo.put("total_count", orderUserPage.getTotalRow());
        resultMap.put("page_info", pageInfo);
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

    /**
     * 买家添加订单
     * author：wss
     *
     * @param buyer_receive_id 买家收货地址ID、
     * @param cart_list        购物车ID数组 String类型 ［1，2，3，4，5］
     * @param seller_id        卖家id
     */
    @POST
    @Transaction
    public HashMap addOrder(Long buyer_receive_id, String cart_list, Long seller_id) {
        //System.out.print("进来");
        long buyer_id = SessionUtil.getUserId();
        //HashMap result = new HashMap();
        //订单编号组成的规则、年月日时分秒+4位随机数
        long ltime = System.currentTimeMillis();
        Date date = new Date(ltime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //订单编号

        //根据购物车ID，从购物车中选取买家购买信息
        //String sql3 = YamlRead.getSQL("getFieldCartAll", "buyer/cart");
        List<cart> results = cart.dao.findBy(" id in (" + cart_list + ")");
        //商品表中订单总价

        BigDecimal total_price = new BigDecimal(0);
        String num = "";
        //遍历购物车
        if (results != null && results.size() > 0) {
            num = CodeUtils.code(dateFormat.format(date), ConstantsUtils.ORDER_NUM_TYPE);
            for (cart cart : results) {
                //商品单价
                String sqlprice = YamlRead.getSQL("getBuyerPrice", "buyer/order");
                goods_sku_price results_goods = goods_sku_price.dao.findFirst(sqlprice, cart.get("buyer_id"), cart.get("seller_id"), cart.get("goods_sku_id"), cart.get("goods_sku_id"));
                Integer goods_sku_count = cart.get("goods_sku_count");
                BigDecimal goods_sku_price = results_goods.get("price");
                BigDecimal single_total_price = new BigDecimal(goods_sku_count).multiply(goods_sku_price);
                total_price = total_price.add(single_total_price);
                //插入订单商品表和订单用户表
                new order_goods().set("area_id", cart.get("area_id")).set("goods_num", cart.get("goods_num")).set("sku_id", cart.get("goods_sku_id")).set("order_num", num).set("goods_sku_price", goods_sku_price).set("goods_sku_count", cart.get("goods_sku_count")).set("single_total_price", single_total_price).save();
            }
            //num = CodeUtils.code(dateFormat.format(date), ConstantsUtils.ORDER_NUM_TYPE);
            cart.dao.deleteBy("id in (" + cart_list + ")");
            new order_user().set("area_id", ConstantsUtils.ALL_AREA_ID).set("order_num", num).set("buyer_id", buyer_id).set("seller_id", seller_id).save();
            new order_info().set("area_id", ConstantsUtils.ALL_AREA_ID).set("num", num).set("status", ConstantsUtils.ORDER_INFO_STATUS_CREATED).set("pay_status", ConstantsUtils.ORDER_PAYMENT_STATUS_WAITE).set("total_price", total_price).set("buyer_receive_id", buyer_receive_id).set("pay_type_id", ConstantsUtils.ORDER_PAYMENT_STATUS_WAITE).save();
        }
        HashMap hash = new HashMap();
        //根据订单编号查订单ID
        String order_id = YamlRead.getSQL("getFieldOrderIdAll", "buyer/order");
        //返回订单ID 和 订单编号
        order_info order_info_list = order_info.dao.findFirst(order_id, num);
        hash.put("order_id", order_info_list.get("id"));
        hash.put("order_num", num);
        return hash;
    }


    //获取买家订单状态数量
    @GET("/orderCount")
    public Map getOrderCount(String buyer_name_num, String data_end, String data_start) {
        //通过session获取当前登录用户
        long buyer_id = SessionUtil.getUserId();
        return getOrderStatus(buyer_name_num, data_end, data_start, buyer_id);
    }

    private Map getOrderStatus(String buyer_name_num, String data_end, String data_start, long buyer_id) {
        //查找订单信息sql
        String orderInfoSql = "select count(1) cn from order_info a LEFT JOIN order_user b ON a.num = b.order_num where 1=1 ";
        Map resulfinal = new HashMap();
        boolean isOrderNum = false;
        //是否存在buyer_name_num
        boolean exitsBuyerNameNum = false;
        if (!StringUtils.isEmpty(buyer_name_num)) {
            exitsBuyerNameNum = true;
            isOrderNum = buyer_name_num.matches("[0-9]+");
            if (isOrderNum == true) {
                // System.out.println("该字符串是订单号");
                orderInfoSql += " and a.num like  ? ";
            } else {
                //System.out.println("该字符串是买家名称");
                orderInfoSql = " SELECT a.* FROM order_info a LEFT JOIN order_user b ON a.num = b.order_num " +
                        "    LEFT JOIN user_info c ON b.buyer_id = c.id where c.nickname like ? ";
            }
        }
        //当天开始时间
        if (StringUtils.isEmpty(data_start)) {
            orderInfoSql += " and a.created_at >= date(sysdate())";
        } else {
            if (data_start.equals(data_end)) {
                orderInfoSql += " and Date(a.created_at) = Date('" + data_start + "')";
            } else {
                orderInfoSql += " and a.created_at >=  Date('" + data_start + "')";
            }
        }
        //当天结束时间
        if (StringUtils.isEmpty(data_end)) {
            orderInfoSql += " and a.created_at <= DATE_ADD(DATE(SYSDATE()), INTERVAL 1 DAY)";
        } else {
            if (data_end.equals(data_start)) {
                orderInfoSql += " and Date(a.created_at) = Date('" + data_start + "')";
                //orderInfoSql += " and Date(a.created_at) = " + DateUtils.formatDate(data_start, DateUtils.format_yyyyMMdd);
            } else {
                orderInfoSql += " and a.created_at <=  Date('" + data_end + "')";
                //orderInfoSql += " and a.created_at <=" + DateUtils.formatDate(data_end, DateUtils.format_yyyyMMdd);
            }
        }
        orderInfoSql += " and a.status = ?  and b.buyer_id = ?";
        FullPage<order_info> orderUserPage;
        List<order_info> order_userList;
        //判断buyer_name_num是订单号还是商家名称
        //待付款
        long waitPay = 0;
        //待发货
        long waitSend = 0;
        //待收货
        long waitReceive = 0;
        //已完成
        long finished = 0;
        //取消
        long cancel = 0;
        if (exitsBuyerNameNum) {
            waitPay = getOrderCount(orderInfoSql, buyer_name_num, ConstantsUtils.ORDER_INFO_STATUS_CREATED,buyer_id);
            //waitSend = getOrderCount(orderInfoSql, buyer_name_num, ConstantsUtils.ORDER_INFO_STATUS_WAIT_RECEIVE,buyer_id);
            waitReceive = getOrderCount(orderInfoSql, buyer_name_num, ConstantsUtils.ORDER_INFO_STATUS_ALREADY,buyer_id);
            //finished = getOrderCount(orderInfoSql, buyer_name_num, ConstantsUtils.ORDER_INFO_STATUS_FINISHED,buyer_id);
            cancel = getOrderCount(orderInfoSql, buyer_name_num, ConstantsUtils.ORDER_INFO_STATUS_CANCEL,buyer_id);
        } else {
            waitPay = getOrderCount(orderInfoSql, ConstantsUtils.ORDER_INFO_STATUS_CREATED,buyer_id);
            //waitSend = getOrderCount(orderInfoSql, ConstantsUtils.ORDER_INFO_STATUS_WAIT_RECEIVE,buyer_id);
            waitReceive = getOrderCount(orderInfoSql, ConstantsUtils.ORDER_INFO_STATUS_ALREADY,buyer_id);
            //finished = getOrderCount(orderInfoSql, ConstantsUtils.ORDER_INFO_STATUS_FINISHED,buyer_id);
            cancel = getOrderCount(orderInfoSql, ConstantsUtils.ORDER_INFO_STATUS_CANCEL,buyer_id);
           /* orderUserPage = order_info.dao.fullPaginate(pageNumber, page_step, orderInfoSql);
            order_userList = orderUserPage == null ? new ArrayList<order_info>() : orderUserPage.getList();*/
        }
        //订单实体
        //返回订单列表
        order_info orderInfo = new order_info();
        //今日订单数
        long orderNum = orderInfo.getDayTotalOrder(buyer_id);
        //今日交易额
        BigDecimal totalPrice = orderInfo.getDayTotalPrice(buyer_id);

        resulfinal.put("count", orderNum);
        resulfinal.put("total_price", totalPrice == null ? 0 : totalPrice);
        resulfinal.put("waitPay", waitPay);
        //resulfinal.put("waitSend", waitSend);
        resulfinal.put("waitReceive", waitReceive);
        //resulfinal.put("finished", finished);
        resulfinal.put("cancel", cancel);
        return resulfinal;
    }

    /*   //通过手机号或者名称获取订单数量
       private Long getOrderCountByName(String buyer_name_num, String orderInfoSql,int status) {
           return order_info.dao.findFirst(orderInfoSql, "%" + buyer_name_num + "%", status).<Long>get("cn");
       }
   */
    //获取订单数量
    private Long getOrderCount(String orderInfoSql, Object... objects) {
        return order_info.dao.findFirst(orderInfoSql, objects).<Long>get("cn");
    }

}
