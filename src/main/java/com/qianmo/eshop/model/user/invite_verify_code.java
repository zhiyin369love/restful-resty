package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.yamlRead;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "invite_verify_code")
public class invite_verify_code extends Model<invite_verify_code> {
    public final static invite_verify_code dao = new invite_verify_code();

    /**
     * 根据验证码和类别获取验证信息
     */
    public invite_verify_code getInviteByCode(int verifyCode, int type) {
        String sql  = "";
        sql = yamlRead.getSQL("getSellerIdByVerifyCode","buyer/buyer");
        return invite_verify_code.dao.findFirst(sql, verifyCode, type);
    }
}
