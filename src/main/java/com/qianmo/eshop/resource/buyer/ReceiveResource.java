package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import com.qianmo.eshop.model.buyer.buyer_receive_address;


import java.util.HashMap;


/**
 * 买家收货地址api
 * Created by ccq on 16-1-1.
 */
@API("/receive")
public class ReceiveResource extends BuyerResource {


  @GET
  public HashMap List(long buyer_id) {
    HashMap result = new HashMap();
    result.put("buyer_receive_list", buyer_receive_address.dao.List(buyer_id));
    return result;
  }

  @GET("/:id")
  public HashMap Details(long id) {
    HashMap result = new HashMap();
    result.put("buyer_receive_list", buyer_receive_address.dao.Details(id));

    return result;
  }

  @PUT("/:id")
  public HashMap Edit(long id,buyer_receive_address model) {
    HashMap result = new HashMap();
    int code;
    String message;

    if(buyer_receive_address.dao.Edit(id, model)){
      code = HttpStatus.CREATED.getCode();
      message = "编辑成功";
    }
    else{
      code = HttpStatus.NOT_FOUND.getCode();
      message = "编辑失败";
    }

    result.put("code", code);
    result.put("message", message);
    return result;
  }

  @POST
  public HashMap Add(buyer_receive_address model) {
    HashMap result = new HashMap();
    int code;
    String message;

    if(buyer_receive_address.dao.Add(model)){
      code = HttpStatus.CREATED.getCode();
      message = "添加成功";
    }
    else{
      code = HttpStatus.NOT_FOUND.getCode();
      message = "添加失败";
    }

    result.put("code", code);
    result.put("message", message);
    return result;
  }

  @DELETE("/:id")
  public HashMap Delete(long id) {
    HashMap result = new HashMap();
    int code;
    String message;

    if(buyer_receive_address.dao.Delete(id)){
      code = HttpStatus.CREATED.getCode();
      message = "删除成功";
    }
    else{
      code = HttpStatus.NOT_FOUND.getCode();
      message = "删除失败";
    }

    result.put("code", code);
    result.put("message", message);
    return result;
  }


}