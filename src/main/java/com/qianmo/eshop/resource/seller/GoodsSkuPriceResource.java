package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.user.user_info;

import java.util.HashMap;

/**
 * Created by fxg06 on 2016/3/6.
 */
@API("/price")
public class GoodsSkuPriceResource extends GoodsResource{

    /**
     * 获取商品价格
     * @param goods_num 商品编号
     * @param sku_id 商品规格ID
     * @param name 零售商公司名称或者账号
     * @param page_start 从第几条开始
     * @param page_step 返回多少条
     * @return
     */
    @GET
    public HashMap price(long goods_num,long sku_id,int sku_price_status,String name,Integer page_start,Integer page_step){
        HashMap resultMap = new HashMap();

        if(page_start==null)
            page_start = ConstantsUtils.DEFAULT_PAGE_START;//默认从第1条开始
        if(page_step==null)
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;//默认返回10条

        String sql = YamlRead.getSQL("findUserAndPrice","seller/goods");
        FullPage<user_info> userList;
        if(name!=null && !"".equals(name)){
            sql = sql + " AND (a.account like '%?%' or a.nickname like '%?%')";
            userList =  user_info.dao.fullPaginate(page_start/page_step + 1,page_step,sql,goods_num,sku_id,sku_price_status,name,name);
        }else{
            userList = user_info.dao.fullPaginate(page_start/page_step + 1,page_step,sql,goods_num,sku_id,sku_price_status);
        }
        resultMap.put("goods_price",userList);
        resultMap.put("total_count",userList.getTotalRow());
        return resultMap;
    }

    public WebResult update(){
        return null;
    }
}
