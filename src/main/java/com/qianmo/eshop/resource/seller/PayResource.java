package com.qianmo.eshop.resource.seller;


import cn.dreampie.route.annotation.*;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.seller.seller_bank;
import com.qianmo.eshop.model.seller.seller_pay;
import com.qianmo.eshop.model.user.user_info;

import java.util.HashMap;
import java.util.List;

/**
 * 卖家支付账号api
 * Created by ccq on 16-1-1.
 */
@API("/pay")
public class PayResource extends SellerResource {

    /**
     * 获取卖家支付管理的信息列表
     *
     * @return
     */
    @GET
    public HashMap details() {
        HashMap result = new HashMap();
        //从session中取出seller_id
        Long seller_id = SessionUtil.getAdminId();
        //获取支付方式
        String payListSql = YamlRead.getSQL("getPayList", "seller/seller");
        List<seller_pay> payList = seller_pay.dao.find(payListSql, seller_id);

        result.put("seller_pay", payList);


        return result;
    }

    /**
     * 编辑卖家支付管理的信息
     *
     * @param seller_pay 待编辑的卖家支付管理实体
     * @return
     */
    @PUT("/:id")
    public HashMap edit(long id,seller_pay seller_pay) {
        HashMap result;
        seller_pay SellerPay = seller_pay.dao.findById(id);
        result = CommonUtils.EditreturnCodeMessage(false);
        if (SellerPay != null) {
            seller_pay.set("id", id);
            if (seller_pay.update()) {
                result = CommonUtils.EditreturnCodeMessage(true);
            }
        }
        return result;
    }

    /**
     * 获取银行汇款的银行列表
     *
     * @return
     */
    @GET("/bank")
    public HashMap getBankList() {
        HashMap result = new HashMap();
        //从session中取出seller_id
        Long seller_id = SessionUtil.getAdminId();
        //获取银行列表
        String bankListSql = YamlRead.getSQL("getBankList", "seller/seller");
        List<seller_pay> bankList = seller_pay.dao.find(bankListSql, seller_id);
        result.put("pay_bank_list", bankList);
        return result;
    }


    /**
     * 编辑银行汇款的银行信息
     * @param id          银行信息ID
     * @param pay_bank 待编辑的银行信息实体
     * @return
     */
    @PUT("/bank/:id")
    public HashMap editBank(long id, seller_bank pay_bank) {
        HashMap result;
        seller_bank SellerBank = seller_bank.dao.findById(id);
        result = CommonUtils.EditreturnCodeMessage(false);
        if (SellerBank != null) {
            pay_bank.set("id", id);
            if (pay_bank.update()) {
                result = CommonUtils.EditreturnCodeMessage(true);
            }
        }
        return result;
    }


    /**
     * 添加银行汇款的银行信息
     *
     * @param pay_bank 待添加的银行信息实体
     * @return
     */
    @POST("/bank")
    public HashMap addBank(seller_bank pay_bank) {
        HashMap result;
        result = CommonUtils.AddreturnCodeMessage(false);
        if (pay_bank != null) {
            pay_bank.set("area_id", ConstantsUtils.ALL_AREA_ID);
            pay_bank.set("seller_id", SessionUtil.getAdminId());
            pay_bank.set("name",pay_bank.get("bank_name"));
            if (pay_bank.save()) {
                result = CommonUtils.AddreturnCodeMessage(true);
            }
        }
        return result;
    }


    /**
     * 删除银行汇款的银行信息
     *
     * @param id 银行信息ID
     * @return
     */
    @DELETE("/bank/:id")
    public HashMap delete(long id) {
        HashMap result = new HashMap();
        result = CommonUtils.DelreturnCodeMessage(false);
        if (seller_bank.dao.deleteById(id)) {
            result = CommonUtils.DelreturnCodeMessage(true);
        }
        return result;
    }

}