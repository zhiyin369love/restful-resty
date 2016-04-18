package com.qianmo.eshop.config;

import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.orm.ActiveRecordPlugin;
import cn.dreampie.orm.provider.druid.DruidDataSourceProvider;
import cn.dreampie.route.config.*;
import cn.dreampie.route.handler.cors.CORSHandler;
import cn.dreampie.route.interceptor.security.SecurityInterceptor;
import cn.dreampie.route.interceptor.transaction.TransactionInterceptor;
import cn.dreampie.security.builder.BothSessionBuilder;
import cn.dreampie.route.cache.CacheInterceptor;

/**
 * Created by ccq on 16-1-16.
 */
public class AppConfig extends Config {
  public void configConstant(ConstantLoader constantLoader) {
    //单页应用 避免被resty解析路径
    constantLoader.addRender("ftl",new com.qianmo.eshop.config.FreemarkerRender());
    constantLoader.setDefaultForward("/");
  }

  public void configResource(ResourceLoader resourceLoader) {
//设置resource的目录  减少启动扫描目录
    resourceLoader.addIncludePackages("com.qianmo.eshop.resource");
  }

  public void configPlugin(PluginLoader pluginLoader) {
    DruidDataSourceProvider ddsp = new DruidDataSourceProvider("default");
    ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(ddsp);
    activeRecordPlugin.addIncludePackages("com.qianmo.eshop.model");

    pluginLoader.add(activeRecordPlugin);
  }

  public void configInterceptor(InterceptorLoader interceptorLoader) {
    //interceptorLoader.add(new CacheInterceptor());
    //权限拦截器 limit 为最大登录session数
    //interceptorLoader.add(new SecurityInterceptor(new MyAuthenticateService()));
    //http session失效时间为24小时
    interceptorLoader.add(new SecurityInterceptor(new BothSessionBuilder(24 * 60 * 60 * 1000, -1, 7, new MyAuthenticateService())));
    //事务的拦截器 @Transaction
    interceptorLoader.add(new TransactionInterceptor());
    //输入的统一校验
    interceptorLoader.add(new InputValidInterceptor());
  }

  public void configHandler(HandlerLoader handlerLoader) {
    //跨域
    handlerLoader.add(new CORSHandler("GET,POST,PUT,DELETE"));
  }
}
