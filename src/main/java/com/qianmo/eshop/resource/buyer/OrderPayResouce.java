package com.qianmo.eshop.resource.buyer;


import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.seller.seller_bank;
import com.qianmo.eshop.model.seller.seller_pay;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 支付方式和支付银行
 * author:wss
 *  传入参数： id：支付方式id  、seller_id ：卖家id
 */
@API("/pay/order")
public class OrderPayResouce extends BuyerResource {
    @GET
    public HashMap getList(int id){
       long seller_id = SessionUtil.getUserId();
        HashMap result = new HashMap();
        try {
            //支付银行列表查询sql
            String sql1 = YamlRead.getSQL("getFieldSellerBankAll","buyer/order");
            HashMap result2 = new HashMap();
            result2.put("pay_bank_list",seller_bank.dao.find(sql1,seller_id));
            //支付方式相关查询sql
            String sql2 = YamlRead.getSQL("getFieldSellerPayAll","buyer/order");
            seller_pay o = new seller_pay();
            if(seller_pay.dao.find(sql2,id)!=null && seller_pay.dao.find(sql2,id).size()>0){
                o = seller_pay.dao.find(sql2,id).get(0);
            }
            result2.put("details",o.get("details"));
            result2.put("pay_id",o.get("id"));
            result2.put(" pay_name",o.get(" name"));
            result2.put("seller_id",o.get("seller_id"));
            result2.put("status",o.get("status"));

            result.put("seller_pay",result2);
            return result;
        }catch (Exception e) {
            //异常情况，方便记录日志 TODO
            return null;
        }
    }

}
