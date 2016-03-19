package com.qianmo.eshop.resource.buyer;


import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.seller.seller_bank;
import com.qianmo.eshop.model.seller.seller_pay;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付方式和支付银行
 *
 * @author wss
 */
@API("/order/pay")
public class OrderPayResouce extends BuyerResource {
    /**
     * @param id
     * @param seller_id
     * @return
     */
    @GET
    public Map getList(Long id, Long seller_id) {
        long buyerId = SessionUtil.getUserId();
        Map result = new HashMap();
        List<buyer_seller> buyerSellerList =  buyer_seller.dao.findBy(" buyer_id = ? and seller_id = ?", buyerId, seller_id);
        if(buyerSellerList == null || buyerSellerList.size() <= 0) {
            return CommonUtils.getCodeMessage(false,"非本人绑定的经销商");
        }
        //获取支付方式sql
        String getPaySql = YamlRead.getSQL("getSellPayList", "buyer/order");
        //获取支付银行列表sql
        String getPayBankList = YamlRead.getSQL("getFieldSellerBankAll", "buyer/order");
        //返回结果列表
        List<HashMap> resultMapList = new ArrayList<HashMap>();
        //HashMap result2 = new HashMap();
        List<seller_pay> sellPayList;
        //如果id不为空值，则需要根据id去查找
        if (id != null && id != 0) {
            sellPayList = new ArrayList<seller_pay>();
            sellPayList.add(seller_pay.dao.findById(id));
        } else {
            sellPayList = seller_pay.dao.find(getPaySql, seller_id);
        }
        if (sellPayList != null && sellPayList.size() > 0) {
            for (seller_pay sellPay : sellPayList) {
                HashMap sellPayMap = new HashMap();
                sellPayMap.put("details", sellPay.get("details"));
                sellPayMap.put("pay_id", sellPay.get("pay_id"));
                sellPayMap.put("pay_name", sellPay.get("pay_name"));
                //如果是在线支付，那么需要查找银行列表
                if (ConstantsUtils.PAY_TYPE_NAME_OFFLINE.equals(sellPay.get("pay_name"))) {
                    List<seller_bank> sellerBankList = seller_bank.dao.find(getPayBankList, seller_id);
                    sellPayMap.put("pay_bank_list", sellerBankList);
                }
                resultMapList.add(sellPayMap);
            }
        }
        result.put("seller_pay_list",resultMapList);
        return result;
    }
}
