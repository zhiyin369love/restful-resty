package com.qianmo.eshop.resource.buyer;


import cn.dreampie.orm.page.Page;
import cn.dreampie.route.annotation.*;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.news.news;
import com.qianmo.eshop.model.news.news_type;

import java.util.HashMap;
import java.util.List;

/**
 * 买家资讯api
 * Created by ccq on 16-1-1.
 */
@API("/news")
public class NewsResource extends BuyerResource {

    /**
     * 获取资讯列表
     * @param page_start 从第几页开始取数据
     * @param page_step  取多少数据
     * @return
     */
    @GET
    public Page<news> list(Integer page_start, Integer page_step, Integer news_type_id) {
        if (page_start == null || page_start == 0) {
            page_start = ConstantsUtils.DEFAULT_PAGE_START;
        }
        if (page_step == null || page_step == 0) {
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
        }
        int pageNumber = page_start / page_step + 1;
        Page<news> newsInfoPage;
        HashMap result = new HashMap();
        String newsInfoSql = YamlRead.getSQL("getNewsAll", "buyer/news");
        if(news_type_id != null){
            if(news_type_id != 0){
                newsInfoSql += " where a.news_type_id = " + news_type_id;
            }
        }
        newsInfoPage = news.dao.fullPaginate(pageNumber, page_step, newsInfoSql);
        return newsInfoPage;
    }

    /**
     * 获取资讯详情
     *
     * @param id 资讯ID
     * @return
     */
    @GET("/:id")
    public HashMap details(long id) {
        HashMap result = new HashMap();
        String newsInfoSql = YamlRead.getSQL("getNewsDetails", "buyer/news");
        news newsInfo = news.dao.findFirst(newsInfoSql,id);
        result.put("news", newsInfo);
        return result;
    }

    @GET("/type")
    public HashMap gettype() {
        HashMap result = new HashMap();
        List<news_type> newsTypeInfo = news_type.dao.findAll();
        result.put("news_type_list", newsTypeInfo);
        return result;
    }
}