package com.qianmo.eshop.model.order;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.ConstantsUtils;

import java.math.BigDecimal;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "order_info")
public class order_info extends Model<order_info> {
    public final static order_info dao = new order_info();


    /**
     * 根据卖家id获取当日交易总金额
     * @param seller_id
     */
    public BigDecimal getDayTotalPrice(long seller_id) {
        return order_info.dao.findFirst("select sum(total_price) totalPice from order_info oi left join order_user ou on oi.num = ou.order_num where ou.seller_id = ?  and oi.status != ? and date(oi.created_at) = date(sysdate())",seller_id, ConstantsUtils.ORDER_INFO_STATUS_CANCEL).<BigDecimal>get("totalPice");
    }

    /**
     * 根据卖家id获取当日总订单数
     * @param seller_id
     */
    public long getDayTotalOrder(long seller_id) {
       return  order_info.dao.findFirst("select count(*) cn from order_info oi left join order_user ou on oi.num = ou.order_num where ou.seller_id = ?   and date(oi.created_at) = date(sysdate())", seller_id).<Long>get("cn");
    }



    /**
     * 根据买家id获取当日交易总金额
     * @param buyer_id
     */
    public BigDecimal getBuyerDayTotalPrice(long buyer_id) {
        return order_info.dao.findFirst("select sum(total_price) totalPice from order_info oi left join order_user ou on oi.num = ou.order_num where ou.buyer_id = ?  and oi.status != ? and date(oi.created_at) = date(sysdate())",buyer_id, ConstantsUtils.ORDER_INFO_STATUS_CANCEL).<BigDecimal>get("totalPice");
    }

    /**
     * 根据mai买家id获取当日总订单数
     * @param buyer_id
     */
    public long getBuyerDayTotalOrder(long buyer_id) {
        return  order_info.dao.findFirst("select count(*) cn from order_info oi left join order_user ou on oi.num = ou.order_num where ou.buyer_id = ?   and date(oi.created_at) = date(sysdate())", buyer_id).<Long>get("cn");
    }



}
