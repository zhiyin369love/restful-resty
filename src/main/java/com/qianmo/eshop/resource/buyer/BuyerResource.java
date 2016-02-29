package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.HashMap;


/**
 * 买家api
 * Created by ccq on 16-1-1.
 */
@API("/buyer")
public class BuyerResource extends ApiResource {


  @GET
  public HashMap getList() {
    HashMap result = new HashMap();
    return result;
  }


  @GET("/:id")
  public HashMap get(int id) {
    HashMap result = new HashMap();
    return result;
  }

  @POST
  public WebResult add() {return new WebResult(HttpStatus.CREATED,"添加成功");}

  @PUT
  @Transaction
  public WebResult update() {
    return new WebResult(HttpStatus.CREATED,"编辑成功");
  }

  @DELETE("/:id")
  public WebResult delete(int id) {
    return new WebResult(HttpStatus.OK,"删除成功");
  }

}