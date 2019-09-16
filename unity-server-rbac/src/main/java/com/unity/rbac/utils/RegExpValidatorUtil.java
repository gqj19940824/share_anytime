package com.unity.rbac.utils;

import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.EncryptUtil;
import com.unity.common.util.Encryption;
import com.unity.rbac.constants.UserConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则校验
 * <p>
 * <p>
 * create by gengjiajia at 2019/03/06 09:48
 * @author gengjiajia
 */
public class RegExpValidatorUtil {

    /**
     * 账号正则校验规则
     */
    private static final String LOGIN_NAME_ALL_REG = "^(?!([a-zA-Z]+|\\d+)$)[a-zA-Z\\d]{2,20}$";
    private static final String LOGIN_NAME_ABC_REG = "^[a-zA-Z]{2,20}$";
    private static final String LOGIN_NAME_NUM_REG = "^([1-9][0-9]*)$";

    /**
     * 纯中文正则校验规则
     */
    private static final String NAME_REG = "^[\\u4e00-\\u9fa5]{2,20}$";

    /**
     * 纯中文正则校验规则
     */
    public static final String ROLE_NAME_REG = "^[\\u4e00-\\u9fa5]{1,20}$";

    /**
     * 密码正则校验规则
     */
    private static final String PWD_REG = "^(?!([a-zA-Z]+|\\d+)$)[a-zA-Z\\d]{6,12}$";

    /**
     * 手机号正则校验规则
     */
    private static final String PHONE_REG = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";

    /**
     * 手机号 11位数字 以1开头 校验规则
     */
    private static final String PHONE_NUM_REG = "^1\\d{10}$";

    /**
     * 电话正则校验规则
     */
    private static final String TEL_PHONE_REG = "^(\\(\\d{3,4}\\)|\\d{3,4}|\\s)?\\d{7,11}$";

    private static final int login_Name_size = 20;

    private static Pattern p = Pattern.compile("[\u4e00-\u9fa5]");


    /**
     * 校验账号
     *
     * @param loginName 账号
     * @return 符合 正则返回true, 否则返回 false;
     * @author gengjiajia
     * @since 2019/03/06 09:53  
     */
    public static boolean checkLoginName(String loginName){
        if(loginName == null){
            return false;
        }
        loginName = loginName.replaceAll(" ","");
        if(loginName.length() > UserConstants.USER_NAME_COMPANY_POSITION_MAX_LENGTH || loginName.length() < UserConstants.USER_NAME_COMPANY_POSITION_MIN_LENGTH){
           return false;
        }
        return match(LOGIN_NAME_ALL_REG,loginName) || match(LOGIN_NAME_ABC_REG,loginName) || match(LOGIN_NAME_NUM_REG,loginName);
    }

    /**
     * 校验项目账号
     *
     * @param  loginName 账号
     * @return 不符：false 符合：true
     * @author gengjiajia
     * @since 2019/08/13 19:39
     */
    public static boolean checkProjectLoginName(String loginName){
        if(StringUtils.isBlank(loginName)
                || loginName.length() > UserConstants.PJ_AD_LOGIN_NAME_MAX_LENGTH
                || !loginName.contains(UserConstants.PROJECT_LOGIN_NAME_PREFIX)
                || StringUtils.isBlank(loginName.replaceAll(UserConstants.PROJECT_LOGIN_NAME_PREFIX,""))){
            return false;
        }
        //去掉前缀后判断
        String newLoginName = loginName.replace(UserConstants.PROJECT_LOGIN_NAME_PREFIX, "");
        if(isContainChinese(newLoginName) || isContainSymbol(newLoginName)){
            //包含中文 或 包含标点符号
            return false;
        }
        return true;
    }

    /**
     * 校验管理员账号
     *
     * @param  loginName 账号
     * @return 不符：false 符合：true
     * @author gengjiajia
     * @since 2019/08/13 19:39
     */
    public static boolean checkAdminLoginName(String loginName){
        if(StringUtils.isBlank(loginName)
                || loginName.length() > UserConstants.PJ_AD_LOGIN_NAME_MAX_LENGTH
                || !loginName.contains(UserConstants.ADMIN_LOGIN_NAME_PREFIX)
                || StringUtils.isBlank(loginName.replaceAll(UserConstants.ADMIN_LOGIN_NAME_PREFIX,""))){
            return false;
        }
        //去掉前缀后判断
        String newLoginName = loginName.replace(UserConstants.ADMIN_LOGIN_NAME_PREFIX, "");
        if(isContainChinese(newLoginName) || isContainSymbol(newLoginName)){
            //包含中文 或 包含标点符号
            return false;
        }
        return true;
    }


    /**
     * 校验手机号
     *
     * @param phone 手机号  目前只校验  11位数字 以1开头
     * @return 符合 正则返回true, 否则返回 false;
     * @author gengjiajia
     * @since 2019/05/16 19:58
     */
    public static boolean checkPhone(String phone){
        return match(PHONE_NUM_REG,phone);
    }

    /**
     * 校验电话号
     *
     * @param phone 电话号
     * @return 符合 正则返回true, 否则返回 false;
     * @author gengjiajia
     * @since 2019/05/16 19:58
     */
    public static boolean checkTelephone(String phone){
        return match(TEL_PHONE_REG,phone);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    public static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 校验用户名称
     *
     * @param name 用户名称
     * @return 符合 正则返回true, 否则返回 false;
     * @author gengjiajia
     * @since 2019/03/06 09:53
     */
    public static boolean checkName(String name){
       return StringUtils.isNotBlank(name) && match(NAME_REG,name);
    }


    /**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文 true 表示有中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 该函数判断一个字符串是否包含标点符号（中文英文标点符号）。
     * 原理是原字符串做一次清洗，清洗掉所有标点符号。
     * 此时，如果原字符串包含标点符号，那么清洗后的长度和原字符串长度不同。返回true。
     * 如果原字符串未包含标点符号，则清洗后长度不变。返回false。
     * @param s
     * @return
     */
    public static boolean isContainSymbol(String s) {
        String tmp = s.replaceAll("\\p{P}", "");
        return s.length() != tmp.length();
    }

    public static void main(String[] args) {
        System.out.println(checkPhone("123ss456789"));
    }
}
