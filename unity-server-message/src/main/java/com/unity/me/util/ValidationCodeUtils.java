package com.unity.me.util;

import java.util.Random;

/**
 * 生成随机数
 *<p>
 *create by zhangxiaogang at 2018/7/31 10:40
 */
public class ValidationCodeUtils {


    /**
     *获取4位随机验证码
     *@return 4位随机验证码
     *@author zhangxiaogang
     *@since 2018/7/31 10:41
     */
    public static String get4ValidationCode(){
        return String.valueOf((new Random().nextInt(8999) + 1000));
    }


    /**
     *获取6位随机验证码
     *@return 6位随机验证码
     *@author zhangxiaogang
     *@since 2018/7/31 10:41
     */
    public static String getSixValidationCode(){
        return String.valueOf((new Random().nextInt(899999) + 100000));
    }



}
