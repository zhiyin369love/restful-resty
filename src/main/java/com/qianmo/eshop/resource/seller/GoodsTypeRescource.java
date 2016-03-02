package com.qianmo.eshop.resource.seller;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_sku_unit;
import com.qianmo.eshop.model.goods.goods_type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List getList(){
        List list = new ArrayList();
        String sql = YamlRead.getSQL("findType","/seller/goods");
        List<goods_type> result  = goods_type.dao.find(sql);
        if(result!=null && result.size()>0){
            for (goods_type type:result){
               if(Integer.parseInt(type.get("pid").toString())==0){
                   List<goods_type> childList = new ArrayList<goods_type>();
                   HashMap map = new HashMap();
                   for(goods_type child:result){
                       if(Integer.parseInt(child.get("pid").toString())==Integer.parseInt(type.get("type_id").toString())){
                           childList.add(child);
                       }
                   }
                   map.put("type_id",type.get("type_id"));
                   map.put("type_name",type.get("type_name"));
                   map.put("goods_type_list",childList);
                   list.add(map);
               }
            }
        }
        return list;
    }

    /**
     * 获取商品分类及该商品分类下的商品总数
     * @param goods_name
     * @return
     */
    @GET("/count")
    public List getTypeList(String goods_name){

        return null;
    }

    /**
     * 获取商品规格单位信息
     * @return
     */
    @GET("/unit")
    public List getSkuUnitList(){
        List list = new ArrayList();
        String sql = YamlRead.getSQL("findSkuUint","/seller/goods");
        List<goods_sku_unit> result = goods_sku_unit.dao.find(sql);
        List list1 = new ArrayList();
        List list2 = new ArrayList();
        if(result!=null && result.size()>0){
            for(goods_sku_unit unit:result){
                if(Integer.parseInt(unit.get("type").toString())==0){
                    list1.add(unit);
                }else{
                    list2.add(unit);
                }
            }
        }
        Map map1 = new HashMap();
        map1.put("type",0);
        map1.put("goods_sku_unit_list",list1);
        list.add(map1);

        Map map2 = new HashMap();
        map2.put("type",1);
        map2.put("goods_sku_unit_list",list2);
        list.add(map2);
        return list;
    }
}
