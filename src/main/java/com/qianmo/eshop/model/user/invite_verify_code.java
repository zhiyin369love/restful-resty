package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "invite_verify_code")
public class invite_verify_code extends Model<invite_verify_code> {
    public final static invite_verify_code dao = new invite_verify_code();

    /**
     * 根据验证码和类别获取验证信息
     */
    public invite_verify_code getInviteByCode(String verifyCode, int type) {
        String sql  = "";
        sql = YamlRead.getSQL("getSellerIdByVerifyCode","buyer/buyer");
        return invite_verify_code.dao.unCache().findFirst(sql, verifyCode, type);
    }

    /**
     * 根据验证码和类别以及手机号获取验证信息
     */
    public invite_verify_code getInviteByCodePhone(int type,String phone) {
        String sql  = "";
        sql = YamlRead.getSQL("getSellerIdByVerifyCodePhone","buyer/buyer");
        return invite_verify_code.dao.unCache().findFirst(sql, type, phone);
    }

    /**
     * 根据验证码和类别获取验证信息
     */
    public invite_verify_code getInviteByBuyerAndSeller(long buyer_id, long seller_id) {
        return invite_verify_code.dao.findFirstBy("buyer_id = ? and seller_id = ?",buyer_id,seller_id);
    }
}
