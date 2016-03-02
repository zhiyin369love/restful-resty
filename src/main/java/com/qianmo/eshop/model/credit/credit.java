package com.qianmo.eshop.model.credit;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "credit")
public class credit extends Model<credit> {
    public final static credit dao = new credit();


    /**
     * 根据验证码和类别获取验证信息
     */
    public credit getTotalPriceBySellerIdStatus(int sellerId, int status) {
        String sql  = "";
        sql = YamlRead.getSQL("getTotalPriceBySellerIdStatus","seller/seller");
        return credit.dao.findFirst(sql, sellerId, status);
    }
}
