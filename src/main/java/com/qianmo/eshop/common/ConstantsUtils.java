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
	public final static Integer PHYSICAL_UNIT = 1;
	/**包装单位**/
	public final static Integer PACKAGE_UNIT = 2;
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
	/**支付状态-待确认**/
	public final static Integer ORDER_PAYMENT_STATUS_WAITE_TRUE = 2;
	/**支付状态-已支付**/
	public final static Integer ORDER_PAYMENT_STATUS_RECEIVED = 3;      //此项值改为3，原为2
	/**********************************************************/

	/**************************订单状态**************************/
	/**订单状态-已取消**/
	public final static Integer ORDER_INFO_STATUS_CANCEL = 0;
	/**订单状态-已创建**/
	public final static Integer ORDER_INFO_STATUS_CREATED = 1;
	/**订单状态-待收货/待发货**/
	public final static Integer ORDER_INFO_STATUS_WAIT_RECEIVE = 2;
	/**订单状态-已发货发货**/
	public final static Integer ORDER_INFO_STATUS_ALREADY = 3;
	/**订单状态-已完成**/
	public final static Integer ORDER_INFO_STATUS_FINISHED = 4;
	/**********************************************************/

  /********************************按时间段筛选订单***********************************/
	   //(1:表示今天，7表示近7天，30:近30天)
    public final static Integer ORDER_TIME_TODAY = 1;
	public final static Integer ORDER_TIME_F= 7;
	public final static Integer ORDER_TIME_s= 30;
/********************************************************/

   /***********************赊账状态****************************/
   public final static Integer CREDIT_STATUS = 0;  //代表未销账
	/**********************************************************/
	/**************************验证码/邀请码类型**************************/
	/**注册**/
	public final static Integer INVITE_VERIFY_CODE_TYPE_REGISTER = 1;
	/**重置密码**/
	public final static Integer INVITE_VERIFY_CODE_TYPE_RESET = 2;
	/**邀请**/
	public final static Integer INVITE_VERIFY_CODE_TYPE_INVITE = 3;
	/**发送短信**/
	public final static Integer INVITE_VERIFY_CODE_TYPE_SEND_SMS = 4;
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

	/**************************买家操作订单标识**************************/
	/**选择支付方式**/
	public final static int ORDER_OP_PAY_TYPE = 0;
	/**选择银行**/
	public final static int ORDER_OP_BANK = 1;
	/**我已付款**/
	public final static int ORDER_OP_PAY_STATUS = 2;
	/**确认收货**/
	public final static int ORDER_OP_PAY_GOODS = 3;
	/**取消订单**/
	public final static int ORDER_OP_PAY_CELL = 4;
	/**再买一次**/
	public final static int ORDER_OP_BUYER_AGAIN = 5;
	/**********************************************************/

	/**************************卖家操作订单标识**************************/
	/**0收到货款**/
	public final static Integer SELLER_ORDER_OP_PAY_TYPE = 0;
	/**1发货**/
	public final static Integer SELLER_ORDER_OP_FAHUO = 1;
	/**2取消**/
	public final static Integer SELLER_ORDER_OP_PAY_STATUS = 2;
	/**3卖家备注订单**/
	public final static Integer SELLER_ORDER_OP_PAY_GOODS = 3;
	/**4同意买家赊账**/
	public final static Integer SELLER_ORDER_OP_PAY_CELL = 4;
	/**5不同意买家赊账**/
	public final static Integer SELLER_ORDER_OP_BUYER_AGAIN = 5;
	/**6同意买家货到付款**/
	public  final  static  Integer SELLER_ORDER_OP_BUYER_CASH_AGREE = 6;
	/**7不同意买家货到付款**/
	public  final  static  Integer SELLER_ORDER_OP_BUYER_CASH_DISAGREE= 7;
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
	public final static String GOODS_MAIN_PIC = "/upload/goods/";
	/**商品详情图片存储目录**/
	public final static String GOODS_DETAIL_PIC = "/upload/detail/";
	/**用户身份证他营业执照图片存储目录**/
	public final static String USER_PIC = "/upload/user/";
	/******************************************************************/

	/****************************分页*****************************/
	/**默认从第1条开始**/
	public final static Integer DEFAULT_PAGE_START = 1;
	/**默认返回10条**/
	public final static Integer DEFAULT_PAGE_STEP = 10;
	/*************************************************************/

	/****************************排序*****************************/
	/**新品**/
	public final static Integer SORT_NEW = 1;
	/**价格**/
	public final static Integer SORT_PRICE = 1;
	/**降序**/
	public final static Integer SORT_DESC = 1;
	/**升序**/
	public final static Integer SORT_ASC = 2;
	/*************************************************************/

	/**************************支付方式名称**************************/
	/**支付方式名称 **/
	/**	1：银行汇款 2：货到付款 3：赊账 4：在线支付 **/
	public final static Integer PAY_TYPE_INT_DEFAULT = -1;
	public final static Integer PAY_TYPE_INT_BANK = 1;
	public final static Integer PAY_TYPE_INT_OFFLINE = 2;
	public final static Integer PAY_TYPE_INT_CREDIT = 3;
	public final static Integer PAY_TYPE_INT_ONLINE = 4;
	/**********************************************************/
	/**************************HTTP STATUS**************************/
	/**http的状态**/
	public final static Integer HTTP_STATUS_OK_200 = 200;
	/**********************************************************/

   /**************************统一校验用**************************/
	/** 请求唯一标识 **/
	public static final String TOKEN = "token";

	/** PARAM_ARRAY 暂不详 **/
	public static final String[] PARAM_ARRAY = { TOKEN };


	/** 系统returnCode编码 **/
	public interface RETURN_CODE {
		String IS_OK = "200";// 正常
		String SYSTEM_ERROR = "-9999";// 错误
	}
	/**********************************************************/
}
