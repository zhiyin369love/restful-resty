package com.qianmo.eshop.resource.buyer;

import cn.dreampie.route.annotation.*;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.model.buyer.buyer_receive_address;


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


}