package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.*;
import cn.dreampie.route.config.ResourceLoader;
import cn.dreampie.route.core.Resource;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.*;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


/**
 * 零售商api
 * Created by ccq on 16-1-1.
 */
@API("/seller")
public class RetailerResource extends ApiResource {

  /**
   *
   * 发送验证码(批量)
   *
   * @param accounts  账号列表
   */
  @POST("/sendcode")
  public WebResult addSendCode(List<user_info> accounts) {
    try {
        //从property文件中获取属性
        String content = PropertyUtil.getProperty("sms.content");
        String phone = "";
        String remark = "";
        String code = "";
        String resultContent = "";
        JSONObject returnResult = new JSONObject();
       for(user_info userInfo : accounts) {
           phone = userInfo.get("phone");
           remark = userInfo.get("remark");
           code = CommonUtils.getRandNum(6);
           returnResult = (JSONObject) JSON.parse(SmsApi.sendSms(SmsApi.APIKEY,content + code ,phone));
           Date afterOneHour = new Date(System.currentTimeMillis() + 60*60*1000);
           invite_verify_code.dao.set("area_id",ConstantsUtils.ALL_AREA_ID).set("code",code).set("type",ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE).set("status",ConstantsUtils.INVITE_CODE_STATUS_SUCCESSED)
                   .set("expire_time", DateUtils.getDateString(afterOneHour,DateUtils.format_yyyyMMddHHmmss)).save();
           if(returnResult.get("msg") == null || (returnResult.get("msg") !=null && !"OK".equals(returnResult.get("msg")))) {
               resultContent += phone + "短信发送失败;";
           }
       }

        if(!"".equals(resultContent)) {
            return new WebResult(HttpStatus.OK, resultContent);
        } else {
            return new WebResult(HttpStatus.OK, "发送验证码成功");
        }
    } catch (Exception e) {
      //异常情况，按理说需要记录日志 TODO
       return new WebResult(HttpStatus.EXPECTATION_FAILED, "异常错误");
    }
  }

    /**
     *零售商合作与否
     *@param  id 零售商id
     *@param op 操作类别
     *@param seller_id 卖家id
     */
    @PUT("/cooperation")
    public WebResult cooperation(int id, int op, int seller_id) {
        try {
            buyer_seller buyerSeller =  buyer_seller.dao.findFirstBy("buyer_id = ? and seller_id = ?", id, seller_id);
            if (buyerSeller == null) {
                buyerSeller.set("buyer_id",id).set("seller_id",seller_id).set("status",op).save();
            } else {
                buyerSeller.set("status",op).update();
            }
            return new WebResult(HttpStatus.OK, "操作成功");
        } catch (Exception e) {
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "异常错误");
        }
    }

    public static void main(String[] args) {
        try {
           /* Properties p = new Properties();

            p.load(RetailerResource.class.getClassLoader().getResourceAsStream("application.properties"));

            System.out.println(new String(p.getProperty("sms.content").getBytes("iso-8859-1"),"UTF-8") + CommonUtils.getRandNum(6));*/

           } catch(Exception e) {
            System.out.println("error");
        }

    }

}