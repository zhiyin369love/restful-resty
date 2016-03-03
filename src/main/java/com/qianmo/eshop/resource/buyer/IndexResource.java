package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.route.annotation.*;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;
import com.qianmo.eshop.common.ConstantsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 * <p>
 * 首页api
 * Created by zhangyang on 16-03-01
 * </p>
 */
@API("/buyer")
public class IndexResource extends ApiResource {


  /**
   *
   * 买家绑定卖家接口
   * 通过验证码查找卖家，然后买家绑定卖家
   *
   * @param bind_code  绑定码
   * @param buyer_id   买家用户id
   */
  @POST("/bind")
  public WebResult addBuyerSeller(int bind_code, int buyer_id) {
    try {
      //通过验证码找卖家id
      invite_verify_code code = getInviteByVerifyCode(bind_code);
      if (code != null) {
        Long seller_Id = code.<Long>get("user_id");
        //查看是否已经绑定过
        buyer_seller buyerSeller = buyer_seller.dao.findFirstBy("buyer_id = ? and seller_id = ? and  status = 0 ", buyer_id, seller_Id);
        if (buyerSeller == null) {
          //如果没有绑定，则将买家卖家绑定起来
          buyer_seller.dao.set("area_id",  ConstantsUtils.ALL_AREA_ID).set("buyer_id", buyer_id).set("seller_id", seller_Id).set("status", ConstantsUtils.BUYER_SELLER_STATUS_BIDING_CANCEL).save();
          code.set("status",ConstantsUtils.INVITE_CODE_STATUS_SUCCESSED).save();
          return new WebResult(HttpStatus.OK, "绑定成功");
        } else {
          //如果已经绑定过，提示已经绑定过
          return new WebResult(HttpStatus.OK, "已经绑定过");
        }
      } else {
        //如果找不到，提示验证码错误
        return new WebResult(HttpStatus.EXPECTATION_FAILED, "验证码错误");
      }
    } catch (Exception e) {
        //异常情况，按理说需要记录日志 TODO
        return new WebResult(HttpStatus.EXPECTATION_FAILED, "验证码错误");
    }
  }


  /**
   *
   * 根据验证码获取经销商信息
   *
   * @param bind_code  绑定码
   * @param buyer_id   买家用户id
   */
  @GET("/seller")
  public HashMap getSellerInfoByVerifyCode(int bind_code, int buyer_id) {
    HashMap resultMap = new HashMap();
    try {
      //通过验证码找卖家id
      invite_verify_code code = getInviteByVerifyCode(bind_code);
      if (code != null) {
        Long seller_Id = code.<Long>get("user_id");
        user_info sellInfo = user_info.dao.findById(seller_Id)==null?new user_info():user_info.dao.findById(seller_Id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("address_full",sellInfo.get("province_name")==null?"":sellInfo.get("province_name").toString() + sellInfo.get("city_name")==null?"":sellInfo.get("city_name").toString() + sellInfo.get("county_name")==null?"":sellInfo.get("county_name").toString() + sellInfo.get("town_name")==null?"":sellInfo.get("town_name").toString() +sellInfo.get("address")==null?"":sellInfo.get("address").toString());
        jsonObject.put("seller_id",sellInfo.get("id"));
        resultMap.put("seller_info",jsonObject);
      } else {
        //如果找不到，返回空
        resultMap.put("seller_info",null);
      }
      return resultMap;
    } catch (Exception e) {
      //异常情况，按理说需要记录日志 TODO
      resultMap.put("seller_info",null);
      return resultMap;
    }
  }


  /**
   *
   * 获取首页汇总信息
   *
   * @param buyer_id   买家用户id
   */
  @GET("/total")
  public HashMap getIndexSummary(int buyer_id) {
    HashMap resultMap = new HashMap();
    HashMap total = new HashMap();
    try {
      //我要采购
      long cartNum = cart.dao.findFirst("select count(*) cn from cart where buyer_id = ?", buyer_id).<Long>get("cn");
      //客服号码   如果买家关联多个卖家，那么客服电话怎么展示
      //String phone = user_info.dao.findById(buyer_id).<String>get("phone");
      //上级经销商
      List<buyer_seller>  sellerlist =  buyer_seller.dao.findBy("buyer_id = ? and status = 0 ", buyer_id);
      List<JSONObject> resultSellerList = new ArrayList<JSONObject>();
      //循环截取所需的字段
      if(sellerlist != null && sellerlist.size() > 0) {
        for(buyer_seller sell : sellerlist) {
          buyer_seller tempSeller = new buyer_seller();
          user_info userInfo = user_info.dao.findById(sell.get("seller_id"))==null?new user_info():user_info.dao.findById(sell.get("seller_id"));
          JSONObject jsonObject = new JSONObject();
          jsonObject.put("seller_id",sell.get("seller_id"));
          jsonObject.put("seller_name",userInfo.get("nickname"));
          jsonObject.put("phone",userInfo.get("phone"));
          resultSellerList.add(jsonObject);
        }
        total.put("cart_count",cartNum);
        //total.put("phone",phone);
        total.put("seller_list",resultSellerList);
        //待付款订单
        long payWait = order_info.dao.findFirst("SELECT COUNT(*) cn FROM order_info a  LEFT JOIN order_user b" +
                "                ON a.num = b.order_num" +
                "                WHERE  a.pay_status = ? and b.order_num IS NOT NULL AND b.buyer_id = ? AND b.area_id = ?", ConstantsUtils.ORDER_PAYMENT_STATUS_WAITE, buyer_id, ConstantsUtils.ALL_AREA_ID) .<Long>get("cn");

        //待收货订单
        long  receiveWait = order_info.dao.findFirst("SELECT COUNT(*) cn FROM order_info a  LEFT JOIN order_user b" +
                "                ON a.num = b.order_num" +
                "                WHERE  a.status = ? and b.order_num IS NOT NULL AND b.buyer_id = ? AND b.area_id = ?", ConstantsUtils.ORDER_INFO_STATUS_WAIT_RECEIVE, buyer_id, ConstantsUtils.ALL_AREA_ID) .<Long>get("cn");
        total.put("todo_pay_count",receiveWait);
        total.put("todo_receive_count",receiveWait);
        resultMap.put("total",total);
        return resultMap;
      }  else {
        return resultMap;
      }

    } catch (Exception e) {
      //异常情况，按理说需要记录日志 TODO
      resultMap.put("total",null);
      return resultMap;
    }
  }

  public invite_verify_code getInviteByVerifyCode(int bindCode) {
     return new invite_verify_code().getInviteByCode(bindCode, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
  }
}