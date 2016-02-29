package com.qianmo.eshop.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换
 * @author dongxy
 */
public class DateUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * 字符串转化成日期
     * @param date
     * @return
     */
    public static Date formatDate(String date){
        try {
           return dateFormat.parse(date);
        }catch (ParseException e) {
        }
        return null;
    }
    
    /**
     * 字符串转化成日期
     * @param date
     * @return
     */
    public static Date formatDate(String date,String dateFormats){
        try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormats);
           return dateFormat.parse(date);
        }catch (ParseException e) {
        }
        return null;
    }
    
    
    /**
     * 格式化日期  yyyy-MM-dd
     * @param date
     * @return
     */
    public static String getDateString(Date date){
        
        return dateFormat.format(date);
    }
    
    /**
     * 格式化日期  yyyy-MM-dd
     * @param date
     * @return
     */
    public static String getDateString(Date date,String dateFormatString){
    	SimpleDateFormat dateFormatIn = new SimpleDateFormat(dateFormatString);
        return dateFormatIn.format(date);
    }
    
}
