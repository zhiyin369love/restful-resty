package com.qianmo.eshop.resource.buyer;


import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.seller.seller_bank;
import com.qianmo.eshop.model.seller.seller_pay;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付方式和支付银行
 * author:wss
 *  传入参数： id：支付方式id  、seller_id ：卖家id
 */
@API("/pay/order")
public class OrderPayResouce extends BuyerResource {
    @GET
    public List<HashMap> getList(Long id,Long seller_id){
       //long seller_id = SessionUtil.getUserId();
        HashMap result = new HashMap();

            //获取支付方式sql
            String getPaySql = YamlRead.getSQL("getSellPayList","buyer/order");
            //获取支付银行列表sql
            String getPayBankList = YamlRead.getSQL("getFieldSellerBankAll","buyer/order");
            //返回结果列表
            List<HashMap> resultMapList = new ArrayList<HashMap>();
            //HashMap result2 = new HashMap();
            List<seller_pay> sellPayList = null;
            //如果id不为空值，则需要根据id去查找
            if(id != null && id !=0) {
                sellPayList = new ArrayList<seller_pay>();
                sellPayList.add(seller_pay.dao.findById(id));
            } else {
                sellPayList = seller_pay.dao.find(getPaySql,seller_id);
            }
            HashMap sellPayMap = new HashMap();
            if(sellPayList != null && sellPayList.size() >0) {
                for(seller_pay sellPay : sellPayList) {
                    sellPayMap.clear();
                    //如果是在线支付，那么需要查找银行列表
                    if(ConstantsUtils.PAY_TYPE_NAME_OFFLINE.equals(sellPay.get("pay_name"))) {
                        List<seller_bank> sellerBankList =  seller_bank.dao.find(getPayBankList,seller_id);
                        sellPayMap.put("pay_bank_list",sellerBankList);
                        sellPayMap.put("details",sellPay.get("details"));
                        sellPayMap.put("pay_id",sellPay.get("pay_id"));
                        sellPayMap.put("pay_name",sellPay.get("pay_name"));
                        sellPayMap.put("seller_id",sellPay.get("seller_id"));
                        sellPayMap.put("status",sellPay.get("status"));
                        resultMapList.add(sellPayMap);
                    }
                }
            }

            //result2.put("pay_bank_list",);
            //支付方式相关查询sql
           /* String sql2 = YamlRead.getSQL("getFieldSellerPayAll","buyer/order");
            seller_pay o = new seller_pay();
            if(seller_pay.dao.find(sql2,id)!=null && seller_pay.dao.find(sql2,id).size()>0){
                o = seller_pay.dao.find(sql2,id).get(0);
            }*/
           /* seller_pay o = seller_pay.dao.findFirstBy("seller_id = ?",id);
            result2.put("details",o.get("details"));
            result2.put("pay_id",o.get("id"));
            result2.put("pay_name",o.get("name"));
            result2.put("seller_id",o.get("seller_id"));
            result2.put("status",o.get("status"));

            result.put("seller_pay",result2);*/
            return resultMapList;

    }

}
