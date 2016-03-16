package com.qianmo.eshop.config;


import com.qianmo.eshop.config.ValidateResult.ERROR_MSG;

/**
 * 校验类型枚举
 * Created by zhangyang on 2016/3/16 0016.
 */
public enum ValidateType {
    required(1, null, ERROR_MSG.REQUIRED),    // 必填字段校验
    num(2, REGEX.NUM, ERROR_MSG.NUM),            // 数字校验
    date(3, REGEX.DATE, ERROR_MSG.DATE),        // 时间校验
    length(4, null, ERROR_MSG.LENGTH),        // 长度校验
    maxlength(5, null, ERROR_MSG.MAXLENGTH),    // 长度最大值校验
    minlength(6, null, ERROR_MSG.MINLENGTH),    // 长度最小值校验
    range(7, null, ERROR_MSG.RANGE),        // 区间校验
    email(8, REGEX.EMAIL, ERROR_MSG.EMAIL),        // 邮箱校验
    idcard(9, null, ERROR_MSG.IDCARD),        // 证件号校验
    phone(10, REGEX.PHONE, ERROR_MSG.PHONE),        // 手机号校验
    url(11, REGEX.URL, ERROR_MSG.URL),            // url校验
    ip(12, REGEX.IP, ERROR_MSG.IP),            // ip地址校验
    postcode(13, REGEX.POSTCODE, ERROR_MSG.POSTCODE),    // 国内邮编校验
    regex(14, null, ERROR_MSG.REGEX);        // 正则表达式校验

    private int order;// 校验顺序
    private String regexs;// 校验正则表达式
    private String regVal;// 校验值
    private String errMsg;// 错误提示信息的key值


    public interface REGEX {
        // 浮点数校验
        String NUM = "^(-?\\d+)(\\.\\d+)?$";
        // 国内邮编地址校验
        String POSTCODE = "[1-9]{1}(\\d+){5}";
        // 国内ip地址校验
        String IP = "^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$";
        // Email格式校验
        String EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        // 15位身份证号
        String IDCARD_15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
        // 18位身份证号
        String IDCARD_18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|x|X)$";
        // 时间校验
        String DATE = "^[2](\\d{3})-?([0-1]{1}\\d{1})-?([0-3]{1}\\d{1})?\\s?([0-2]\\d{1})?:?([0-5]\\d{1})?:?([0-5]\\d{1})?$";
        // 手机号、电话号码校验
        String PHONE = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";
        // Url校验
        String URL = "^[h][t]{2}[p][:][\\/][\\/][w]{3}[\\.][0-9A-Za-z]+[\\.][a-z]{2,3}([\\/][0-9A-Za-z]+)+([\\/][0-9A-Za-z]+[.][a-z]+)?$";
    }

    /**
     * 私有构造器 Setters and Getters
     */
    private ValidateType(int order, String regexs, String errMsg) {
        this.setOrder(order);
        this.setRegexs(regexs);
        this.setErrMsg(errMsg);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getRegexs() {
        return regexs;
    }

    public void setRegexs(String regexs) {
        this.regexs = regexs;
    }

    public String getRegVal() {
        return regVal;
    }

    public void setRegVal(String regVal) {
        this.regVal = regVal;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}

