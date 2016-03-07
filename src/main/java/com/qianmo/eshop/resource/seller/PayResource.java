package com.qianmo.eshop.resource.seller;


import cn.dreampie.route.annotation.*;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.model.seller.seller_bank;
import com.qianmo.eshop.model.seller.seller_pay;
import com.qianmo.eshop.model.user.user_info;

import java.util.HashMap;

/**
 * 卖家支付账号api
 * Created by ccq on 16-1-1.
 */
@API("/pay")
public class PayResource extends SellerResource {

    /**
     * 获取卖家支付管理的信息列表
     * @param seller_id 卖家ID
     * @return
     */
    @GET
    public HashMap Details(long seller_id) {
        HashMap result = new HashMap();
        result.put("seller_pay", seller_pay.dao.findById(seller_id));
        return result;
    }

    /**
     * 编辑卖家支付管理的信息
     * @param id 支付ID
     * @param seller_pay 待编辑的卖家支付管理实体
     * @return
     */
    @PUT("/:id")
    public HashMap Edit(long id,seller_pay seller_pay) {
        HashMap result;
        seller_pay SellerPay = seller_pay.dao.findById(id);
        result = CommonUtils.EditreturnCodeMessage(false);
        if(SellerPay != null){
            seller_pay.set("id",id);
            if (seller_pay.update()){
                result = CommonUtils.EditreturnCodeMessage(true);
            }
        }
        return result;
    }

    /**
     * 获取银行汇款的银行列表
     * @param seller_id 卖家ID
     * @return
     */
    @GET("/bank")
    public HashMap getBankList(long seller_id) {
        HashMap result = new HashMap();
        result.put("bank_list", seller_bank.dao.findBy("seller_id",seller_id));
        return result;
    }

    /**
     * 编辑银行汇款的银行信息
     * @param id 银行汇款
     * @param seller_bank 待编辑的银行信息实体
     * @return
     */
    @PUT("/bank/:id")
    public HashMap Edit(long id,seller_bank seller_bank) {
        HashMap result;
        seller_bank SellerBank = seller_bank.dao.findById(id);
        result = CommonUtils.EditreturnCodeMessage(false);
        if(SellerBank != null){
            seller_bank.set("id",id);
            if (seller_bank.update()){
                result = CommonUtils.EditreturnCodeMessage(true);
            }
        }
        return result;
    }

    /**
     * 删除银行汇款的银行信息
     * @param id 银行信息ID
     * @return
     */
    @DELETE("/bank/:id")
    public HashMap Delete(long id) {
        HashMap result = new HashMap();
        result = CommonUtils.DelreturnCodeMessage(false);
        if(seller_bank.dao.deleteById(id)){
            result = CommonUtils.DelreturnCodeMessage(true);
        }
        return result;
    }

}