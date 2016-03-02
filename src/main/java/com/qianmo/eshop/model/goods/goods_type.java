package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.bean.goods.GoodsType;
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
     * 获取商品分类
     * @return
     */
    public List getList(){
        List list = new ArrayList();
        List<goods_type> parentList = dao.findBy("pid=0");
        if(parentList!=null && parentList.size()>0){
            for (goods_type type:parentList){
                GoodsType goodsType = new GoodsType();
                goodsType.setType_id(Integer.parseInt(type.get("id").toString()));
                goodsType.setType_name(type.get("name").toString());
                List<goods_type> childList = dao.findBy("pid=?",type.get("id"));
                List<GoodsType> typeList = new ArrayList<GoodsType>();
                if(childList!=null && childList.size()>0){
                    for(goods_type childType:childList){
                        GoodsType childGoodsType = new GoodsType();
                        childGoodsType.setType_id(Integer.parseInt(childType.get("id").toString()));
                        childGoodsType.setType_name(childType.get("name").toString());
                        typeList.add(childGoodsType);
                    }
                }
                goodsType.setGoods_type_list(typeList);
                list.add(goodsType);
            }
        }
        return list;
    }

}
