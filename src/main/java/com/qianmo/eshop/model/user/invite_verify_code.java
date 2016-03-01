package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "invite_verify_code")
public class invite_verify_code extends Model<invite_verify_code> {
    public final static invite_verify_code dao = new invite_verify_code();
}
