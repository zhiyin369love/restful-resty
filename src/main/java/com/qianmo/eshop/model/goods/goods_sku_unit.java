package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.ConstantsUtils;
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
        String sql = YamlRead.getSQL("findSkuUint","seller/goods");
        List<goods_sku_unit> result = dao.find(sql);
        List physicalList = new ArrayList();
        List packageList = new ArrayList();
        if(result!=null && result.size()>0){
            for(goods_sku_unit unit:result){
                if(Integer.parseInt(unit.get("type").toString())== ConstantsUtils.PHYSICAL_UNIT){
                    physicalList.add(unit);
                }else{
                    packageList.add(unit);
                }
            }
        }
        /*
        物理规格单位
         */
        Map physicalMap = new HashMap();
        physicalMap.put("type",ConstantsUtils.PHYSICAL_UNIT);
        physicalMap.put("goods_sku_unit_list",physicalList);
        list.add(physicalMap);
        /*
        包装规格单位
         */
        Map packageMap = new HashMap();
        packageMap.put("type",ConstantsUtils.PACKAGE_UNIT);
        packageMap.put("goods_sku_unit_list",packageList);
        list.add(packageMap);
        return list;
    }
}
