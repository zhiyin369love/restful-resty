package com.qianmo.eshop.common;

import com.qianmo.eshop.model.user.sequence;

import java.text.DecimalFormat;

/**
 * Created by fxg06 on 2016/3/3.
 */

public class CodeUtils {

    /**
     * 自动生成订单号或者商品货号
     *
     * @param codeNum 订单号或货号的前缀
     * @param type    1商品编号 2订单编号
     * @return
     */
    public static String code(String codeNum, int type) {
        String num;
        DecimalFormat df;

        if (type == ConstantsUtils.GOODS_NUM_TYPE) {
            df = new DecimalFormat("000000000000");  //商品编号
        } else {
            df = new DecimalFormat("0000");  //订单编号
        }

        sequence model = sequence.dao.findById(type);
        long code;
        code = Long.parseLong(model.get("code").toString()) + 1;
        if ((type == ConstantsUtils.GOODS_NUM_TYPE && code > 999999999999l)
                || (type == ConstantsUtils.ORDER_NUM_TYPE && code > 9999)) {
            code = 0;
        }
        code = Long.parseLong(df.format(code));
        model.set("code", code);
        model.update();
        num = codeNum + df.format(code);
        return num;
    }


}
