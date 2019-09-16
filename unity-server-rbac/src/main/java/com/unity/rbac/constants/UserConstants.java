package com.unity.rbac.constants;

/**
 * 用户 常量池
 * <p>
 * create by gengjiajia at 2019/01/23 16:22
 * @author gengjiajia
 */
public class UserConstants {
    /**重置密码*/
    public static final String RESET_PWD = "111111";
    /**手机号*/
    public static final String PHONE = "phone";
    /**名称*/
    public static final String NAME = "name";
    /**系统*/
    public static final String OS = "os";
    /**ID标识*/
    public static final String ID = "id";
    /**来源*/
    public static final String SOURCE = "source";
    /**用户中心组织名称*/
    public static final String DEPARTNAME = "departName";
    /**4位验证码*/
    public static final String VERIFY_CODE = "verifyCode";
    /**短信验证码*/
    public static final String VERIF_CODE = "verificationCode";
    /**短信验证码类型*/
    public static final String VERIF_TYPE = "messageType";
    /**单次批量插入最大数量*/
    public final static int MAX_BATCH_INSERT_NUM = 100;
    /**用户拥有的按钮资源权限编码*/
    public static final String BUTTON_CODE_LIST = "buttonCodeList";
    /**用户拥有的菜单资源权限编码*/
    public static final String MENU_CODE_LIST = "menuCodeList";
    /**左侧菜单栏 地址*/
    public static final String MENU_ID = "id";
    /**左侧菜单栏 地址*/
    public static final String MENU_ID_PARENT = "idParent";
    /**左侧菜单栏 地址*/
    public static final String MENU_PATH = "path";
    /**左侧菜单栏 icon*/
    public static final String MENU_ICONCLS = "iconCls";
    /**左侧菜单栏 菜单名称*/
    public static final String MENU_NAME = "name";
    /**左侧菜单栏 菜单页面路由*/
    public static final String MENU_COMPONENT = "component";
    /**左侧菜单栏 子级菜单*/
    public static final String MENU_CHILDREN = "children";
    /**左侧菜单栏 菜单上级节点值*/
    public static final Long MENU_PARENT_ID = 1L;
    /**用户 —> 名称 部门、职位    最大限制20字符*/
    public final static int USER_NAME_COMPANY_POSITION_MAX_LENGTH = 20;
    /**用户 —> 名称 部门、职位   最小 限制2字符*/
    public final static int USER_NAME_COMPANY_POSITION_MIN_LENGTH = 2;
    /**备注   限制300字符*/
    public final static int USER_NOTES_MAX_LENGTH = 255;
    /**用户注册默认身份id*/
    public static final Long DEFAULT_IDENTITY_ID = 1L;
    /**项目账号前缀*/
    public static final String PROJECT_LOGIN_NAME_PREFIX = "@PJ_";
    /**管理员账号前缀*/
    public static final String ADMIN_LOGIN_NAME_PREFIX = "@AD_";
    /**项目及管理员账号 最大限制20字符*/
    public final static int PJ_AD_LOGIN_NAME_MAX_LENGTH = 24;
}
