package com.qianmo.eshop.model.goods;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

import java.util.List;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "goods_form")
public class goods_form extends Model<goods_form> {
    public final static goods_form dao = new goods_form();

    /**
     * 获取商品剂型
     *
     * @param categoryId 商品分类id
     * @return
     */
    public List getFirmList(Long categoryId){
        List list = find(YamlRead.getSQL("findGoodsForm", "seller/goods"), categoryId);
        return list;
    }
}
