package com.qianmo.eshop.resource.seller;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.common.http.result.WebResult;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.route.annotation.PUT;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.buyer.buyer_seller;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.goods.goods_sku_price;
import com.qianmo.eshop.model.user.user_info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    /**
     * 批量修改商品价格
     * @param goods_price_list
     * @param goods_num 商品编号
     * @param status 状态 1：可购买 0：不可购买
     * @return
     */
    @PUT("/batch")
    @Transaction
    public WebResult edit(List<goods_sku_price> goods_price_list,Integer goods_num,Integer status){
        user_info userInfo = (user_info) Subject.getPrincipal().getModel();
        long seller_id = 0;
        //判断登录用户是否为子账号，如果是则获取其父级id
        if(userInfo!=null){
            if(Long.parseLong(userInfo.get("pid").toString())==0){
                seller_id = Long.parseLong(userInfo.get("id").toString());
            }else{
                seller_id = Long.parseLong(userInfo.get("pid").toString());
            }

            if(goods_price_list!=null && goods_price_list.size()>0){
                for(goods_sku_price sku_price:goods_price_list){
                    /*
                    sku_price_id不为空时修改价格，否则新增
                     */
                    if (sku_price.get("sku_price_id")!=null){
                        goods_sku_price.dao.updateColsBy("price,status","id=?",sku_price.get("price"),sku_price.get("price_status"),sku_price.get("sku_price_id"));
                    }else{
                        goods_sku_price price = new goods_sku_price();
                        price.set("area_id",ConstantsUtils.ALL_AREA_ID);
                        price.set("price",sku_price.get("price"));
                        price.set("status",sku_price.get("price_status"));
                        price.set("sku_id",sku_price.get("sku_id"));
                        price.set("buyer_id",sku_price.get("buyer_id"));
                        price.set("seller_id",seller_id);
                        price.set("goods_num",goods_num);
                        price.save();
                    }
                }
            }
        }
        return new WebResult(HttpStatus.OK, "修改商品价格成功");
    }

    /**
     * 获取经销商下单个商品所有用户可购买不可购买总数
     * @param sku_id 商品规格ID
     * @return
     */
    @GET("/count")
    public List count(long sku_id){
        user_info userInfo = (user_info) Subject.getPrincipal().getModel();
        long seller_id = 0;
        //判断登录用户是否为子账号，如果是则获取其父级id
        if(userInfo!=null){
            if(Long.parseLong(userInfo.get("pid").toString())==0){
                seller_id = Long.parseLong(userInfo.get("id").toString());
            }else{
                seller_id = Long.parseLong(userInfo.get("pid").toString());
            }
        }
        List list = new ArrayList();
        //查询经销商下所有零售商总数
        int userCount = buyer_seller.dao.queryFirst(YamlRead.getSQL("findAllUserCount","seller/seller"),seller_id);
        //查询不可购买的零售商总数
        int count = goods_sku_price.dao.queryFirst(YamlRead.getSQL("findPriceCount","seller/goods"),seller_id,sku_id);

        /*
        不可购买
         */
        HashMap notBuyMap = new HashMap();
        notBuyMap.put("status",ConstantsUtils.GOODS_SKU_PRICE_BUY_DISABLE);
        notBuyMap.put("count",count);
        list.add(notBuyMap);
        /*
        可购买
         */
        HashMap buyMap = new HashMap();
        notBuyMap.put("status",ConstantsUtils.GOODS_SKU_PRICE_BUY_ENBLE);
        notBuyMap.put("count",count);
        list.add(buyMap);

        return list;
    }

    /**
     * 修改商品默认价格
     * @param sku_id
     * @param price
     * @return
     */
    public WebResult edit(long sku_id,double price){
        goods_sku sku = goods_sku.dao.findById(sku_id);
        if(sku!=null){
            sku.set("list_price",price);
            sku.update();
        }
        return new WebResult(HttpStatus.OK,"价格设置成功");
    }
}
