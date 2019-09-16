package com.unity.me.util;

/**
 * <p>
 * Created by zhaozesheng on 2019-03-05 18:14:30
 */
public class MessageConstants {
    /**
     * 手机号正则验证
     */
    public static final String MOBILE_CHECKED="^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";

    /**
     * 邮箱正则
     */
    public static final String EMAIL_CHECKED ="^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    public static final String ALIAS_TYPE="JPUSH_ALIAS";

    /**
     * 验证码条件未过期条件 9分钟（单位:毫秒）
     */
    public static final long FREQUEN_CONDITION=9 * 60 * 1000;

    /**
     * 验证码超时时间 10分钟 （单位:毫秒）
     */
    public static final long CODE_OVERTIME = 10 * 60 * 1000;
}
