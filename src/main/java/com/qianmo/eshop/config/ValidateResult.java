package com.qianmo.eshop.config;

/**
 * @author  zhangyang
 * 校验通用结果
 */
public class ValidateResult {
	private String returnCode;// key值同OutputObject的returnCode
	private String returnMessage;// key值同OutputObject的returnMessage

	public interface ERROR_MSG {
		String REQUIRED = "是必填的";
		String NUM = "不是数字";
		String DATE = "不是正确的日期格式";
		String LENGTH = "长度不正确";
		String MAXLENGTH = "不能大于最大长度";
		String MINLENGTH = "不能小于最小长度";
		String RANGE = "不在范围内";
		String EMAIL = "错误的电子邮箱";
		String IDCARD = "错误的idcard";
		String PHONE = "错误的手机号码";
		String URL = "错误的url";
		String IP = "错误的ip";
		String POSTCODE = "错误的邮编";
		String REGEX = "错误的正则表达式";
	}

	public interface WARNING_MSG {
		String VALIDATE_FORMAT = "校验格式有问题";
		String VALIDATE_LENGTH = "校验长度有问题";

	}

	/**
	 * Constructor
	 **/
	public ValidateResult(String returnCode, String returnMessage) {
		this.setReturnCode(returnCode);
		this.setReturnMessage(returnMessage);
	}

	/**
	 * Getters And Setters
	 **/
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public String toString() {
		return "returnCode:" + returnCode + "\treturnMessage:" + returnMessage;
	}

	public String toJson() {
		return "{\"returnCode\":\"" + returnCode + "\",\"returnMessage\":\"" + returnMessage + "\"}";
	}
}