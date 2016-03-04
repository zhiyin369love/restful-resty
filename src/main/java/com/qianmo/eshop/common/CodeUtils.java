package com.qianmo.eshop.common;

import com.qianmo.eshop.model.user.sequence;

import java.text.DecimalFormat;

/**
 * Created by fxg06 on 2016/3/3.
 */

public class CodeUtils{

    public static String code(String codeNum, int type){
        String num ;
        DecimalFormat df;
        if(type==1){
            df = new DecimalFormat("000000000000");
        }else{
            df = new DecimalFormat("0000");
        }
        sequence model = sequence.dao.findById(type);
        long code = 0;
        code = Long.parseLong(model.get("code").toString())+1;
        if((type==1 && code>999999999999l) || (type==2 && code>9999)){
            code = 0;
        }
        code = Long.parseLong(df.format(code));
        model.set("code",code);
        model.update();
        num = codeNum + df.format(code);
        return num;
    }
}
