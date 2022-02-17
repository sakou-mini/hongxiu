package com.donglai.common.constant;

/**
 * @author yty
 * @version 1.0
 * @date 2020/3/9 10:03
 * @description:
 */
public class EmailPhoneConstant {
    /*手机验证码=======================*/
    /*1.发送短信的间隔时间（毫秒）*/
    public static final long interval = 60000;
    /*2.手机号码正则表达式*/
    public static final String REGEX_MOBILE = "((\\+86|0086)?\\s*)((134[0-8]\\d{7})|(((13([0-3]|[5-9]))|" +
            "(14[5-9])|15([0-3]|[5-9])|(16(2|[5-7]))|" + "17([0-3]|[5-8])|18[0-9]|19(1|[8-9]))\\d{8})|(14(0|" +
            "1|4)0\\d{7})|(1740([0-5]|[6-9]|[10-12])\\d{7}))";
    //3.验证码存活时间(5分钟)
    public static final long codeOverdueTime = 300000;

}
