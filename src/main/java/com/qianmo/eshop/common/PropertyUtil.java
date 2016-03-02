package com.qianmo.eshop.common;

import com.qianmo.eshop.resource.seller.RetailerResource;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhangyang on 3/2/16.
 */
public class PropertyUtil {
    //从property文件中根据key获取对应的值
    public static String getProperty(String key) throws Exception {
        try {
            Properties p = new Properties();
            p.load(RetailerResource.class.getClassLoader().getResourceAsStream("application.properties"));
            String value = p.getProperty(key)==null?"":p.getProperty(key);
            return new String(value.getBytes("iso-8859-1"),"UTF-8");
        } catch(Exception e) {
            throw  e;
        }
    }
}