package com.qianmo.eshop.resource.seller;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.bean.goods.GoodsType;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku_unit;
import com.qianmo.eshop.model.goods.goods_type;
import com.qianmo.eshop.model.user.user_info;

import java.util.List;

/**
 * Created by fxg06 on 2016/3/1.
 */
@API("/type")
public class GoodsTypeRescource extends GoodsResource {
    /**
     * 获取商品分类
     * @return
     */
    @GET
    public List list(){
        return goods_type.dao.getList();
    }

    /**
     * 获取商品分类及该商品分类下的商品总数
     * @param goods_name
     * @return
     */
    @GET("/count")
    public List list(String goods_name,long seller_id){
        user_info user = user_info.dao.findById(seller_id);
        //判断添加商品的用户是否为子账号，如果是则获取其父级id
        if(Integer.parseInt(user.get("pid").toString())==0){
            seller_id = user.get("id");
        }else{
            seller_id = user.get("pid");
        }
        List<GoodsType> list = goods_type.dao.getList();
        if(list!=null && list.size()>0){
            for(GoodsType type:list){
                long count = 0;
                List<GoodsType> childList = (List)type.getGoods_type_list();
                if(childList!=null && childList.size()>0){
                    for(GoodsType childType:childList){
                        long childCount = goods_info.dao.queryFirst(YamlRead.getSQL("findGoodsCount","seller/goods"),childType.getType_id());
                        childType.setGoods_count(childCount);
                        count += childCount;
                    }
                }
                type.setGoods_count(count);
            }
        }
        return list;
    }

    /**
     * 获取商品规格单位信息
     * @return
     */
    @GET("/unit")
    public List getSkuUnit(){
        return goods_sku_unit.dao.getList();
    }
}
