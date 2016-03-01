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

	/**************************验证码/邀请码类型**************************/
	/**注册**/
	public final static Integer INVITE_VERIFY_CODE_TYPE_REGISTER = 1;
	/**重置密码**/
	public final static Integer INVITE_VERIFY_CODE_TYPE_RESET = 2;
	/**邀请**/
	public final static Integer INVITE_VERIFY_CODE_TYPE_INVITE = 3;
	/**********************************************************/

	/**************************买家卖家绑定**************************/
	/**绑定中**/
	public final static Integer BUYER_SELLER_STATUS_BIDING = 0;
	/**取消绑定**/
	public final static Integer BUYER_SELLER_STATUS_BIDING_CANCEL = 1;
	/**********************************************************/

}
