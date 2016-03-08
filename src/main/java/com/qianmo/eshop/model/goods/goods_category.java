package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.bean.goods.GoodsCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fxg06 on 2016/3/7.
 */
@Table(name = "goods_category")
public class goods_category extends Model<goods_category> {
    public final static goods_category dao = new goods_category();

    /**
     * 获取商品分类
     * @return
     */
    public List getList(){
        List list = new ArrayList();
        //查询一级商品分类
        List<goods_category> parentList = dao.findBy("pid=0 and deleted_at is null");
        if(parentList!=null && parentList.size()>0){
            for (goods_category category:parentList){
                GoodsCategory goodsCategory = new GoodsCategory();
                goodsCategory.setCategory_id(Long.parseLong(category.get("id").toString()));
                goodsCategory.setCategory_name(category.get("name").toString());
                //根据一级商品分类查询二级商品分类
                List<goods_category> childList = dao.findBy("pid=? and deleted_at is null",category.get("id"));
                List<GoodsCategory> categoryList = new ArrayList<GoodsCategory>();
                if(childList!=null && childList.size()>0){
                    for(goods_category childCategory:childList){
                        GoodsCategory childGoodsCategory = new GoodsCategory();
                        childGoodsCategory.setCategory_id(Long.parseLong(childCategory.get("id").toString()));
                        childGoodsCategory.setCategory_name(childCategory.get("name").toString());
                        categoryList.add(childGoodsCategory);
                    }
                }
                goodsCategory.setGoods_category_list(categoryList);
                list.add(goodsCategory);
            }
        }
        return list;
    }
}
