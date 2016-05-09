package com.qianmo.eshop.model.news;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "news")
public class news extends Model<news> {
    public final static news dao = new news();
}
