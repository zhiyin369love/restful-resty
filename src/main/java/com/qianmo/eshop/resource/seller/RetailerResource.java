package com.qianmo.eshop.resource.seller;

import cn.dreampie.log.Logger;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.PUT;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.bean.user.UserInfo;
import com.qianmo.eshop.common.*;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.sms_template;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


/**
 * 零售商api
 * Created by ccq on 16-1-1.
 */
@API("/seller/retailer")
public class RetailerResource extends ApiResource {
    private static final Logger logger = Logger.getLogger(RetailerResource.class);
    private long seller_id = SessionUtil.getUserId();

    /**
     * 发送邀请码(批量)
     *
     * @param accounts 账号列表
     */
    @PUT("/send_code")
    @Transaction
    public Map addSendCode(List<JSONObject> accounts) {
        //try {
        //从property文件中获取属性
        Map result = new HashMap();
        //String content = PropertyUtil.getProperty("sms.content");
        String phone = "";
        String remark = "";
        String code = "";
        String resultContent = "";
        JSONObject returnResult = new JSONObject();
        Date afterOneDay = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        if (seller_id == 0l) {
            return CommonUtils.getCodeMessage(false, "输入参数有误");
        }
        if (accounts != null && accounts.size() > 0) {
            try {
                sms_template model = sms_template.dao.findById(ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
                String templateContent = model.get("content");
                String sign = model.get("sign");
                String content = sign + templateContent;
                for (JSONObject userInfo : accounts) {
                    phone = (String) userInfo.get("phone");
                    remark = (String) userInfo.get("remark");
                    code = CommonUtils.getRandNum(6);
                    invite_verify_code verifyCode = invite_verify_code.dao.findFirstBy(" user_id = ? and phone = ? and type = ?  ", seller_id, phone, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
                    //如果没有发送过邀请码，那么第一次需要保存
                    if (verifyCode == null) {
                        returnResult = (JSONObject) JSON.parse(SmsApi.sendSms(SmsApi.APIKEY, content.replace("?", code), phone));
                        //if (returnResult.get("msg") != null && "OK".equals(returnResult.get("msg"))) {
                        invite_verify_code.dao.set("area_id", ConstantsUtils.ALL_AREA_ID).set("code", code).set("user_id", seller_id).set("type", ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE).set("status", ConstantsUtils.INVITE_CODE_STATUS_SUCCESSED)
                                .set("expire_time", DateUtils.getDateString(afterOneDay, DateUtils.format_yyyyMMddHHmmss)).set("remark", remark).set("phone", phone).save();
                        // }
                    } else {
                        //如果邀请码在一天有效期内，暂时就不给发
                        if (DateUtils.formatDate(verifyCode.get("expire_time").toString(), DateUtils.format_yyyyMMddHHmmss).getTime() > System.currentTimeMillis()) {
                            return CommonUtils.getCodeMessage(false, "邀请码在一天有效期内暂时不发送");
                        } else {
                            //如果在一天有效期外，那么就需要发送，并且update  invite_verify_code这张表
                            returnResult = (JSONObject) JSON.parse(SmsApi.sendSms(SmsApi.APIKEY, content.replace("?", code), phone));
                            //if (returnResult.get("msg") != null && "OK".equals(returnResult.get("msg"))) {
                            verifyCode.set("code", code)
                                    .set("expire_time", DateUtils.getDateString(afterOneDay, DateUtils.format_yyyyMMddHHmmss))
                                    .set("status", ConstantsUtils.INVITE_CODE_STATUS_SUCCESSED)
                                    .update();
                            //}
                        }
                    }
                    if (returnResult.get("msg") == null || !"OK".equals(returnResult.get("msg"))) {
                        logger.info(returnResult.toString());
                        resultContent += phone + "邀请失败;";
                    }
                }
            } catch (IOException e) {

            }
            if (!"".equals(resultContent)) {
                result = CommonUtils.getCodeMessage(false, resultContent.substring(0, resultContent.length() - 1));
                //return result;
            } else {
                result = setResult("邀请成功");
            }
        } else {
            result = CommonUtils.getCodeMessage(false, "输入有误");
        }
        return result;
    }


    /**
     * 添加或者修改备注
     *
     * @param remarks 账号列表
     */
    @PUT("/remark")
    @Transaction
    public Map saveOrEditSendCode(Map<String, Object> remarks) {
        Map result = CommonUtils.getCodeMessage(true, "操作成功");
        if (seller_id == 0l) {
            result = CommonUtils.getCodeMessage(false, "输入参数有误");
        }
        if (remarks != null) {
            //for (JSONObject userInfo : remarks) {
            Long buyerId = Long.valueOf(remarks.get("buyer_id").toString());
            String phone = (String) remarks.get("phone");
            //并未对remark做非空判断，这项判断应该在前台点确定之前给个提醒
            String remark = (String) remarks.get("remark");
            if (buyerId != null) {
                user_info userInfo = user_info.dao.findById(buyerId);
                if (userInfo != null) {
                    userInfo.set("remark", remark).update();
                } else {
                    result = CommonUtils.getCodeMessage(false, "输入零售商id有误");
                }
            } else {
                //code = CommonUtils.getRandNum(6);
                invite_verify_code verifyCode = invite_verify_code.dao.findFirstBy(" user_id = ? and phone = ? and type = ?  ", seller_id, phone, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
                //如果没有发送过邀请码，那么第一次需要保存
                if (verifyCode == null) {
                    result = CommonUtils.getCodeMessage(false, "根据该手机号找不到用户信息");
                } else {
                    verifyCode.set("remark", remark).update();
                }
            }
        } else {
            result = CommonUtils.getCodeMessage(false, "输入参数有误");
        }
        return result;
    }


    /**
     * 零售商合作与否
     *
     * @param id 零售商id
     * @param op 操作类别
     */
    @PUT("/:id")
    @Transaction
    public Map cooperation(Long id, Long op) {
        //try {
        if ((id == null || id == 0l) || op == null || seller_id == 0l) {
            return CommonUtils.getCodeMessage(false, "输入参数有误");
        }
        buyer_seller buyerSeller = buyer_seller.dao.findFirstBy("buyer_id = ? and seller_id = ?", id, seller_id);
        if (buyerSeller == null) {
            new buyer_seller().set("buyer_id", id).set("seller_id", seller_id).set("status", op).set("area_id", ConstantsUtils.ALL_AREA_ID).save();
        } else {
            buyerSeller.set("status", op).update();
        }
        return CommonUtils.getCodeMessage(true, "修改成功");
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
    public HashMap getRetailerList(String buyer_name, String name, Integer page_start, Integer page_step, String phone, Integer status) {
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
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
            String sql;
            if (status != null) {
                //查找已注册
                if (status == ConstantsUtils.ONE) {
                    sql = YamlRead.getSQL("getMyRegisterRetailer", "seller/seller");
                } else {
                    //查找未注册
                    sql = YamlRead.getSQL("getMyNoRegisterRetailer", "seller/seller");
                }
            } else {
                //如果不传，默认查询所有
                sql = YamlRead.getSQL("getMyRetailer", "seller/seller");
            }


            if (!StringUtils.isEmpty(buyer_name)) {
                //是否手机号码
                boolean isOrderNum = buyer_name.matches("[0-9]+");
                if (isOrderNum) {
                    sql += " and a.phone like '%" + buyer_name.trim() + "%'";
                } else {
                    sql += " and (a.nickname like '%" + buyer_name.trim() + "%'" + " or a.name like '%" + buyer_name.trim() + "%' )";
                }

            }
            /*if(!StringUtils.isEmpty(name)) {
                sql += " and a.name like '%" + name + "%'";
            }
            if(!StringUtils.isEmpty(phone)) {
                sql += " and a.phone like '%" + phone + "%'";
            }*/
            if (status != null) {
                //查找已注册
                if (status == ConstantsUtils.ONE) {
                    inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id);
                } else {
                    inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE, seller_id);
                }
            } else {
                inviteCodeList = invite_verify_code.dao.fullPaginate(pageNumber, page_step, sql, seller_id, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
            }
            inviteVerifyCodes = inviteCodeList.getList();
            resultMap.put("page_size", page_step);
            resultMap.put("total_count", inviteCodeList.getTotalRow());
            resultMap.put("total_page", inviteCodeList.getTotalPage());
            resultMap.put("buyer_list", inviteVerifyCodes);
            /*resultMap.put("page_info", pageInfo);*/
        } else {
            resultMap.put("buyer_list", null);
        }
        // return resultMap;
        return resultMap;

/*        } catch (Exception e) {
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
    public Map updateRetailerPrice(List<JSONObject> store_price_list) {
        Map resultMap = new HashMap();
        String content = "";
        int i = 0;
        if (seller_id != 0l) {
            //try {
            if (store_price_list != null && store_price_list.size() > 0) {
                for (JSONObject skuPrice : store_price_list) {
                    i += 1;
                    if (skuPrice.get("goods_id") != null && skuPrice.get("goods_sku_id") != null && skuPrice.get("buyer_id") != null) {
                        goods_sku_price goodsSkuPrice = goods_sku_price.dao.findFirstBy("goods_num = ? and sku_id = ? and buyer_id = ? and seller_id = ?", skuPrice.get("goods_id"), skuPrice.get("goods_sku_id"), skuPrice.get("buyer_id"),
                                seller_id);
                        if (goodsSkuPrice == null) {
                            new goods_sku_price().set("price", skuPrice.get("goods_price")).set("status", skuPrice.get("goods_price_status")).
                                    set("goods_num", skuPrice.get("goods_id")).set("sku_id", skuPrice.get("goods_sku_id")).set("area_id", ConstantsUtils.ALL_AREA_ID).
                                    set("buyer_id", skuPrice.get("buyer_id")).set("type", 0).set("seller_id", seller_id).save();
                        } else {
                            goodsSkuPrice.set("price", skuPrice.get("goods_price")).set("status", skuPrice.get("goods_price_status")).update();
                        }
                    } else {
                        content += "第" + i + "行输入参数中有为空的;";
                    }
                }
                if (!"".equals(content)) {
                    resultMap = CommonUtils.getCodeMessage(false, content.substring(0, content.length() - 1));
                } else {
                    resultMap = setResult("批量修改价格成功");
                }
            }
        }
        return resultMap;
        /*} catch (Exception e) {
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
    public Map getRetailerPriceList(Long goods_id, Long goos_sku_id, Long id, Integer page_start, Integer page_step, Integer type) {

        HashMap resultMap = new HashMap();
        //try {
        if (seller_id != 0l) {
            //需要判断是否已注册,如果没有注册过，那么返回结果中is_invited是0
            if (!(page_start != null && page_start != 0 && page_step != null && page_step != 0)) {
                page_start = ConstantsUtils.DEFAULT_PAGE_START;
                page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
            }
            int pageNumber = page_start / page_step + 1;
            if (type == null) {
                type = 1;
            }
            FullPage<goods_sku> goodsSkuFullPageList;
            String getRetailPriceSql = YamlRead.getSQL("getRetailerPrice", "seller/seller");
            if (goods_id != null) {
                getRetailPriceSql += " and b.num = " + goods_id;
            }
            if (goos_sku_id != null) {
                getRetailPriceSql += " and c.id = " + goos_sku_id;
            }
            //如果商品编号不为空的话

            goodsSkuFullPageList = goods_sku.dao.fullPaginate(pageNumber, page_step, getRetailPriceSql, type, seller_id, type, seller_id, id);
            List<goods_sku> goodsSkuList = goodsSkuFullPageList == null ? null : goodsSkuFullPageList.getList();
            resultMap.put("buyer_price_list", goodsSkuList);
            resultMap.put("total_count", goodsSkuFullPageList.getTotalRow());
            resultMap.put("page_size", page_step);
        }
        return resultMap;
    }


    /**
     * 获取用户商品购买不可购买总数
     *
     * @param buyer_id   买家id
     * @param goods_num  商品编号
     * @param goods_name 商品名称
     */
    @GET("/buy_count")
    public Map getRetailerList(Long buyer_id, Long goods_num, String goods_name) {
        Map resultMap = new HashMap();
        //可购买总数
        long couldBuy;
        //不可购买总数
        long couldNotBuy;
        //try {
        String sql = "SELECT COUNT(*) cn FROM goods_sku_price a LEFT JOIN goods_info b " +
                "  ON a.goods_num = b.num  where a.buyer_id = ? and b.seller_id = ? and a.status = ? ";
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
        return resultMap;
       /* } catch (Exception e) {
            return resultMap;
        }*/
    }


    public long getCountByGoods(String sql, Object... objects) {
        return goods_sku_price.dao.findFirst(sql, objects).<Long>get("cn");
    }

    private Map setResult(String message) {
        Map resultMap = new HashMap();
        resultMap.put("code", ConstantsUtils.HTTP_STATUS_OK_200);
        resultMap.put("message", message);
        return resultMap;
    }


    /**
     * 获取经销商下的零售商注册未注册数量
     *
     * @param buyer_name 零售商名称
     */
    @GET("/retailerCount")
    public Map getRetailerList(String buyer_name) {
        Map resultMap = new HashMap();
        //try {
        if (seller_id != 0l) {
            //需要判断是否已注册,如果已经注册过，需要根据phone去找买家id，如果没有注册过，那么返回结果中is_invited是0
            FullPage<invite_verify_code> inviteCodeList = null;
            String noRegistersql = YamlRead.getSQL("getNoRegisterUserList", "seller/seller");
            String registersql = YamlRead.getSQL("getRegisterUserList", "seller/seller");
            long noRegisterCount = 0l;
            boolean isNum = true;
            if (!StringUtils.isEmpty(buyer_name)) {
                //是否手机号码
                isNum = buyer_name.matches("[0-9]+");
                if (isNum) {
                    noRegistersql += " and a.phone like '%" + buyer_name + "%'";
                    registersql += "  and a.phone like '%" + buyer_name + "%'";
                } else {
                    registersql += " and (a.nickname like '%" + buyer_name + "%'" + " or a.name like '%" + buyer_name + "%' )";
                }
            }
            //如果是按名称搜索的话，那么直接将未注册设为0
            if (!isNum) {
                noRegisterCount = 0l;
            } else {
                noRegisterCount = invite_verify_code.dao.findFirst(noRegistersql, seller_id, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE, seller_id).<Long>get("cn");
            }
            long registerCount = invite_verify_code.dao.findFirst(registersql, seller_id).<Long>get("cn");
            resultMap.put("noRegisterCount", noRegisterCount);
            resultMap.put("registerCount", registerCount);
        }
        return resultMap;
    }
}