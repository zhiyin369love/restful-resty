package com.qianmo.eshop.resource.buyer;

import cn.dreampie.common.util.Maper;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.ConstantsUtils;
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
 * 获取所有订单信息 --买家订单列表
 *  author:wss
 *  传入参数：order_num：订单ID
 */

@API("/buyer/order")
public class OrderAllResouce extends ApiResource {
    @GET
    public HashMap getList(Integer order_num,Integer order_status,Integer page_start,Integer page_step) {

        HashMap result = new HashMap();
        String sql3 = YamlRead.getSQL("getFieldOrderInfoAll","buyer/order");
        String sql4 = YamlRead.getSQL("getFirldOrderRemarkAll","buyer/order");

        //商品信息
        HashMap result2 =  new HashMap();

       /* String sql2_1 = YamlRead.getSQL("getFirldGoodsInfoAll","buyer/order");
        String sql2_2 = YamlRead.getSQL("getFieldGoodsSkuListAll","buyer/order");
        String sql2_3 = YamlRead.getSQL("getFieldGoodsTypeALL","buyer/order");
*/
        OrderResource resource = new OrderResource();
        List<HashMap> resultMap = resource.getOrderHashMaps(order_num);


        //用户信息
        HashMap result3 =  new HashMap();
        String sql1_1 = YamlRead.getSQL("getFieldBuyerInfoAll","buyer/order");
        String sql1_2 = YamlRead.getSQL("getFieldBuyerReceiveAll","buyer/order");
        order_user o = new order_user();
        if(order_user.dao.find(sql1_1,order_num)!=null && order_user.dao.find(sql1_1,order_num).size()>0){
            o = order_user.dao.find(sql1_1,order_num).get(0);
        }
        result3.put("buyer_id",o.get("buyer_id"));
        result3.put("buyer_name",o.get("name"));
        result3.put("buyer_receive", buyer_receive_address.dao.find(sql1_2,order_num));

        FullPage<order_user> inviteCodeList  =  order_user.dao.fullPaginateBy(page_start/page_step + 1,page_step,"page_start = ? and page_step = ?",o.get("seller_id"), ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);

        result.put("buyer_info",result3);
        result.put("goods_list",resultMap);
        if ( order_status != null){
            sql3 = sql3 + " and oi.status = ?";
            result.put("order_info",order_info.dao.find(sql3,order_num,order_status));
        }else{
            result.put("order_info",order_info.dao.find(sql3,order_num));
        }
        HashMap count =  new HashMap();
        count.put("total_count",inviteCodeList.getTotalRow());

        result.put("order_remark_list", order_remark.dao.find(sql4,order_num));
        result.put("page_info",count);

        return result;
    }

}
