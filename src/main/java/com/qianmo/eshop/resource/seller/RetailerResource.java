package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.PUT;
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

import java.io.IOException;
import java.util.*;


/**
 * 零售商api
 * Created by ccq on 16-1-1.
 */
@API("/seller/retailer")
public class RetailerResource extends ApiResource {
    private long seller_id = SessionUtil.getUserId();

    /**
     * 发送邀请码(批量)
     *
     * @param accounts 账号列表
     */
    @PUT("/sendcode")
    @Transaction
    public WebResult addSendCode(List<JSONObject> accounts) {
        //try {
        //从property文件中获取属性
        String content = PropertyUtil.getProperty("sms.content");
        String phone = "";
        String remark = "";
        String code = "";
        String resultContent = "";
        JSONObject returnResult = new JSONObject();
        Date afterOneDay = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        if (seller_id == 0l) {
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "输入参数有误");
        }
        if (accounts != null && accounts.size() > 0) {
            for (JSONObject userInfo : accounts) {
                phone = (String) userInfo.get("phone");
                remark = (String) userInfo.get("remark");
                code = CommonUtils.getRandNum(6);
                invite_verify_code verifyCode = invite_verify_code.dao.findFirstBy(" user_id = ? and phone = ? and type = ?  ", seller_id, phone, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
                //如果没有发送过邀请码，那么第一次需要保存
                if (verifyCode == null) {
                    try {
                        returnResult = (JSONObject) JSON.parse(SmsApi.sendSms(SmsApi.APIKEY, content + code, phone));
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }
                    invite_verify_code.dao.set("area_id", ConstantsUtils.ALL_AREA_ID).set("code", code).set("user_id", seller_id).set("type", ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE).set("status", ConstantsUtils.INVITE_CODE_STATUS_EXPIRED)
                            .set("expire_time", DateUtils.getDateString(afterOneDay, DateUtils.format_yyyyMMddHHmmss)).set("remark", remark).set("phone", phone).save();
                } else {
                    //如果邀请码在一天有效期内，暂时就不给发
                    if (DateUtils.formatDate(verifyCode.<String>get("expire_time"), DateUtils.format_yyyyMMddHHmmss).getTime() < System.currentTimeMillis()) {
                        return new WebResult(HttpStatus.OK, "邀请码在一天有效期内暂时不发送");
                    } else {
                        //如果在一天有效期外，那么就需要发送，并且update  invite_verify_code这张表
                        verifyCode.set("code", code).set("expire_time", DateUtils.getDateString(afterOneDay, DateUtils.format_yyyyMMddHHmmss)).update();
                        try {
                            returnResult = (JSONObject) JSON.parse(SmsApi.sendSms(SmsApi.APIKEY, content + code, phone));
                        } catch (IOException e) {
                            // e.printStackTrace();
                        }
                    }
                }
                if (returnResult.get("msg") == null || !"OK".equals(returnResult.get("msg"))) {
                    resultContent += phone + "短信发送失败;";
                }
            }
            if (!"".equals(resultContent)) {
                return new WebResult(HttpStatus.EXPECTATION_FAILED, resultContent);
            } else {
                return new WebResult(HttpStatus.CREATED, "发送验证码成功");
            }
        } else {
            return new WebResult(HttpStatus.BAD_REQUEST, "输入参数有误");
        }
      /*  } catch (Exception e) {
            //异常情况，按理说需要记录日志 TODO
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "异常错误");
        }*/
    }

    /**
     * 零售商合作与否
     *
     * @param id 零售商id
     * @param op 操作类别
     */
    @PUT("/:id")
    @Transaction
    public WebResult cooperation(Long id, Long op) {
        //try {
        if ((id == null || id == 0l) || op == null || seller_id == 0l) {
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "输入参数有误");
        }
        buyer_seller buyerSeller = buyer_seller.dao.findFirstBy("buyer_id = ? and seller_id = ?", id, seller_id);
        if (buyerSeller == null) {
            new buyer_seller().set("buyer_id", id).set("seller_id", seller_id).set("status", op).set("area_id", ConstantsUtils.ALL_AREA_ID).save();
        } else {
            buyerSeller.set("status", op).update();
        }
        return new WebResult(HttpStatus.CREATED, "操作成功");
       /* } catch (Exception e) {
            return new WebResult(HttpStatus.EXPECTATION_FAILED, "异常错误");
        }*/
    }


    /**
     * 获取经销商下的零售商信息
     *
     * @param buyer_name 零售商名称
     * @param name       用户名称
     * @param page_start 第几条开始
     * @param page_step  返回多少条
     * @param phone      手机号
     */
    @GET
    public WebResult getRetailerList(String buyer_name, String name, Integer page_start, Integer page_step, String phone) {
        HashMap resultMap = new HashMap();
        List<invite_verify_code> inviteVerifyCodes = new ArrayList<invite_verify_code>();
        List<HashMap> buyerSellerResultList = new ArrayList<HashMap>();
        Map pageInfo = new HashMap();
        //try {
        if (seller_id != 0l) {
            //需要判断是否已注册,如果已经注册过，需要根据phone去找买家id，如果没有注册过，那么返回结果中is_invited是0
            if (!(page_start != null && page_start != 0 && page_step != null && page_step != 0)) {
                page_start = ConstantsUtils.DEFAULT_PAGE_START;
                page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
            }
            int pageNumber = page_start / page_step + 1;
            FullPage<invite_verify_code> inviteCodeList = null;
            String sql = "SELECT * FROM user_info a LEFT JOIN invite_verify_code b" +
                    "ON a.phone = b.phone " +
                    "WHERE b.phone IS NOT NULL AND b.user_id = ? AND b.type  =? ";
            if (!StringUtils.isEmpty(buyer_name) && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(phone)) {
                sql += " and a.nickname like ? and a.name like ? and a.phone like ?";
                inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE, "%" + buyer_name + "%", "%" + name + "%", "%" + phone + "%");
            } else if (!StringUtils.isEmpty(buyer_name) && !StringUtils.isEmpty(name)) {
                sql += " and a.nickname like ? and a.name like ? ";
                inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE, "%" + buyer_name + "%", "%" + name + "%");
            } else if (!StringUtils.isEmpty(buyer_name) && !StringUtils.isEmpty(phone)) {
                sql += " and a.nickname like ? and a.phone like ? ";
                inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE, "%" + buyer_name + "%", "%" + phone + "%");
            } else if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(phone)) {
                sql += " and a.name like ? and a.phone like ? ";
                inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE, "%" + phone + "%", "%" + phone + "%");
            } else if (!StringUtils.isEmpty(buyer_name)) {
                sql += " and a.nickname like ?  ";
                inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE, "%" + buyer_name + "%");
            } else if (!StringUtils.isEmpty(name)) {
                sql += " and a.name like ?  ";
                inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE, "%" + name + "%");
            } else if (!StringUtils.isEmpty(phone)) {
                sql += " and a.phone like ?  ";
                inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE, "%" + phone + "%");
            } else {
                inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
            }
            inviteVerifyCodes = inviteCodeList.getList();
            pageInfo.put("page_size", page_step);
            pageInfo.put("total_count", inviteCodeList.getTotalRow());
            pageInfo.put("total_page", inviteCodeList.getTotalPage());
               /* } else {
                    String sql = "SELECT * FROM user_info a LEFT JOIN invite_verify_code b\n" +
                            "ON a.phone = b.phone " +
                            "WHERE b.phone IS NOT NULL AND b.user_id = ? AND b.type  =? ";
                    if(!StringUtils.isEmpty(buyer_name) && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(phone)) {
                        sql += " and a.nickname = ? and a.name = ? and a.phone = ?";
                        inviteVerifyCodes = invite_verify_code.dao.find(sql,seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE,buyer_name,name,phone);
                    } else if (!StringUtils.isEmpty(buyer_name) && !StringUtils.isEmpty(name) ){
                        sql += " and a.nickname = ? and a.name = ? ";
                        inviteVerifyCodes = invite_verify_code.dao.find(sql,seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE,buyer_name,name);
                    } else if (!StringUtils.isEmpty(buyer_name) && !StringUtils.isEmpty(phone)) {
                        sql += " and a.nickname = ? and a.phone = ? ";
                        inviteVerifyCodes = invite_verify_code.dao.find(sql,seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE,buyer_name,phone);
                    } else if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(phone)) {
                        sql += " and a.name = ? and a.phone = ? ";
                        inviteVerifyCodes = invite_verify_code.dao.find(sql,seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE,name,phone);
                    } else if(!StringUtils.isEmpty(buyer_name)) {
                        sql += " and a.nickname = ?  ";
                        inviteVerifyCodes = invite_verify_code.dao.find(sql,seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE,buyer_name);
                    } else if (!StringUtils.isEmpty(name) ) {
                        sql += " and a.name = ?  ";
                        inviteVerifyCodes = invite_verify_code.dao.find(sql,seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE,name);
                    } else if (!StringUtils.isEmpty(phone)) {
                        sql += " and a.phone = ?  ";
                        inviteVerifyCodes = invite_verify_code.dao.find(sql,seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE,phone);
                    } else {
                        inviteVerifyCodes = invite_verify_code.dao.find(sql,seller_id,ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
                    }
                    pageInfo.put("page_size",null);
                    pageInfo.put("total_count",inviteVerifyCodes==null?0:inviteVerifyCodes.size());
                    pageInfo.put("total_page",null);
                }*/
            if (inviteVerifyCodes != null && inviteVerifyCodes.size() > 0) {
                HashMap userTemp = new HashMap();
                for (invite_verify_code inviteVerifyCodeTemp : inviteVerifyCodes) {
                    userTemp.clear();
                    //user_info userInfoTemp = new user_info();
                    userTemp.put("is_invited", inviteVerifyCodeTemp.get("is_invited"));
                    //地址
                    userTemp.put("address", inviteVerifyCodeTemp.get("province_name") + inviteVerifyCodeTemp.get("city_name").toString() + inviteVerifyCodeTemp.get("county_name").toString() + inviteVerifyCodeTemp.get("town_name").toString() + inviteVerifyCodeTemp.get("address").toString());
                    // invite_verify_code verifyCode =  new invite_verify_code().getInviteByBuyerAndSeller(buyerId,seller_id);
                    //是否已邀请
                    userTemp.put("is_invited", ConstantsUtils.INVITE_CODE_STATUS_SUCCESSED);
                    //用户id
                    userTemp.put("user_id", inviteVerifyCodeTemp.get("id"));
                    userTemp.put("account", inviteVerifyCodeTemp.get("account"));
                    //phone
                    userTemp.put("phone", inviteVerifyCodeTemp.get("phone"));
                    //邀请码
                    userTemp.put("invited_code", inviteVerifyCodeTemp.get("code"));
                    //备注
                    userTemp.put("remark", inviteVerifyCodeTemp.get("remark"));
                    buyerSellerResultList.add(userTemp);
                }
            }
            resultMap.put("buyer_list", buyerSellerResultList);
            resultMap.put("page_info", pageInfo);
        } else {
            resultMap.put("buyer_list", null);
        }
        // return resultMap;
        return new WebResult(HttpStatus.OK, resultMap);

/*        } catch (Exception e) {
            //异常情况，按理说需要记录日志 TODO
            resultMap.put("buyer_list", null);
            return resultMap;
        }*/
    }


    /**
     * 批量修改零售商价格
     *
     * @param store_price_list 商品型号价格列表
     */
    @PUT("/price/batch")
    @Transaction
    public WebResult updateRetailerPrice(List<JSONObject> store_price_list) {
        HashMap resultMap = new HashMap();
        String content = "";
        int i = 0;
        //try {
        if (store_price_list != null && store_price_list.size() > 0) {
            for (JSONObject skuPrice : store_price_list) {
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
        return new WebResult(HttpStatus.OK, resultMap);
        /*} catch (Exception e) {
            //异常情况，按理说需要记录日志 TODO
            return resultMap;
        }*/
    }


    /**
     * 获取零售商商品价格信息
     *
     * @param goods_id    商品id
     * @param goos_sku_id 商品型号id
     * @param id          用户id
     * @param page_start  第几条开始
     * @param page_step   返回多少条
     * @param type        是否购买
     */
    @GET("/price/:id")
    public WebResult getRetailerPriceList(Long goods_id, Long goos_sku_id, Long id, Integer page_start, Integer page_step, Integer type) {

        HashMap resultMap = new HashMap();
        List<goods_sku_price> goodsSkuPrices = new ArrayList<goods_sku_price>();
        List<goods_sku_price> goodsSkuPriceResultList = new ArrayList<goods_sku_price>();
        Map pageInfo = new HashMap();
        //try {
        if (seller_id != 0l) {
            //需要判断是否已注册,如果没有注册过，那么返回结果中is_invited是0
            if (!(page_start != null && page_start != 0 && page_step != null && page_step != 0)) {
                page_start = ConstantsUtils.DEFAULT_PAGE_START;
                page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
            }
            int pageNumber = page_start / page_step + 1;
            FullPage<goods_sku> goodsSkuFullPageList;
            //如果商品编号不为空的话
            if (goods_id != null) {
                goodsSkuFullPageList = goods_sku.dao.fullPaginateBy(pageNumber, page_step, "seller_id = ? and status = ? and goods_num = ?", seller_id, ConstantsUtils.ONE, goods_id);
            } else {
                goodsSkuFullPageList = goods_sku.dao.fullPaginateBy(pageNumber, page_step, "seller_id = ? and status = ? ", seller_id, ConstantsUtils.ONE);
            }
            List<goods_sku> goodsSkuList = goodsSkuFullPageList == null ? null : goodsSkuFullPageList.getList();
            String getRetailerSkuPriceSql = YamlRead.getSQL("getRetailerSkuPrice", "seller/seller");
            if (goodsSkuList != null && goodsSkuList.size() > 0) {
                //HashMap result = new HashMap();
                for (goods_sku goodsSku : goodsSkuList) {
                    //如果商品型号id不为空
                    if (goos_sku_id != null) {
                        getRetailerSkuPriceSql += " and b.sku_id =  " + goos_sku_id;
                    }
                    //如果类别不为空
                    if (type != null) {
                        getRetailerSkuPriceSql += " and b.type =  " + type;
                    }
                    long goodsNum = goodsSku.get("goods_num");
                    goods_sku_price goodsSkuPrice = goods_sku_price.dao.findFirst(getRetailerSkuPriceSql, id, seller_id, goodsNum);
                    goodsSkuPriceResultList.add(goodsSkuPrice);
                }
            }
        }
        resultMap.put("buyer_price_list", goodsSkuPriceResultList);
        resultMap.put("total_count", goodsSkuPriceResultList.size());
        resultMap.put("page_size", page_step);
        return new WebResult(HttpStatus.OK, resultMap);
    }



    /**
     * 获取用户商品购买不可购买总数
     *
     * @param buyer_id   买家id
     * @param goods_num  商品编号
     * @param goods_name 商品名称
     */
    @GET("/buy_count")
    public WebResult getRetailerList(Long buyer_id, Long goods_num, String goods_name) {
        HashMap resultMap = new HashMap();
        //可购买总数
        long couldBuy = 0l;
        //不可购买总数
        long couldNotBuy = 0l;
        //try {
        String sql = "SELECT COUNT(*) cn, 0 FROM goods_sku_price a LEFT JOIN goods_info b" +
                "ON a.goods_num = b.num  where a.buyer_id = ? and b.seller_id = ? and a.status = ? ";
        if (goods_num != null && goods_num != 0 && !StringUtils.isEmpty(goods_name)) {
            sql += " and a.goods_num like ? and b.name like ? ";
            couldBuy = getCountByGoods(sql, buyer_id, seller_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE, "%" + goods_num + "%", "%" + goods_name + "%");
            couldNotBuy = getCountByGoods(sql, buyer_id, seller_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE, "%" + goods_num + "%", "%" + goods_name + "%");
        } else if (goods_num != null && goods_num != 0) {
            sql += " and a.goods_num like ?  ";
            couldBuy = getCountByGoods(sql, buyer_id, seller_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE, "%" + goods_num + "%");
            couldNotBuy = getCountByGoods(sql, buyer_id, seller_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE, "%" + goods_num + "%");
        } else if (!StringUtils.isEmpty(goods_name)) {
            sql += " and b.name like ?  ";
            couldBuy = getCountByGoods(sql, buyer_id, seller_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE, "%" + goods_name + "%");
            couldNotBuy = getCountByGoods(sql, buyer_id, seller_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE, "%" + goods_name + "%");
        } else {
            couldBuy = getCountByGoods(sql, buyer_id, seller_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
            couldNotBuy = getCountByGoods(sql, buyer_id, seller_id, ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE);
        }
        resultMap.put("couldBuy", couldBuy);
        resultMap.put("couldNotBuy", couldNotBuy);
        // return resultMap;
        return new WebResult(HttpStatus.OK, resultMap);
       /* } catch (Exception e) {
            return resultMap;
        }*/
    }


    public long getCountByGoods(String sql, Object... objects) {
        return goods_sku_price.dao.findFirst(sql, objects).<Long>get("cn");
    }

}