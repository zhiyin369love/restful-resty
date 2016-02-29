package com.qianmo.eshop.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** 
 * Cookie辅助工具类 
 */ 
public class CookieUtil {
	
	/**
	 * 设置cookie
	 * @param response
	 * @param name  cookie名字
	 * @param value cookie值
	 * @param maxAge cookie生命周期  以秒为单位
	 */
	public static void addCookie(HttpServletResponse response,String name,String value,int maxAge){
	    Cookie cookie = new Cookie(name,value);
	    cookie.setPath("/");
	    
	    if(maxAge>0)  cookie.setMaxAge(maxAge);
	    response.addCookie(cookie);
	}
	
	/**
	 * 根据名字获取cookie
	 * @param request
	 * @param name cookie名字
	 * @return
	 */
	public static Cookie getCookieByName(HttpServletRequest request,String name){
		
	    Map<String,Cookie> cookieMap = readCookieMap(request);
	    
	    if(cookieMap.containsKey(name)){
	        Cookie cookie = (Cookie)cookieMap.get(name);
	        return cookie;
	    }else{
	        return null;
	    }   
	}	
	
	/**
	 * 根据名字删除cookie
	 * @param request
	 * @param name cookie名字
	 * @return
	 */
	public static boolean deleteCookieByName(HttpServletRequest request,HttpServletResponse response,String name){
		addCookie(response, name, null, 0);
		boolean b = false;
		Cookie cookie = getCookieByName(request, name);
		 if(cookie!=null && !"".equals(cookie.getValue().trim())){
			 b = false;
		 }else{
			 b = true;
		 }
	    return b;
	}	
	
	
	/**
	 * 将cookie封装到Map里面
	 * @param request
	 * @return
	 */
	private static Map<String,Cookie> readCookieMap(HttpServletRequest request){  
	    Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
	    Cookie[] cookies = request.getCookies();
	    if(null!=cookies){
	        for(Cookie cookie : cookies){
	            cookieMap.put(cookie.getName(), cookie);
	        }
	    }
	    return cookieMap;
	}
}
