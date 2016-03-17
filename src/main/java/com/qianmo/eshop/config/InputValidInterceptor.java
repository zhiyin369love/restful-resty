package com.qianmo.eshop.config;



import cn.dreampie.log.Logger;
import cn.dreampie.route.core.Params;
import cn.dreampie.route.core.RouteInvocation;
import cn.dreampie.route.interceptor.Interceptor;
import com.alibaba.druid.util.StringUtils;
import com.qianmo.eshop.common.ConstantsUtils.RETURN_CODE;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 暂时只考虑做com.qianmo.eshop.resource路径下的class的拦截
 * Created by zhangyang on 16/03/16
 */
public class InputValidInterceptor implements Interceptor {
    private static final Logger logger = Logger.getLogger(InputValidInterceptor.class);
    /**
     * 对输入参数进行拦截
     *
     * @param ri
     */
    public void intercept(RouteInvocation ri) {

        //类名
        ValidateResult result = new ValidateResult(RETURN_CODE.IS_OK, null);
        String className = ri.getResourceClass().getName();
        if (!className.startsWith("com.qianmo.eshop.resource")) {
            ri.invoke();
        } else {
            //输入参数
            Params params = ri.getRouteMatch().getParams();
            //如果不需要参数
            if (params == null) {
                ri.invoke();
            } else {
                try {
                    result = checkParams(ValidateRule.values(), params);
                    if (!RETURN_CODE.IS_OK.equals(result.getCode())) {
                        //ri.getRouteMatch().getResponse().setStatus(HttpStatus.OK);
                        result.setCode(RETURN_CODE.IS_OK);
                        ri.render(result);
                        //throw new InterceptorException(result.getMessage());
                    } else {
                        ri.invoke();
                    }
                } catch (Throwable t) {
                    logger.error(t.getCause().getMessage());
                    //如果统一验证有异常，那么需要继续调用方法
                    ri.invoke();
                   /* Throwable cause = t.getCause();

                    if (cause == null) {
                        cause = t;
                    }
                    if (cause instanceof WebException) {
                        throw (WebException) cause;
                    } else {
                        throw new InterceptorException(cause.getMessage(), cause);
                    }*/
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
    public ValidateResult checkParams(ValidateRule[] sources,
                                      Params params) {
        if (params == null || sources == null) {// 参数列表或请求参数为空，则默认校验通过
            return new ValidateResult(RETURN_CODE.IS_OK, null);
        }

        // 创建ValidateResult对象,作为校验出参，默认通过
        ValidateResult result = new ValidateResult(RETURN_CODE.IS_OK, null);
        for (String key : params.getNames()) {
            String regexs = "";
            //TODO 例外参数
            //需要判断是jsonObject还是list<JsonObject>如果是普通的可以用下面的逻辑
            Object value = params.get(key);
            if (value instanceof Map) {
                //框架在前期会把参数拼接好，map是<String,Object>结构的
                if(value != null) {
                    Map input = (Map) value;
                    Set<String> keySets = input.keySet();
                    //三层循环，如果是JSONObject，那么外层循环次数会非常少，三层循环也不会有大问题
                    for (String keyTemp : keySets) {
                        for (ValidateRule rule : sources) {
                            if (keyTemp.equals(rule.name())) {
                                regexs = rule.getCheckRegx();//获取参数对应的校验规则，如果不存在校验规则，则不去校验
                                break;
                            }
                        }
                        ValidateResult validateResult = getValidateResult(regexs, value, result, key);
                        //如果有校验失败，直接跳出循环
                        if (validateResult != null && validateResult.getCode() == RETURN_CODE.SYSTEM_ERROR) {
                            return validateResult;
                        }
                    }
                }
            } else if (value instanceof List) {
                List<Object> valueList = (List<Object>) value;
                if(valueList != null && valueList.size() >0) {
                   for(int i = 0; i < valueList.size(); i++) {
                       Map input = (Map) valueList.get(i);
                       Set<String> keySets = input.keySet();
                       //可以跟jsonObject合并，但实际上重复代码也就几行，
                       //三层循环，如果是list<Object>，那么外层循环次数会非常少，三层循环也不会有大问题
                       for (String keyTemp : keySets) {
                           for (ValidateRule rule : sources) {
                               if (keyTemp.equals(rule.name())) {
                                   regexs = rule.getCheckRegx();//获取参数对应的校验规则，如果不存在校验规则，则不去校验
                                   break;
                               }
                           }
                           ValidateResult validateResult = getValidateResult(regexs, value, result, key);
                           //如果有校验失败，直接跳出循环
                           if (validateResult != null && validateResult.getCode() == RETURN_CODE.SYSTEM_ERROR) {
                               return validateResult;
                           }
                       }
                   }
                }
            } else {
                for (ValidateRule rule : sources) {
                    if (key.equals(rule.name())) {
                        regexs = rule.getCheckRegx();//获取参数对应的校验规则，如果不存在校验规则，则不去校验
                        break;
                    }
                }
                ValidateResult validateResult = getValidateResult(regexs, value, result, key);
                //如果有校验失败，直接跳出循环
                if (validateResult != null && validateResult.getCode() == RETURN_CODE.SYSTEM_ERROR) {
                    return validateResult;
                }
            }
        }
        return result;

    }

    private ValidateResult getValidateResult(String regexs, Object valueObject, ValidateResult result, String key) {
        //
        List<ValidateType> validateTypes = getValidateTypes(regexs);
        String value;
        for (ValidateType validateType : validateTypes) {
            if (valueObject == null || StringUtils.isEmpty(valueObject.toString())) {
                // 如果传入为空，校验值为required，则返回失败
                if (validateType == ValidateType.required) {
                    return new ValidateResult(RETURN_CODE.SYSTEM_ERROR, getText(validateType.getErrMsg(), key));
                }
            }
            value = valueObject.toString();
            try {
                getValidResult(result, key, value, validateType);
                if (!RETURN_CODE.IS_OK.equals(result.getCode())) {// 校验失败，返回
                    return result;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                //System.err.println(e.getMessage());
            }
        }
        return null;
    }

    private void getValidResult(ValidateResult result, String key, String value, ValidateType validateType) {
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
                if (!RETURN_CODE.IS_OK.equals(result.getCode())) {
                    validateRegex(value, ValidateType.REGEX.IDCARD_18, validateType, key, result);
                }
                break;
            case regex:// 正则表达式校验
                validateRegex(value, validateType.getRegVal(), validateType, key, result);
                break;
            default:// 其他正则校验(数字校验,时间校验,邮箱校验,手机号校验,url校验,ip地址校验,国内邮编校验)
                validateRegex(value, validateType.getRegexs(), validateType, key, result);
                break;
        }
    }

    /**
     * 正则表达式校验
     *
     * @param regex 正则表达式
     * @param value 被校验的值
     * @return 校验结果
     */
    private boolean matchRegex(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * 获取待校验的列表
     *
     * @param regexs 配置的校验列表
     * @return 规则列表
     */
    private List<ValidateType> getValidateTypes(String regexs) {
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
                logger.error(e.getMessage());
                //System.err.println(e.getMessage());
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
     * @param value        前台输入的值
     * @param key          前台输入的参数名
     * @param validateType 校验规则
     * @param result       校验返回值
     */
    private void isRequired(String value, ValidateType validateType,
                            String key, ValidateResult result) {
        if (value == null || "".equals(value.trim())) {// 校验失败,封装返回结果
            result.setCode(RETURN_CODE.SYSTEM_ERROR);
            result.setMessage(getText(validateType.getErrMsg(), key));
        }
    }

    /**
     * 校验是否超过最大值
     */
    private void validateLength(String value, String regVal,
                                ValidateType validateType, String key, ValidateResult result) {
        try {
            long length = Long.parseLong(regVal);

            if ((value.length() != length)) {
                result.setCode(RETURN_CODE.SYSTEM_ERROR);
                result.setMessage(getText(validateType.getErrMsg(), key, String.valueOf(length)));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            //System.err.println(getText(WARNING_MSG.VALIDATE_LENGTH, e.getMessage()));
        }
    }

    /**
     * 校验是否超过最大值
     */
    private void validateMaxlength(String value, String regVal,
                                   ValidateType validateType, String key, ValidateResult result) {
        try {
            long length = Long.parseLong(regVal);

            if ((value.length() > length)) {
                result.setCode(RETURN_CODE.SYSTEM_ERROR);
                result.setMessage(getText(validateType.getErrMsg(), key, String.valueOf(length)));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            //System.err.println("VALIDATE MAX LENGTH ERROR ! THE ERROR IS " + e.getMessage() + "!");
        }
    }

    /**
     * 校验是否小于最小值
     */
    private void validateMinlength(String value, String regVal,
                                   ValidateType validateType, String key, ValidateResult result) {
        try {
            long length = Long.parseLong(regVal);
            if ((value.length() < length)) {
                result.setCode(RETURN_CODE.SYSTEM_ERROR);
                result.setMessage(getText(validateType.getErrMsg(), key, String.valueOf(length)));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            //System.err.println("VALIDATE MIN ERROR ! THE ERROR IS " + e.getMessage() + "!");
        }
    }

    /**
     * 校验是否属于某个区间
     */
    private void validateRange(String val, String scope,
                               ValidateType validateType, String key, ValidateResult result) {
        final String regex = "^\\s*(\\(|\\[)\\s*((-?\\d+)(\\.\\d+)?)+\\s*~\\s*((-?\\d+)(\\.\\d+)?)+\\s*(\\)|\\])\\s*$";
        if (!matchRegex(scope, regex)) {// 校验配置的区间值格式是否正确
            logger.error("RANGE CONFIGURATION ERROR!! THE WRONG RANGE IS='" + scope + "'!!");
            //System.err.println("RANGE CONFIGURATION ERROR!! THE WRONG RANGE IS='" + scope + "'!!");
        }
        scope = scope.replaceAll(" ", "");//去空格
        // 取出配置的字段
        String[] scopes = scope.split("~");
        double value = Double.parseDouble(val);
        double min = Double.parseDouble(scopes[0].substring(1));
        double max = Double.parseDouble(scopes[1].substring(0, scopes[1].length() - 1));
        if (min > max) {//配置错误
            logger.error("RANGE CONFIGURATION ERROR!! THE WRONG RANGE IS='" + scope + "'!! MAXVALUE MUST GREATER THEN MINVALUE!!");
            //System.err.println("RANGE CONFIGURATION ERROR!! THE WRONG RANGE IS='" + scope + "'!! MAXVALUE MUST GREATER THEN MINVALUE!!");
        }
        String firstBrackets = String.valueOf(scopes[0].charAt(0));

        boolean validateFlag = true;// 校验标识，默认通过
        if ("(".equals(firstBrackets)) {// 和小数校验
            if (value <= min) {
                validateFlag = Boolean.FALSE;
                logger.error("输入的值必须大于最小值！");
            }
        } else if ("[".equals(firstBrackets)) {
            if (value < min) {
                validateFlag = Boolean.FALSE;
                logger.error("输入的值必须大于等于最小值！");
            }
        }

        String lastBrackets = String.valueOf(scopes[1].charAt(scopes[1].length() - 1));
        if (")".equals(lastBrackets)) {// 和大数校验
            if (value >= max) {
                validateFlag = Boolean.FALSE;
                logger.error("输入的值必须小于最大值！");
            }
        } else if ("]".equals(lastBrackets)) {
            if (value > max) {
                validateFlag = Boolean.FALSE;
                logger.error("输入的值必须小于等于最大值！");
            }
        }

        if (!validateFlag) {// 校验失败,封装返回结果
            result.setCode(RETURN_CODE.SYSTEM_ERROR);
            result.setMessage(getText(validateType.getErrMsg(), key, scope));
        }
    }

    /**
     * 根据规则验证传入的值是否正确(如果传入值为空，则不校验)
     *
     * @param value        传入值
     * @param regex        正则表达式
     * @param validateType 校验类型
     * @param key          前台输入的参数名
     * @return 验证结果
     */
    private void validateRegex(String value, String regex,
                               ValidateType validateType, String key, ValidateResult result) {
        if (value == null || "".equals(value.trim())) {
            return;
        }
        if (!matchRegex(value, regex)) {// 校验失败,封装返回结果
            result.setCode(RETURN_CODE.SYSTEM_ERROR);
            result.setMessage(getText(validateType.getErrMsg(), key));
        }
    }

    /**
     * 拼接返回内容
     */
    private String getText(String localeKey, String key) {
        return key + localeKey;
    }

    /**
     * 拼接返回内容
     */
    private String getText(String localeKey, String key, String params) {
        return key + localeKey + params;
    }
}

