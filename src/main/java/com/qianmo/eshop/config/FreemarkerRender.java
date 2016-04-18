package com.qianmo.eshop.config;

import cn.dreampie.common.Render;
import cn.dreampie.common.http.HttpRequest;
import cn.dreampie.common.http.HttpResponse;
import cn.dreampie.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by zhangyang on 16/04/07.
 */
public class FreemarkerRender extends Render {
    private static final Logger logger = Logger.getLogger(FreemarkerRender.class);
    //freemarker 配置

    FreemarkerRender() {

    }

    public void render(HttpRequest request, HttpResponse response, Object templateMap) {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(getClass(), "/templates");
        if (templateMap != null) {
            //out 中应该包括有template的name 以及返回的数据等
            String templateName = "";
            try {
                if (templateMap instanceof Map) {
                    templateName = ((Map) templateMap).get("name") == null ? "" : ((Map) templateMap).get("name").toString();
                    // 获取模板文件
                    Template t = cfg.getTemplate(templateName);
                    if(t != null) {
                        response.setContentType("text/html; charset=UTF-8");
                        Writer out = response.getWriter();
                        t.process(templateMap, out);
                    } else {
                        logger.warn("can not find template via name,please check it!");
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
