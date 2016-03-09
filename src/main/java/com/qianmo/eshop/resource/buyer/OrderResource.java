package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.common.util.Maper;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.alibaba.fastjson.JSONObject;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_receive_address;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.order.order_goods;
import com.qianmo.eshop.model.order.order_info;
import com.qianmo.eshop.model.order.order_remark;
import com.qianmo.eshop.model.order.order_user;
import com.qianmo.eshop.resource.z_common.ApiResource;
import org.apache.poi.ss.formula.functions.T;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 查看单个订单详情
 * author:wss
 * 传入参数说明：id 订单ID
 */

@API("/order")
public class OrderResource extends BuyerResource {

    @GET("/:id")
    public HashMap getList(int id) {
       HashMap result = new HashMap();
       try {

        //订单实体查询sql
        String sqlOrderinfo = YamlRead.getSQL("getFieldOrderInfoAll","buyer/order");
        //订单备注列表查询sql
        String sqlOrderremark = YamlRead.getSQL("getFirldOrderRemarkAll","buyer/order");
        //商品实体列表查询sql
           List<HashMap> resultMap = getOrderHashMaps(id);

        //买家信息实体查询sql
        HashMap result_buyer = new HashMap();
        String sqlbuyer_info = YamlRead.getSQL("getFieldBuyerInfoAll","buyer/order");
        //买家收货人实体
        String sqlbuyer_receive = YamlRead.getSQL("getFieldBuyerReceiveAll","buyer/order");
        order_user o = new order_user();
           List<order_user> order_users_list = order_user.dao.find(sqlbuyer_info,id);
        if(order_users_list!=null && order_users_list.size()>0){
         o = order_users_list.get(0);
        }
           result_buyer.put("buyer_id",o.get("buyer_id"));
           result_buyer.put("buyer_name",o.get("name"));
           result_buyer.put("buyer_receive", buyer_receive_address.dao.find(sqlbuyer_receive,id));

        //返回json
        result.put("buyer_info",result_buyer);
        result.put("goods_list",resultMap);
        result.put("order_info",order_info.dao.find(sqlOrderinfo,id));
        result.put("order_remark_list", order_remark.dao.find(sqlOrderremark,id));
        return result;
       }catch (Exception e) {
        //异常情况，方便记录日志 TODO
        result.put("buyer_info",null);
        result.put("goods_list",null);
        result.put("order_info",null);
        result.put("order_remark_list",null);
        return result;
       }
    }
//商品实体封装
    public List<HashMap> getOrderHashMaps(long id) {
        HashMap resultgoods =  new HashMap();
        //商品信息
        String sqlgoods_info = YamlRead.getSQL("getFirldGoodsInfoAll","buyer/order");
        //商品规格列表
        String sqlgoodssku = YamlRead.getSQL("getFieldGoodsSkuAll","buyer/order");
        //商品分类
        String sqlgoodstype = YamlRead.getSQL("getFieldGoodsTypeALL","buyer/order");
        Map goodsResult = new HashMap();

        List<goods_info>  goods_infoList =  goods_info.dao.find(sqlgoods_info,id);
        List<HashMap> resultMap = new ArrayList<HashMap>();
        for (goods_info goodlist: goods_infoList ){
            resultgoods.clear();
            resultgoods.put("goods_info", goods_info.dao.find(sqlgoods_info,id));
            long goodsNum = (Long)((JSONObject)goodlist.get("goods_info")).get("id");
            long category_id = (Long)((JSONObject)goodlist.get("goods_info")).get("category_id");
            resultgoods.put("goods_sku_list", goods_sku.dao.find(sqlgoodssku,goodsNum));
            resultgoods.put("goods_type", goods_category.dao.find(sqlgoodstype,category_id));
            resultMap.add(resultgoods);
        }
        return resultMap;
    }
}
