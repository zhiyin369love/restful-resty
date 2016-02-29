package com.qianmo.eshop.common;

/**
 * 
 * @ClassName: ConstantsUtils
 * @Description: 静态变量工具
 * @date 2015年6月11日 上午11:19:46
 *
 */
public class ConstantsUtils {
	
	/**************************支付方式**************************/
	/**支付方式-余额**/
	public final static Integer ORDER_PAYMENTTYPE_CASH = 0;
	/**支付方式-线上**/
	public final static Integer ORDER_PAYMENTTYPE_ONLINE = 1;
	/**********************************************************/

	/**************************支付状态**************************/
	/**支付状态-取消支付**/
	public final static Integer ORDER_PAYMENT_STATUS_CANCALED = 0;
	/**支付状态-等待支付**/
	public final static Integer ORDER_PAYMENT_STATUS_WAITE = 1;
	/**支付状态-已支付**/
	public final static Integer ORDER_PAYMENT_STATUS_RECEIVED = 2;
	/**********************************************************/

}
