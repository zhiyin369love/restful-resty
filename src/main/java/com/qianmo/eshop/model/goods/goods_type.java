package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "goods_type")
public class goods_type extends Model<goods_type> {
    public final static goods_type dao = new goods_type();

    /**
     *
     * @return
     */
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
}
