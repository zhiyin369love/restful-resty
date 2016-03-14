package com.qianmo.eshop.resource.buyer;

import cn.dreampie.route.annotation.*;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.model.buyer.buyer_receive_address;


import java.util.HashMap;


/**
 * 买家收货地址api
 * Created by ccq on 16-1-1.
 */
@API("/address")
public class AddressResource extends BuyerResource {
    @GET
    public HashMap List() {
        HashMap result = new HashMap();
        result.put("buyer_address_list", buyer_receive_address.dao.List(Subject.getPrincipal().getModel().get("id")));
        return result;
    }

    @GET("/:id")
    public HashMap Details(long id) {
        HashMap result = new HashMap();
        result.put("buyer_address", buyer_receive_address.dao.Details(id));

        return result;
    }

    @PUT("/:id")
    public HashMap Edit(long id, buyer_receive_address buyer_address) {
        HashMap result = CommonUtils.EditreturnCodeMessage(false);
        if (buyer_receive_address.dao.Edit(id, buyer_address)) {
            result = CommonUtils.EditreturnCodeMessage(true);
        }
        return result;
    }

    @POST
    public HashMap Add(String buyer_id, buyer_receive_address buyer_address) {
        HashMap result = CommonUtils.AddreturnCodeMessage(false);
        if (buyer_receive_address.dao.Add(buyer_address)) {
            result = CommonUtils.AddreturnCodeMessage(true);
        }
        return result;
    }

    @DELETE("/:id")
    public HashMap Delete(long id) {
        HashMap result  = CommonUtils.DelreturnCodeMessage(false);
        if (buyer_receive_address.dao.Delete(id)) {
            result = CommonUtils.DelreturnCodeMessage(true);
        }
        return result;
    }


}