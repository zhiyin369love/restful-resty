package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "goods_sku_unit")
public class goods_sku_unit extends Model<goods_sku_unit> {
    public final static goods_sku_unit dao = new goods_sku_unit();

    public List getList(){
        List list = new ArrayList();
        String sql = YamlRead.getSQL("findSkuUint","/seller/goods");
        List<goods_sku_unit> result = dao.find(sql);
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
