package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "sms_template")
public class sms_template extends Model<sms_template> {
    public final static sms_template dao = new sms_template();
}
