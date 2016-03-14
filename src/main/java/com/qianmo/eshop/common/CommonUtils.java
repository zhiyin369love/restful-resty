package com.qianmo.eshop.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.dreampie.common.http.result.HttpStatus;
import org.apache.commons.codec.binary.Base64;


public final class CommonUtils {

  public static boolean isNotEmpty(Object o) {
    return !isEmpty(o);
  }


  public static boolean isEmpty(Object value) {
    if (value == null) return true;

    if (value instanceof String) return ((String) value).length() == 0;
    if (value instanceof Object[]) return ((Object[]) value).length == 0;
    if (value instanceof Collection) return ((Collection<? extends Object>) value).size() == 0;
    if (value instanceof Map) return ((Map<? extends Object, ? extends Object>) value).size() == 0;
    if (value instanceof CharSequence) return ((CharSequence) value).length() == 0;

    // These types would flood the log
    // Number covers: BigDecimal, BigInteger, Byte, Double, Float, Integer,
    // Long, Short
    if (value instanceof Boolean) return false;
    if (value instanceof Number) return false;
    if (value instanceof Character) return false;
    if (value instanceof java.util.Date) return false;

    return false;
  }

  
  public static boolean isChinaMobileNo(String mobile){
	  Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$");
    Matcher m = p.matcher(mobile); 
    return m.matches();
  }

  public static boolean isNumber(String input) {
    char[] chars = input.toCharArray();
    for (char c : chars) {
      if (!Character.isDigit(c)) return false;
    }
    return true;
  }

  public static boolean isEmpty(String input) {
    return input == null || input.trim().length() == 0;
  }

  public static String md5(String input) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
      md.update(input.getBytes());

      byte byteData[] = md.digest();

      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < byteData.length; i++) {
        sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
      }

      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < byteData.length; i++) {
        String hex = Integer.toHexString(0xff & byteData[i]);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      return null;
    }

  }

  public static String genUUID() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString().toLowerCase();
  }

  /**
   * Encodes the byte array into base64 string
   *
   * @param imageByteArray - byte array
   * @return String a {@link String}
   */
  public static String encodeImage(byte[] imageByteArray) {
    return Base64.encodeBase64URLSafeString(imageByteArray);
  }

  /**
   * Decodes the base64 string into byte array
   *
   * @param imageDataString - a {@link String}
   * @return byte array
   */
  public static byte[] decodeImage(String imageDataString) {
    return Base64.decodeBase64(imageDataString);
  }


  public static void randomList(List<?> list) {  
      Collections.sort(list, new Comparator(){  
          HashMap map = new HashMap();  
          public int compare(Object v1, Object v2) {  
              init(v1);  
              init(v2);  
                
              double n1 = ((Double)map.get(v1)).doubleValue();  
              double n2 = ((Double)map.get(v2)).doubleValue();  
              if(n1 > n2)  
                  return 1;  
              else if(n1 < n2)  
                  return -1;  
              return 0;  
          }  
          private void init(Object v){  
              if(map.get(v) == null){  
                  map.put(v, new Double(Math.random()));  
              }  
          }  
          protected void finalize() throws Throwable {  
              map = null;  
          }  
      });
  }

    public static String getRandNum(int charCount) {
        String charValue = "";
        for (int i = 0; i < charCount; i++) {
            char c = (char) (randomInt(0, 10) + '0');
            charValue += String.valueOf(c);
        }
        return charValue;

    }

    public static int randomInt(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }

    public static HashMap EditreturnCodeMessage(boolean istrue){
        HashMap result = new HashMap();
        int code = HttpStatus.OK.getCode();
        String message = "编辑失败";
        if(istrue){
            message = "编辑成功";
        }
        result.put("code",code);
        result.put("message",message);
        return result;
    }

    public static HashMap AddreturnCodeMessage(boolean istrue){
        HashMap result = new HashMap();
        int code = HttpStatus.OK.getCode();
        String message = "添加失败";
        if(istrue){
            message = "添加成功";
        }
        result.put("code",code);
        result.put("message",message);
        return result;
    }

    public static HashMap DelreturnCodeMessage(boolean istrue){
        HashMap result = new HashMap();
        int code = HttpStatus.OK.getCode();
        String message = "删除失败";
        if(istrue){
            message = "删除成功";
        }
        result.put("code",code);
        result.put("message",message);
        return result;
    }

}
