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
        String sql3 = YamlRead.getSQL("getFieldOrderInfoAll","buyer/order");
        //订单备注列表查询sql
        String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","buyer/order");
        //商品实体列表查询sql
           List<HashMap> resultMap = getOrderHashMaps(id);

        //买家信息实体查询sql
        HashMap result3 = new HashMap();
        String sql1_1 = YamlRead.getSQL("getFieldBuyerInfoAll","buyer/order");
        //买家收货人实体
        String sql1_2 = YamlRead.getSQL("getFieldBuyerReceiveAll","buyer/order");
        order_user o = new order_user();
        if(order_user.dao.find(sql1_1,id)!=null && order_user.dao.find(sql1_1,id).size()>0){
         o = order_user.dao.find(sql1_1,id).get(0);
        }
        result3.put("buyer_id",o.get("buyer_id"));
        result3.put("buyer_name",o.get("name"));
        result3.put("buyer_receive", buyer_receive_address.dao.find(sql1_2,id));

        //返回json
        result.put("buyer_info",result3);
        result.put("goods_list",resultMap);
        result.put("order_info",order_info.dao.find(sql3,id));
        result.put("order_remark_list", order_remark.dao.find(sql4,id));
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
        HashMap result2 =  new HashMap();
        //商品信息
        String sql2_1 = YamlRead.getSQL("getFirldGoodsInfoAll","buyer/order");
        //商品规格列表
        String sql2_2 = YamlRead.getSQL("getFieldGoodsSkuAll","buyer/order");
        //商品分类
        String sql2_3 = YamlRead.getSQL("getFieldGoodsTypeALL","buyer/order");
        Map goodsResult = new HashMap();

        List<goods_info>  goods_infoList =  goods_info.dao.find(sql2_1,id);
        List<HashMap> resultMap = new ArrayList<HashMap>();
        for (goods_info goodlist: goods_infoList ){
            result2.clear();
            result2.put("goods_info", goods_info.dao.find(sql2_1,id));
            long goodsNum = (Long)((JSONObject)goodlist.get("goods_info")).get("id");
            long category_id = (Long)((JSONObject)goodlist.get("goods_info")).get("category_id");
            result2.put("goods_sku_list", goods_sku.dao.find(sql2_2,goodsNum));
            result2.put("goods_type", goods_category.dao.find(sql2_3,category_id));
            resultMap.add(result2);
        }
        return resultMap;
    }
}
