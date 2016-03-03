package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.*;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.*;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;
import java.util.*;


/**
 * 零售商api
 * Created by ccq on 16-1-1.
 */
@API("/seller")
public class RetailerResource extends ApiResource {

  /**
   *
   * 发送邀请码(批量)
   *
   * @param accounts  账号列表
   * @param seller_id 卖家id
   */
  @PUT("/sendcode")
  public WebResult addSendCode(List<user_info> accounts,long seller_id) {
    try {
        //从property文件中获取属性
        String content = PropertyUtil.getProperty("sms.content");
        String phone = "";
        String remark = "";
        String code = "";
        String resultContent = "";
        JSONObject returnResult = new JSONObject();
        Date afterOneHour = new Date(System.currentTimeMillis() + 24*60*60*1000);;
        if(accounts != null && accounts.size() >0) {
            for(user_info userInfo : accounts) {
                phone = userInfo.get("phone");
                remark = userInfo.get("remark");
                code = CommonUtils.getRandNum(6);
                invite_verify_code verifyCode = invite_verify_code.dao.findFirstBy(" user_id = ? and phone = ? and type = ?  ", seller_id, phone,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
                //如果没有发送过邀请码，那么第一次需要保存
                if(verifyCode == null) {
                    returnResult = (JSONObject) JSON.parse(SmsApi.sendSms(SmsApi.APIKEY,content + code ,phone));
                    invite_verify_code.dao.set("area_id",ConstantsUtils.ALL_AREA_ID).set("code",code).set("user_id",seller_id).set("type",ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE).set("status",ConstantsUtils.INVITE_CODE_STATUS_EXPIRED)
                            .set("expire_time", DateUtils.getDateString(afterOneHour,DateUtils.format_yyyyMMddHHmmss)).set("remark",remark).save();
                } else {
                    //如果邀请码在一天有效期内，暂时就不给发
                    if(DateUtils.formatDate(verifyCode.<String>get("expire_time"),DateUtils.format_yyyyMMddHHmmss).getTime() < System.currentTimeMillis()) {
                        return new WebResult(HttpStatus.OK, "邀请码在一天有效期内暂时不发送");
                    } else {
                        //如果在一天有效期外，那么就需要发送，并且update  invite_verify_code这张表
                        verifyCode.set("code",code).set("expire_time", DateUtils.getDateString(afterOneHour,DateUtils.format_yyyyMMddHHmmss)).update();
                        returnResult = (JSONObject) JSON.parse(SmsApi.sendSms(SmsApi.APIKEY,content + code ,phone));
                    }
                }
                if(returnResult.get("msg") == null || (returnResult.get("msg") !=null && !"OK".equals(returnResult.get("msg")))) {
                    resultContent += phone + "短信发送失败;";
                }
            }
            if(!"".equals(resultContent)) {
                return new WebResult(HttpStatus.OK, resultContent);
            } else {
                return new WebResult(HttpStatus.OK, "发送验证码成功");
            }
        } else {
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "输入参数有误");
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


    /**
     *
     * 获取经销商下的零售商信息
     *
     * @param  buyer_name 零售商名称
     * @param  name 用户名称
     * @param  page_start 第几条开始
     * @param  page_step  返回多少条
     * @param  phone 手机号
     * @param  seller_id  卖家id 必填
     */
    @GET("/retailerList")
    public HashMap getRetailerList(String buyer_name, String name, int page_start, int page_step,String  phone,long seller_id) {
        HashMap resultMap = new HashMap();
        List<invite_verify_code> inviteVerifyCodes = new ArrayList<invite_verify_code>();
        List<user_info> buyerSellerResultList = new ArrayList<user_info>();
        Map pageInfo = new HashMap();
        try {
            if(seller_id != 0l) {
                //需要判断是否已注册,如果已经注册过，需要根据phone去找买家id，如果没有注册过，那么返回结果中is_invited是0
                if(page_start !=0 && page_step!= 0) {
                    FullPage<invite_verify_code> inviteCodeList  =  invite_verify_code.dao.fullPaginateBy(page_start/page_step + 1,page_step,"user_id = ? and type = ?",seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
                    inviteVerifyCodes = inviteCodeList.getList();
                    pageInfo.put("page_size",page_step);
                    pageInfo.put("total_count",inviteCodeList.getTotalRow());
                    pageInfo.put("total_page",inviteCodeList.getTotalPage());
                } else {
                    inviteVerifyCodes = invite_verify_code.dao.findBy("user_id = ? and type = ?",seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
                    pageInfo.put("page_size",null);
                    pageInfo.put("total_count",inviteVerifyCodes==null?0:inviteVerifyCodes.size());
                    pageInfo.put("total_page",null);
                }
                if (inviteVerifyCodes != null && inviteVerifyCodes.size() > 0) {
                    for(invite_verify_code inviteVerifyCodeTemp : inviteVerifyCodes) {
                        user_info userTemp = new user_info();
                        //如果状态为false，表明是未注册过的
                        if(inviteVerifyCodeTemp.<Boolean>get("status")) {
                            //是否已邀请
                            userTemp.set("is_invited",ConstantsUtils.INVITE_CODE_STATUS_EXPIRED);
                        } else {
                            //如果状态为true，表明是注册过的，那么需要通过phone去user_info表中查找信息
                            if(!StringUtils.isEmpty(buyer_name) && !StringUtils.isEmpty(name)) {
                                userTemp = user_info.dao.findFirstBy("phone = ? and nickname =? and name =? ", inviteVerifyCodeTemp.get("phone"),buyer_name,name);
                            } else if (!StringUtils.isEmpty(buyer_name)) {
                                userTemp = user_info.dao.findFirstBy("phone = ? and nickname =? ", inviteVerifyCodeTemp.get("phone"),buyer_name);
                            } else if (!StringUtils.isEmpty(name)) {
                                userTemp = user_info.dao.findFirstBy("phone = ? and name =? ", inviteVerifyCodeTemp.get("phone"),name);
                            } else {
                                userTemp = user_info.dao.findFirstBy("phone = ?", inviteVerifyCodeTemp.get("phone"));
                            }
                            //地址
                            userTemp.set("address",userTemp.get("province_name") + userTemp.get("city_name").toString() + userTemp.get("county_name").toString() + userTemp.get("town_name").toString() + userTemp.get("address").toString());
                            // invite_verify_code verifyCode =  new invite_verify_code().getInviteByBuyerAndSeller(buyerId,seller_id);
                            //是否已邀请
                            userTemp.set("is_invited",ConstantsUtils.INVITE_CODE_STATUS_SUCCESSED);
                            //用户id
                            userTemp.set("user_id",userTemp.get("id"));
                        }
                        //phone
                        userTemp.set("phone",inviteVerifyCodeTemp.get("phone"));
                        //邀请码
                        userTemp.set("invited_code",inviteVerifyCodeTemp.get("code"));
                        //备注
                        userTemp.set("remark",inviteVerifyCodeTemp.get("remark"));
                        buyerSellerResultList.add(userTemp);
                    }
                    resultMap.put("buyer_list",buyerSellerResultList);
                    resultMap.put("page_info",pageInfo);
                } else {
                    resultMap.put("buyer_list",null);
                }
               // return resultMap;
                }
                return resultMap;

        } catch (Exception e) {
            //异常情况，按理说需要记录日志 TODO
            resultMap.put("buyer_list",null);
            return resultMap;
        }
    }



    /**
     *
     * 批量修改零售商价格
     *
     * @param  store_price_list 商品型号价格列表
     */
    @PUT("/price/batch")
    public HashMap updateRetailerPrice(List<goods_sku_price> store_price_list) {
        HashMap resultMap = new HashMap();
        String content = "";
        int i = 0;
        try {
            if (store_price_list != null && store_price_list.size() >0) {
                for (goods_sku_price skuPrice : store_price_list) {
                    i += 1;
                    if (skuPrice.get("goods_id") != null && skuPrice.get("goods_sku_id") != null && skuPrice.get("buyer_id") != null && skuPrice.get("seller_id") != null) {
                        goods_sku_price.dao.findFirstBy("goods_num = ? and sku_id = ? and buyer_id = ? and seller_id = ?", skuPrice.get("goods_id"), skuPrice.get("goods_sku_id"), skuPrice.get("buyer_id"),
                                skuPrice.get("seller_id")).set("price", skuPrice.get("goods_price")).set("status", skuPrice.get("goods_price_status")).update();
                    } else {
                        content += "第" + i + "行输入参数中有为空的;";
                    }
                }
                if (!"".equals(content)) {
                    resultMap.put("code", -1);
                    resultMap.put("message", content);
                } else {
                    resultMap.put("code", 0);
                    resultMap.put("message", "批量修改价格成功");
                }
            }
            return resultMap;
        } catch (Exception e) {
            //异常情况，按理说需要记录日志 TODO
            return resultMap;
        }
    }


    /**
     *
     * 获取零售商商品价格信息
     *
     * @param  goods_id 商品id
     * @param  goos_sku_id 商品型号id
     * @param  id 卖家id
     * @param  page_start 第几条开始
     * @param  page_step  返回多少条
     * @param  type 是否购买
     */
    @GET("/price/:id")
    public HashMap getRetailerPriceList(long goods_id, long goos_sku_id,long id,  int page_start, int page_step,int type) {
        HashMap resultMap = new HashMap();
        List<goods_sku_price> goodsSkuPrices = new ArrayList<goods_sku_price>();
        List<goods_sku_price> goodsSkuPriceResultList = new ArrayList<goods_sku_price>();
        Map pageInfo = new HashMap();
        try {
            if(id != 0l) {
                //需要判断是否已注册,如果已经注册过，需要根据phone去找买家id，如果没有注册过，那么返回结果中is_invited是0
                if(page_start !=0 && page_step!= 0) {
                    FullPage<goods_sku_price> goodSkuPriceList = null;
                    if(goods_id != 0 && goos_sku_id != 0 && type != 0) {
                        goodSkuPriceList =  goods_sku_price.dao.fullPaginateBy(page_start/page_step + 1,page_step,"seller_id = ? and goods_num = ? and sku_id = ? and type = ?",
                                id,goods_id,goos_sku_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
                    } else if (goods_id != 0 && goos_sku_id != 0) {
                        goodSkuPriceList =  goods_sku_price.dao.fullPaginateBy(page_start/page_step + 1,page_step,"seller_id = ? and goods_num = ? and sku_id = ? ",
                                id,goods_id,goos_sku_id);
                    } else if (goos_sku_id != 0 && type != 0) {
                        goodSkuPriceList =  goods_sku_price.dao.fullPaginateBy(page_start/page_step + 1,page_step,"seller_id = ? and sku_id = ? and type = ? ",
                                id,goos_sku_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
                    } else if (goods_id != 0  && type != 0) {
                        goodSkuPriceList =  goods_sku_price.dao.fullPaginateBy(page_start/page_step + 1,page_step,"seller_id = ? and goods_num = ?  and type = ?",
                                id,goods_id,ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
                    } else if (goods_id != 0) {
                        goodSkuPriceList =  goods_sku_price.dao.fullPaginateBy(page_start/page_step + 1,page_step,"seller_id = ? and goods_num = ? ",
                                id,goods_id);
                    } else if (goos_sku_id != 0) {
                        goodSkuPriceList =  goods_sku_price.dao.fullPaginateBy(page_start/page_step + 1,page_step,"seller_id = ? and sku_id = ? ",
                                id,goos_sku_id);
                    }  else if (type != 0) {
                        goodSkuPriceList =  goods_sku_price.dao.fullPaginateBy(page_start/page_step + 1,page_step,"seller_id = ?  and type = ?",
                                id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
                    }
                    goodsSkuPrices = goodSkuPriceList.getList();
                    pageInfo.put("page_size",page_step);
                    pageInfo.put("total_count",goodSkuPriceList.getTotalRow());
                    pageInfo.put("total_page",goodSkuPriceList.getTotalPage());
                } else {
                    if(goods_id != 0 && goos_sku_id != 0 && type != 0) {
                        goodsSkuPrices =  goods_sku_price.dao.findBy("seller_id = ? and goods_num = ? and sku_id = ? and type = ?",
                                id,goods_id,goos_sku_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
                    } else if (goods_id != 0 && goos_sku_id != 0) {
                        goodsSkuPrices =  goods_sku_price.dao.findBy("seller_id = ? and goods_num = ? and sku_id = ? ",
                                id,goods_id,goos_sku_id);
                    } else if (goos_sku_id != 0 && type != 0) {
                        goodsSkuPrices =  goods_sku_price.dao.findBy("seller_id = ? and sku_id = ? and type = ? ",
                                id,goos_sku_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
                    } else if (goods_id != 0  && type != 0) {
                        goodsSkuPrices =  goods_sku_price.dao.findBy("seller_id = ? and goods_num = ?  and type = ?",
                                id,goods_id,ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
                    } else if (goods_id != 0) {
                        goodsSkuPrices =  goods_sku_price.dao.findBy("seller_id = ? and goods_num = ? ",
                                id,goods_id);
                    } else if (goos_sku_id != 0) {
                        goodsSkuPrices =  goods_sku_price.dao.findBy("seller_id = ? and sku_id = ? ",
                                id,goos_sku_id);
                    }  else if (type != 0) {
                        goodsSkuPrices =  goods_sku_price.dao.findBy("seller_id = ?  and type = ?",
                                id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
                    }
                    pageInfo.put("page_size",null);
                    pageInfo.put("total_count",goodsSkuPrices==null?0:goodsSkuPrices.size());
                    pageInfo.put("total_page",null);
                }
                if(goodsSkuPrices != null && goodsSkuPrices.size() >0) {
                    for(goods_sku_price goodsSkuPriceTemp : goodsSkuPrices) {
                        //商品id
                        long goodsNum = goodsSkuPriceTemp.<Long>get("goods_num");
                        goodsSkuPriceTemp.set("goods_id",goodsNum);
                        //商品名称
                        goodsSkuPriceTemp.set("goods_name", goods_info.dao.findFirstBy("num = ?" , goodsNum).get("name"));
                        //型号名称
                        long skuId = goodsSkuPriceTemp.<Long>get("sku_id");
                        goodsSkuPriceTemp.set(" sku_name", goods_sku.dao.findById(skuId).get("name"));
                        goodsSkuPriceResultList.add(goodsSkuPriceTemp);
                    }
                }
                resultMap.put("buyer_price_list",goodsSkuPriceResultList);
                resultMap.put("page_info",pageInfo);
            } else {
                resultMap.put("buyer_price_list",null);
            }
            return resultMap;
        } catch (Exception e) {
            //异常情况，按理说需要记录日志 TODO
            resultMap.put("buyer_price_list",null);
            return resultMap;
        }
    }

}