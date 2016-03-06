package com.qianmo.eshop.common;

/**
 * 
 * @ClassName: ConstantsUtils
 * @Description: 静态变量工具
 * @date 2015年6月11日 上午11:19:46
 *
 */
public class ConstantsUtils {
	/**************************商品规格单位**************************/
	/**物理单位**/
	public final static Integer PHYSICAL_UNIT = 0;
	/**包装单位**/
	public final static Integer PACKAGE_UNIT = 0;
	/**********************************************************/

	/**************************数字1是0否**************************/
	public final static Integer NO = 0;
	public final static Integer YES = 1;

	public final static Integer ZERO = 0;
	public final static Integer ONE = 1;

    public final static Integer SIX = 6;
	/**********************************************************/

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
	public final static Integer INVITE_CODE_STATUS_SUCCESSED = 1;
	/**邀请码状态 失效**/
	public final static Integer INVITE_CODE_STATUS_EXPIRED = 0;
	/**********************************************************/


	/***************************商品是否购买************************/
	/**购买**/
	public final static Integer GOODS_SKU_PRICE_BUY_ENBLE = 1;
	/**不可购买**/
	public final static Integer GOODS_SKU_PRICE_BUY_DISABLE = 0;
	/**********************************************************/

	/***************************自动生成编号类型************************/
	/**商品编号**/
	public final static Integer GOODS_NUM_TYPE = 1;
	/**订单编号**/
	public final static Integer ORDER_NUM_TYPE = 2;
	/******************************************************************/


	/***********************商品及规格上下架状态**************************/
	/**上架状态**/
	public final static Integer RELEASE_STATUS_ON = 1;
	/**下架状态**/
	public final static Integer RELEASE_STATUS_OFF = 0;
	/******************************************************************/

	/**************************图片存储目录*****************************/
	/**图片存储总目录**/
	public final static String PIC_DIR = "/upload";
	/**商品主图存储目录**/
	public final static String GOODS_MAIN_PIC = "/upload/goods";
	/**商品详情图片存储目录**/
	public final static String GOODS_DETAIL_PIC = "/upload/detail";
	/******************************************************************/

	/****************************分页*****************************/
	/**默认从第1条开始**/
	public final static Integer DEFAULT_PAGE_START = 1;
	/**默认返回10条**/
	public final static Integer DEFAULT_PAGE_STEP = 10;
	/*************************************************************/
}
