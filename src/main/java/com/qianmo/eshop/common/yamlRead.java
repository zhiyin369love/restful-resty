package com.qianmo.eshop.common;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Map;

/**
 * Created by ccq on 2/2/16.
 */
public class yamlRead {

    //从yaml文件中根据key获取对应的sql语句
    public static String getSQL(String key,String filename){
        String sql = null;
        try {
            Yaml yaml = new Yaml();
            URL url = getURL(filename);
            if (url != null) {
                Map<String, Object> data = (Map<String, Object>)yaml.load(new FileInputStream(url.getFile()));
                sql = data.get(key).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
    }


    //根据名称获取对应的yaml文件
    public static URL getURL(String key){
        URL url = null;
        try {
            url = yamlRead.class.getClassLoader().getResource("sqlyaml/" + key + ".yaml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}