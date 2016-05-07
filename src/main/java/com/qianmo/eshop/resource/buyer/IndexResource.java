package com.qianmo.eshop.resource.buyer;


import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.POST;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.cart.cart;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 首页api
 * Created by zhangyang on 16-03-01
 * </p>
 */
@API("/buyer")
public class IndexResource extends ApiResource {
    private long buyer_id = SessionUtil.getUserId();

    /**
     * 买家绑定卖家接口
     * 通过验证码查找卖家，然后买家绑定卖家
     *
     * @param bind_code 绑定码
     */
    @POST("/bind")
    public Map addBuyerSeller(int bind_code) {
        long userId = SessionUtil.getUserId();
        boolean isBind = new buyer_seller().bindSeller(bind_code,userId);
        if(isBind) {
            return CommonUtils.getCodeMessage(isBind,"绑定买家成功");
        } else {
            return CommonUtils.getCodeMessage(isBind,"没有此验证码或验证码失效");
        }
    }


    /**
     * 根据验证码获取经销商信息
     *
     * @param bind_code 绑定码
     */
    @GET("/seller")
    public Map getSellerInfoByVerifyCode(Integer bind_code) {
        HashMap resultMap = new HashMap();
        //try {
        //通过验证码找卖家id
        invite_verify_code code = buyer_seller.dao.getInviteByVerifyCode(bind_code);
        if (code != null) {
            Long seller_Id = code.<Long>get("user_id");
           /* String getUserInfoSql = YamlRead.getSQL("findUserInfoById","buyer/order");
            user_info  userTemp = user_info.dao.findFirst(getUserInfoSql,seller_Id);
            user_info sellInfo = userTemp == null ? new user_info() : userTemp;*/
            user_info userInfo = new user_info();
            user_info sellInfo = userInfo.getUserInfoById(seller_Id);
            //sellInfo.remove("permissions");
            //sellInfo.remove("seller_id");
/*            JSONObject jsonObject = new JSONObject();
            jsonObject.put("address_full", sellInfo.get("province_name") == null ? "" : sellInfo.get("province_name").toString() + sellInfo.get("city_name") == null ? "" : sellInfo.get("city_name").toString() + sellInfo.get("county_name") == null ? "" : sellInfo.get("county_name").toString() + sellInfo.get("town_name") == null ? "" : sellInfo.get("town_name").toString() + sellInfo.get("address") == null ? "" : sellInfo.get("address").toString());
            jsonObject.put("seller_id", sellInfo.get("id"));*/
            resultMap.put("seller_info", sellInfo);
        } else {
            //如果找不到，返回空
            resultMap.put("seller_info", null);
        }
        return resultMap;
        /*} catch (Exception e) {
            resultMap.put("seller_info", null);
            return resultMap;
        }*/
    }


    /**
     * 获取首页汇总信息
     */
    @GET("/total")
    public Map getIndexSummary() {
        HashMap resultMap = new HashMap();
        HashMap total = new HashMap();
        //try {
        //我要采购
        long cartNum = cart.dao.findFirst("select count(*) cn from cart where buyer_id = ?", buyer_id).<Long>get("cn");
        //String phone = user_info.dao.findById(buyer_id).<String>get("phone");
        //上级经销商
        List<buyer_seller> sellerlist = buyer_seller.dao.findBy("buyer_id = ? and status = ?", buyer_id,ConstantsUtils.BUYER_SELLER_STATUS_BIDING);
        List<HashMap> resultSellerList = new ArrayList<HashMap>();
        //循环截取所需的字段
        if (sellerlist != null && sellerlist.size() > 0) {
            user_info userInfo = new user_info();

            for (buyer_seller sell : sellerlist) {
                HashMap jsonObject = new HashMap();
                user_info sellInfo = userInfo.getUserInfoById(sell.<Long>get("seller_id"));
                //如果为了更好的节省效率和代码复用，接口文档中的字段名称最好都统一，
                // 例如nickname有的地方是seller_name,有的地方是nickname
                jsonObject.put("seller_id", sell.get("seller_id"));
                jsonObject.put("seller_name", sellInfo.get("nickname"));
                jsonObject.put("phone", sellInfo.get("phone"));
                resultSellerList.add(jsonObject);
            }
            total.put("cart_count", cartNum);
            //total.put("phone",phone);
            total.put("seller_list", resultSellerList);
            //待付款订单
            long payWait = order_info.dao.findFirst("SELECT COUNT(*) cn FROM order_info a  LEFT JOIN order_user b" +
                    "                ON a.num = b.order_num" +
                    "                WHERE  a.status = ? and b.order_num IS NOT NULL AND b.buyer_id = ? ", ConstantsUtils.ORDER_INFO_STATUS_CREATED, buyer_id).<Long>get("cn");

            //待收货订单
            long receiveWait = order_info.dao.findFirst("SELECT COUNT(*) cn FROM order_info a  LEFT JOIN order_user b" +
                    "                ON a.num = b.order_num" +
                    "                WHERE  a.status in ( ?,?) and b.order_num IS NOT NULL AND b.buyer_id = ? ", ConstantsUtils.ORDER_INFO_STATUS_WAIT_RECEIVE, ConstantsUtils.ORDER_INFO_STATUS_ALREADY, buyer_id).<Long>get("cn");
            total.put("todo_pay_count", payWait);
            total.put("todo_receive_count", receiveWait);
            resultMap.put("total", total);
            //  return resultMap;
        } else {
            total.put("cart_count", 0);
            total.put("todo_pay_count", 0);
            total.put("todo_receive_count", 0);
            resultMap.put("total", total);
        }
        return resultMap;
        //}

        /*} catch (Exception e) {
            resultMap.put("total", null);
            return resultMap;
        }*/
    }


    /**
     * 根据验证码获取经销商信息
     */
    @GET("/allseller")
    public Map getAllSellerInfo() {
        long buyerId = SessionUtil.getUserId();
        //获取当前用户绑定的经销商信息sql
        String allSellerInfoSql = YamlRead.getSQL("getAllSellerInfo", "buyer/buyer");
        List<buyer_seller> buyerSellerList =  buyer_seller.dao.find(allSellerInfoSql, buyerId, ConstantsUtils.BUYER_SELLER_STATUS_BIDING);
        Map resultMap = new HashMap();
        resultMap.put("code",ConstantsUtils.HTTP_STATUS_OK_200);
        resultMap.put("sellerList",buyerSellerList);
        return resultMap;
    }
}