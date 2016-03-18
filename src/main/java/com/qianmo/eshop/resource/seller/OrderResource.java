package com.qianmo.eshop.resource.seller;

import cn.dreampie.orm.page.Page;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.PUT;
import com.alibaba.druid.util.StringUtils;
import com.qianmo.eshop.common.*;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.credit.credit;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 买家获取单个订单详情   和   操作订单
 *
 * @author :wss
 */

@API("/order")
public class OrderResource extends SellerResource {
    /**
     * 查看单个订单详情
     *
     * @param id 订单ID
     * @return
     */
    @GET("/:id")
    public HashMap getOrderDetail(Long id) {
        HashMap result = new HashMap();
        //订单实体查询sql
        String sqlOrderInfo = YamlRead.getSQL("getFieldOrderInfoAll", "seller/order");
        //订单备注列表查询sql
        String sqlOrderRemark = YamlRead.getSQL("getFirldOrderRemarkAll", "seller/order");
        //商品实体列表查询sql
        List<HashMap> resultMap = getOrderHashMaps(id);

        //买家信息实体查询sql
        HashMap resultBuyerAll = new HashMap();
        String sqlBuyerInfo = YamlRead.getSQL("getFieldBuyerInfoAll", "seller/order");
        //买家收货人实体
        String sqlBuyerReceive = YamlRead.getSQL("getFieldBuyerReceiveAll", "seller/order");
        order_user o = order_user.dao.findFirst(sqlBuyerInfo, id);
        /*if (order_user_list != null && order_user_list.size() > 0) {
            o = order_user.dao.find(sqlBuyerInfo, id).get(0);
        }*/
        resultBuyerAll.put("buyer_id", o.get("buyer_id"));
        resultBuyerAll.put("buyer_name", o.get("name"));
        resultBuyerAll.put("buyer_receive", buyer_receive_address.dao.findFirst(sqlBuyerReceive, id));
        //返回json
        result.put("buyer_info", resultBuyerAll);
        result.put("goods_list", resultMap);
        result.put("order_info", order_info.dao.findFirst(sqlOrderInfo, id));
        result.put("order_remark_list", order_remark.dao.find(sqlOrderRemark, id));
        return result;

    }

    /**
     * @param id     订单ID
     * @param op     操作状态
     * @param remark 备注
     * @return
     */
    @PUT("/:id")
    public Map opOrder(Integer id, int op, String remark) {

        if (op == ConstantsUtils.SELLER_ORDER_OP_PAY_TYPE) {
            //收到货款
            order_info.dao.update("update order_info set pay_status = ?  where num = ? ", ConstantsUtils.ORDER_PAYMENT_STATUS_RECEIVED, id);
        } else if (op == ConstantsUtils.SELLER_ORDER_OP_FAHUO) {
            //发货
            order_info.dao.update("update order_info set status = ?  where num = ? ", ConstantsUtils.ORDER_INFO_STATUS_WAIT_RECEIVE, id);
            new order_remark().set("order_num", id).set("op", op).set("details", remark).save();
        } else if (op == ConstantsUtils.SELLER_ORDER_OP_PAY_STATUS) {
            //取消
            order_info.dao.update("update order_info set status = ?  where id = ? ", ConstantsUtils.ORDER_INFO_STATUS_CANCEL, id);
            new order_remark().set("order_num", id).set("op", op).set("details", remark).save();
        } else if (op == ConstantsUtils.SELLER_ORDER_OP_PAY_GOODS) {
            //卖家备注订单
            new order_remark().set("order_num", id).set("op", op).set("details", remark).save();
        } else if (op == ConstantsUtils.SELLER_ORDER_OP_PAY_CELL) {
            //当卖家同意买家赊账时
            HashMap result3 = new HashMap();
            String creditorder = YamlRead.getSQL("getFileCreditOrderUserAll", "seller/order");
            order_user o = order_user.dao.findFirst(creditorder, id);
            new credit().set("area_id", ConstantsUtils.ALL_AREA_ID).set("order_num", id).set("status", 0).set("buyer_id", o.get("buyer_id")).set("seller_id", o.get("seller_id")).save();
        } else {
            //当卖家不同意买家赊账时订单取消 op==5时
            order_info.dao.update("update order_info set status = ? where id = ? ", ConstantsUtils.ORDER_INFO_STATUS_CANCEL, id);    //注：除了要删除订单主表之外，可能还要删除其他关联表，“待开发”
        }
        return CommonUtils.getCodeMessage(true,"操作订单成功");
    }

    /**
     * 获取订单列表
     *
     * @param buyer_name_num 零售商名称或订单号
     * @param data_end       结束时间
     * @param data_start     开始时间
     * @param order_status   订单状态
     * @param page_start     第几条开始
     * @param page_step      返回多少条
     * @return
     */
    @GET
    public HashMap getOrderList(String buyer_name_num, String data_end, String data_start, Integer order_status, Integer page_start, Integer page_step) {
        //通过session获取当前登录用户
        long seller_id = SessionUtil.getUserId();
        //查找订单信息sql
        String orderInfoSql = "select * from order_info a where 1=1 ";
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
                        "    LEFT JOIN user_info c ON b.buyer_id = c.id where c.name like ? ";
            }
        }
        //当天开始时间
        if (StringUtils.isEmpty(data_start)) {
            orderInfoSql += " and a.created_at >= date(sysdate())";
        } else {
            orderInfoSql += " and a.created_at >=" + DateUtils.formatDate(data_start, DateUtils.format_yyyyMMdd);
        }
        //当天结束时间 TODO 当天开始时间和结束时间是同一天时，前台怎么传值，是传同一天的数据吗？
        if (StringUtils.isEmpty(data_end)) {
            orderInfoSql += " and a.created_at <= DATE_ADD(DATE(SYSDATE()), INTERVAL 1 DAY)";
        } else {
            orderInfoSql += " and a.created_at <=" + DateUtils.formatDate(data_end, DateUtils.format_yyyyMMdd);
        }
        //当开始时间和结束时间相等时
        if (data_end == data_start) {
            orderInfoSql += " and Date(a.created_at) = " + DateUtils.formatDate(data_start, DateUtils.format_yyyyMMdd);
        }
        //订单状态
        if (order_status != null) {
            orderInfoSql += " and a.status =  " + order_status;
        }
        if (page_start == null || page_start == 0) {
            page_start = ConstantsUtils.DEFAULT_PAGE_START;
        }
        if (page_step == null || page_step == 0) {
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
        }
        int pageNumber = page_start / page_step + 1;
        Page<order_info> orderUserPage = null;
        List<order_info> order_userList = null;
        //判断buyer_name_num是订单号还是商家名称
        if (exitsBuyerNameNum) {
            orderUserPage = order_info.dao.paginate(pageNumber, page_step, orderInfoSql, "%" + buyer_name_num + "%");
            order_userList = orderUserPage == null ? new ArrayList<order_info>() : orderUserPage.getList();
        } else {
            orderUserPage = order_info.dao.paginate(pageNumber, page_step, orderInfoSql);
            order_userList = orderUserPage == null ? new ArrayList<order_info>() : orderUserPage.getList();
        }
        //订单实体
        //返回订单列表
        List<HashMap> resultMapList = new ArrayList<HashMap>();
        HashMap resulfinal = new HashMap();
        if (order_userList != null && order_userList.size() > 0) {
            for (order_info orderInfo : order_userList) {
                HashMap orderMap = new HashMap();
                orderMap = getOrderDetail(orderInfo.<Long>get("id"));
                resultMapList.add(orderMap);
                // resulfinal.put("order_total", count2);  //4
            }
            long orderNum = order_info.dao.findFirst("select count(*) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CANCEL).<Integer>get("cn");
            //今日交易额
            BigDecimal totalPrice = new BigDecimal(order_info.dao.findFirst("select sum(total_price) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CANCEL).<Integer>get("cn"));
            //double totalPrice = order_info.dao.findFirst("select sum(total_price) cn from order_info where seller_id = ?  and status != ? and date(created_at) = date(sysdate())", seller_id, ConstantsUtils.ORDER_INFO_STATUS_CANCEL).<Integer>get("cn");
            resulfinal.put("count", orderNum);
            resulfinal.put("total_price", totalPrice);
            resulfinal.put("order_list", resultMapList);
            resulfinal.put("total_count", resultMapList.size());
        }
        return resulfinal;
    }

    //商品实体封装
    public List<HashMap> getOrderHashMaps(long id) {

        //商品信息
        String sqlGoodInfo = YamlRead.getSQL("getFirldGoodsInfoAll", "seller/order");
        //商品规格列表
        String sqlGoodsSku = YamlRead.getSQL("getFieldGoodsSkuAll", "seller/order");
        //订单编号
        order_info num = order_info.dao.findFirst("select num from order_info where id = ? ", id);
        //商品分类
        String sqlGoodType = YamlRead.getSQL("getFieldGoodsTypeALL", "seller/order");

        Map goodsResult = new HashMap();
        List<goods_info> goods_infoList = goods_info.dao.find(sqlGoodInfo, id);
        List<HashMap> resultMap = new ArrayList<HashMap>();
        for (goods_info goodlist : goods_infoList) {
            HashMap resultGoods = new HashMap();
            // goods_info goods_info_list = goods_info.dao.findFirst(sqlGoodInfo,id);
            long goodsNum = goodlist.get("number");
            long orderNum = num.get("num");
            long category_id = goodlist.get("category_id");
            resultGoods.put("goods_sku_list", goods_sku.dao.find(sqlGoodsSku, goodsNum, orderNum));
            resultGoods.put("goods_type", goods_category.dao.find(sqlGoodType, category_id));
            resultGoods.put("goods_info", goods_info.dao.findFirstBy(" num = ? ", goodsNum));
            resultMap.add(resultGoods);
        }
        return resultMap;
    }
}
