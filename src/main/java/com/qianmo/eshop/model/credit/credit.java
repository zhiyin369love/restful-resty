package com.qianmo.eshop.model.credit;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "credit")
public class credit extends Model<credit> {
    public final static credit dao = new credit();
}
