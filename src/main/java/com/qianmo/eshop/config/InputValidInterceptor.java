package com.qianmo.eshop.config;



import cn.dreampie.common.http.exception.WebException;
import cn.dreampie.route.core.Params;
import cn.dreampie.route.core.RouteInvocation;
import cn.dreampie.route.interceptor.Interceptor;
import cn.dreampie.route.interceptor.exception.InterceptorException;
import com.alibaba.druid.util.StringUtils;
import com.qianmo.eshop.common.ConstantsUtils.RETURN_CODE;
import com.qianmo.eshop.config.ValidateResult.WARNING_MSG;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 暂时只考虑做com.qianmo.eshop.resource路径下的class的拦截
 * Created by zhangyang on 16/03/16
 */
public class InputValidInterceptor implements Interceptor {

    /**
     *对输入参数进行拦截
     * @param ri
     */
    public void intercept(RouteInvocation ri) {

        //类名
        ValidateResult result;
        String className = ri.getResourceClass().getName();
        if(!className.startsWith("com.qianmo.eshop.resource")) {
            ri.invoke();
        } else {
            //输入参数
            Params params = ri.getRouteMatch().getParams();
            //如果不需要参数
            if(params == null) {
                ri.invoke();
            } else {
                try {
                    result = checkParams(ValidateRule.values(),params);
                    if (!RETURN_CODE.IS_OK.equals(result.getReturnCode())) {
                        throw new InterceptorException(result.getReturnMessage());
                    } else {
                        ri.invoke();
                    }
                } catch (Throwable t) {
                    Throwable cause = t.getCause();

                    if (cause == null) {
                        cause = t;
                    }
                    if (cause instanceof WebException) {
                        throw (WebException) cause;
                    } else {
                        throw new InterceptorException(cause.getMessage(), cause);
                    }
                }
            }
        }
    }



    /**
     * 验证请求的参数个数、顺序、和正则。
     *
     * @param sources 配置的校验规则
     * @param params  前台传入的参数
     * @return
     */
    public static ValidateResult checkParams(ValidateRule[] sources ,
                                             Params params) {
        if (params == null || sources == null) {// 参数列表或请求参数为空，则默认校验通过
            return new ValidateResult(RETURN_CODE.IS_OK, null);
        }

        // 去除params中的特定参数 TODO
       /* for (String key : ControlConstants.PARAM_ARRAY) {
            if (params.containsName(key)) {
                params.remove(key);
            }
        }*/

        ValidateResult result = new ValidateResult(RETURN_CODE.IS_OK, null);// 创建ValidateResult对象,作为校验出参，默认通过
        // 按配置规则进行校验
        for(ValidateRule parameter : sources) {
            if (parameter == null || StringUtils.isEmpty(parameter.getCheckRegx())) {
                continue;// 如果Parameter为空，或者regex为空，则不校验
            }
            String regexs = parameter.getCheckRegx();// 获取配置的校验规则
            String key = parameter.name();
            Object valueObject = params.get(key);//获取前台请求的值
            List<ValidateType> validateTypes = getValidateTypes(key, regexs);
            String value ;
            for (ValidateType validateType : validateTypes) {
                if (valueObject == null) {
                    // 如果传入为空，校验值为required，则返回失败
                    if (validateType == ValidateType.required) {
                        return new ValidateResult(RETURN_CODE.SYSTEM_ERROR, getText(validateType.getErrMsg(), key));
                    }else {
                        // 传入为空，校验值为非required，默认校验通过(校验值已排序，required顺序为第一个)
                        break;
                    }
                }
                 value = valueObject.toString();
                    try {
                        switch (validateType) {
                            case required:// 必填字段校验
                                isRequired(value, validateType, key, result);
                                break;
                            case length:// 长度校验
                                validateLength(value, validateType.getRegVal(), validateType, key, result);
                                break;
                            case maxlength:// 长度最大值校验
                                validateMaxlength(value, validateType.getRegVal(), validateType, key, result);
                                break;
                            case minlength:// 长度最小值校验
                                validateMinlength(value, validateType.getRegVal(), validateType, key, result);
                                break;
                            case range:// 区间校验
                                validateRange(value, validateType.getRegVal(), validateType, key, result);
                                break;
                            case idcard:// 证件号校验
                                // 校验规则：15位通过 || 18位通过
                                validateRegex(value, ValidateType.REGEX.IDCARD_15, validateType, key, result);
                                if (!RETURN_CODE.IS_OK.equals(result.getReturnCode())) {
                                    validateRegex(value, ValidateType.REGEX.IDCARD_18, validateType, key, result);
                                }
                                break;
                            case regex:// 正则表达式校验
                                validateRegex(value, validateType.getRegVal(), validateType, key, result);
                                break;
                            default:// 其他正则校验(数字校验,时间校验,邮箱校验,手机号校验,url校验,ip地址校验,国内邮编校验,正则表达式校验)
                                validateRegex(value, validateType.getRegexs(), validateType, key, result);
                                break;
                        }
                        if (!RETURN_CODE.IS_OK.equals(result.getReturnCode())) {// 校验失败，返回
                            return result;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
        }
        return result;

    }

    /**
     * 正则表达式校验
     *
     * @param regex
     *            正则表达式
     * @param value
     *            被校验的值
     * @return 校验结果
     */
    private static boolean matchRegex(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * 获取待校验的列表
     *
     * @param key 配置的Key值,只供显示错误用
     * @param regexs 配置的校验列表
     * @return 规则列表
     */
    public static List<ValidateType> getValidateTypes(String key, String regexs) {
        List<ValidateType> typeList = new ArrayList<ValidateType>();

        // 配置为空,则不校验
        if (regexs == null || regexs.trim().length() == 0) {
            return typeList;
        }
        String[] regexsArr = regexs.split(",");
        // 数组为空，则不校验
        if (regexsArr.length == 0) {
            return typeList;
        }

        // 逐个获取校验规则,key值不区分大小写
        for (String regex : regexsArr) {
            String[] arr = regex.split("=");// 分开键值对
            try {
                ValidateType validateType = ValidateType.valueOf(arr[0].trim().toLowerCase());
                if (arr.length > 1) {// 取出配置的校验值
                    validateType.setRegVal(arr[1]);
                }
                typeList.add(validateType);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        // 按照校验先后顺序
        Collections.sort(typeList, new Comparator<ValidateType>() {
            public int compare(ValidateType v1, ValidateType v2) {
                return v1.getOrder() - v2.getOrder();
            }
        });
        return typeList;
    }

    /**
     * 判断是否非空,如果value为空,则处理返回值
     *
     * @param value 前台输入的值
     * @param key 前台输入的参数名
     * @param validateType 校验规则
     * @param result 校验返回值
     */
    private static void isRequired(String value, ValidateType validateType,
                                   String key, ValidateResult result) {
        if (value == null || "".equals(value.trim())) {// 校验失败,封装返回结果
            result.setReturnCode(RETURN_CODE.SYSTEM_ERROR);
            result.setReturnMessage(getText(validateType.getErrMsg(), key));
        }
    }

    /**
     * 校验是否超过最大值
     */
    private static void validateLength(String value, String regVal,
                                       ValidateType validateType, String key, ValidateResult result) {
        try {
            long length = Long.parseLong(regVal);

            if ((value.length() != length)) {
                result.setReturnCode(RETURN_CODE.SYSTEM_ERROR);
                result.setReturnMessage(getText(validateType.getErrMsg(), key, String.valueOf(length)));
            }
        } catch (Exception e) {
            System.err.println(getText(WARNING_MSG.VALIDATE_LENGTH, e.getMessage()));
        }
    }

    /**
     * 校验是否超过最大值
     */
    private static void validateMaxlength(String value, String regVal,
                                          ValidateType validateType, String key, ValidateResult result) {
        try {
            long length = Long.parseLong(regVal);

            if ((value.length() > length)) {
                result.setReturnCode(RETURN_CODE.SYSTEM_ERROR);
                result.setReturnMessage(getText(validateType.getErrMsg(), key, String.valueOf(length)));
            }
        } catch (Exception e) {
            System.err.println("VALIDATE MAX LENGTH ERROR ! THE ERROR IS "+e.getMessage()+"!");
        }
    }

    /**
     * 校验是否小于最小值
     */
    private static void validateMinlength(String value, String regVal,
                                          ValidateType validateType, String key, ValidateResult result) {
        try {
            long length = Long.parseLong(regVal);
            if ((value.length() < length)) {
                result.setReturnCode(RETURN_CODE.SYSTEM_ERROR);
                result.setReturnMessage(getText(validateType.getErrMsg(), key, String.valueOf(length)));
            }
        } catch (Exception e) {
            System.err.println("VALIDATE MIN ERROR ! THE ERROR IS "+e.getMessage()+"!");
        }
    }

    /**
     * 校验是否属于某个区间
     */
    private static void validateRange(String val, String scope,
                                      ValidateType validateType, String key, ValidateResult result) {
        final String regex = "^\\s*(\\(|\\[)\\s*((-?\\d+)(\\.\\d+)?)+\\s*~\\s*((-?\\d+)(\\.\\d+)?)+\\s*(\\)|\\])\\s*$";
        if (!matchRegex(scope, regex)) {// 校验配置的区间值格式是否正确
            System.err.println("RANGE CONFIGURATION ERROR!! THE WRONG RANGE IS='" + scope+"'!!");
        }
        scope = scope.replaceAll(" ", "");//去空格
        // 取出配置的字段
        String[] scopes = scope.split("~");
        double value = Double.parseDouble(val);
        double min = Double.parseDouble(scopes[0].substring(1));
        double max = Double.parseDouble(scopes[1].substring(0, scopes[1].length() - 1));
        if (min > max) {//配置错误
            System.err.println("RANGE CONFIGURATION ERROR!! THE WRONG RANGE IS='" + scope+"'!! MAXVALUE MUST GREATER THEN MINVALUE!!");
        }
        String firstBrackets = String.valueOf(scopes[0].charAt(0));

        boolean validateFlag = true;// 校验标识，默认通过
        if ("(".equals(firstBrackets)) {// 和小数校验
            if (value <= min) {
                validateFlag = Boolean.FALSE;
                System.err.println("输入的值必须大于最小值！");
            }
        } else if ("[".equals(firstBrackets)) {
            if (value < min){
                validateFlag = Boolean.FALSE;
                System.err.println("输入的值必须大于等于最小值！");
            }
        }

        String lastBrackets = String.valueOf(scopes[1].charAt(scopes[1].length() - 1));
        if (")".equals(lastBrackets) ) {// 和大数校验
            if (value >= max){
                validateFlag = Boolean.FALSE;
                System.err.println("输入的值必须小于最大值！");
            }
        } else if ("]".equals(lastBrackets)) {
            if (value > max){
                validateFlag = Boolean.FALSE;
                System.err.println("输入的值必须小于等于最大值！");
            }
        }

        if (!validateFlag) {// 校验失败,封装返回结果
            result.setReturnCode(RETURN_CODE.SYSTEM_ERROR);
            result.setReturnMessage(getText(validateType.getErrMsg(), key, scope));
        }
    }

    /**
     * 根据规则验证传入的值是否正确(如果传入值为空，则不校验)
     *
     * @param value 传入值
     * @param regex 正则表达式
     * @param validateType 校验类型
     * @param key 前台输入的参数名
     * @return 验证结果
     */
    private static void validateRegex(String value, String regex,
                                      ValidateType validateType,String key,ValidateResult result) {
        if (value == null || "".equals(value.trim())) {
            return;
        }
        if (!matchRegex(value, regex)) {// 校验失败,封装返回结果
            result.setReturnCode(RETURN_CODE.SYSTEM_ERROR);
            result.setReturnMessage(getText(validateType.getErrMsg(),key));
        }
    }

    /**
     *  拼接返回内容
     */
    private static String getText(String localeKey, String key) {
        return key + localeKey;
    }

    /**
     *  拼接返回内容
     */
    private static String getText(String localeKey, String key,String params) {
        return key + localeKey + params;
    }
}

