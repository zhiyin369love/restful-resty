package com.qianmo.eshop.model.news;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "news_type")
public class news_type extends Model<news_type> {
    public final static news_type dao = new news_type();
}
