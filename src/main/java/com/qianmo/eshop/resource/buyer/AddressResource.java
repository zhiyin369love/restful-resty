package com.qianmo.eshop.resource.buyer;

import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.user.user_info;


import java.util.HashMap;


/**
 * 买家收货地址api
 * Created by ccq on 16-1-1.
 */
@API("/address")
public class AddressResource extends BuyerResource {
    @GET
    public HashMap list() {
        HashMap result = new HashMap();
        result.put("buyer_address_list", buyer_receive_address.dao.list(SessionUtil.getUserId()));
        return result;
    }

    @GET("/:id")
    public HashMap details(long id) {
        HashMap result = new HashMap();
        result.put("buyer_address", buyer_receive_address.dao.details(id));

        return result;
    }

    @PUT("/:id")
    public HashMap edit(long id, buyer_receive_address buyer_address) {
        HashMap result = CommonUtils.EditreturnCodeMessage(false);
        if (buyer_receive_address.dao.edit(id, buyer_address)) {
            result = CommonUtils.EditreturnCodeMessage(true);
        }
        return result;
    }

    @POST
    public HashMap add(buyer_receive_address buyer_address) {
        HashMap result = CommonUtils.AddreturnCodeMessage(false);
        if (buyer_receive_address.dao.add(buyer_address)) {
            result = CommonUtils.AddreturnCodeMessage(true);
        }
        return result;
    }

    @DELETE("/:id")
    public HashMap delete(long id) {
        HashMap result  = CommonUtils.DelreturnCodeMessage(false);
        if (buyer_receive_address.dao.delete(id)) {
            result = CommonUtils.DelreturnCodeMessage(true);
        }
        return result;
    }

    /**
     * 将收获地址设为默认地址
     * @param id
     * @return
     */
    @PUT("/default/:id")
    @Transaction
    public HashMap isdefault(long id){
        HashMap result = CommonUtils.getCodeMessage(false, "设置默认地址失败");
        try {
            user_info userInfo = SessionUtil.getUser();
            String updateSql = "UPDATE buyer_receive_address SET isdefault = 1 WHERE id = ? AND buyer_id = ?";
            buyer_receive_address.dao.update(updateSql,id,userInfo.get("id"));
            updateSql = "UPDATE buyer_receive_address SET isdefault = 0 WHERE id != ? AND buyer_id = ?";
            buyer_receive_address.dao.update(updateSql,id,userInfo.get("id"));
            result = CommonUtils.getCodeMessage(true, "设置默认地址成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}