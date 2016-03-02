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

	/**************************订单状态**************************/
	/**订单状态-已取消**/
	public final static Integer ORDER_INFO_STATUS_CACEL = 0;
	/**订单状态-已创建**/
	public final static Integer ORDER_INFO_STATUS_CREATED = 1;
	/**订单状态-待收货**/
	public final static Integer ORDER_INFO_STATUS_WAIT_RECEIVE = 2;
	/**订单状态-已完成**/
	public final static Integer ORDER_INFO_STATUS_FINISHED = 3;
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
	public final static Integer BUYER_SELLER_STATUS_BIDING = 1;
	/**取消绑定**/
	public final static Integer BUYER_SELLER_STATUS_BIDING_CANCEL = 0;
	/**********************************************************/

	/**************************地区id**************************/
	/**地区id**/
	public final static Integer ALL_AREA_ID = 1;
	/**********************************************************/

	/***************************销账未销账************************/
	/**未销账**/
	public final static Integer CREDIT_CANCEL_STATUS = 0;
	/**销账**/
	public final static Integer CREDIT_ALREADY_STATUS = 1;
	/**********************************************************/

	/***************************商品************************/
	/**商品未上架**/
	public final static Integer GOODS_WAIT_SELL = 0;
	/**商品已上架**/
	public final static Integer GOODS_SELLING = 1;
	/**********************************************************/

	/***************************邀请码************************/
	/**邀请码状态 有效**/
	public final static Integer INVITE_CODE_STATUS_SUCCESSED = 0;
	/**邀请码状态 失效**/
	public final static Integer INVITE_CODE_STATUS_EXPIRED = 1;
	/**********************************************************/
}
