package com.qianmo.eshop.config;

/**
 * Created by zhangyang on 2016/3/16 0016.
 */
public enum ValidateRule {
    userId("required,num,maxlength=18,minlength=1");

    private String checkRegx;

    private ValidateRule(String value) {
        checkRegx = value;
    }

    public String getCheckRegx() {
        return checkRegx;
    }
}

  /*  *//**
 * 根据key去获取需要check的规则
 *//*
    public  String[] getCheckRegx(String key) {
        String[] checkRegxs = null;
        for(ValidateRule temp : ValidateRule.values()) {
            if (key.equals(temp.name())) {
                checkRegxs = temp.getCheckRegx().split(",");
                break;
            }
        }
        return checkRegxs;
    }

    public static void main(String[] args) {
        ValidateRule rule = new ValidateRule();
        String[] temp =  rule.getCheckRegx("userId");
        for(String temptemp : temp) {
            System.out.println(temptemp);
        }
    }
}*/
