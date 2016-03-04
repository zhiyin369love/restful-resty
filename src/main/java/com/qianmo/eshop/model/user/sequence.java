package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by fxg06 on 2016/3/3.
 */
@Table(name = "sequence", cached = true)
public class sequence extends Model<sequence> {
    public final static sequence dao = new sequence();
}
