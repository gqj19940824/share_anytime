package com.unity.common.constant;

/**
 * rbac Redis Keys 常量池
 * <p>
 * create by gengjiajia at 2019/01/23 16:22
 * @author gengjiajia
 */
public class RedisConstants {

    /**token redis -> 起始头*/
    public static final String LOGINNAME2TOKEN = "LOGINNAME2TOKEN";

    // 字典
    public static final String DIC_PREFIX = "DIC:DIC:";

    // 字点表
    public static final String DICGROUP = "DIC:GROUP";

    /**redis key连接符*/
    public static final String KEY_JOINER = ":";

    /**资源树 redis -> key*/
    public static final String RESOURCE_TREE = "TREE:resource_tree";

    /**用户信息map集合 redis -> 起始头*/
    public static final String USER = "User:";

    /**公司信息map集合 redis -> 起始头*/
    public static final String DEPARTMENT = "Department:";

    /**项目信息map集合 redis -> 起始头*/
    public static final String PROJECT = "Project:";

    /**天气预报 redis -> 起始头*/
    public static final String WEATHERFORECAST = "WeatherForecast:weatherForecast";

    /**
     * 单位排序id
     * */
    public static final String DEPARTMENT_ORDER_LIST ="department:order";

    /**组织单位 级别*/
    public static final String LEVEL ="level";

    /**名称*/
    public static final String NAME ="name";

    /**单位级次编码*/
    public static final String GRADATIONCODE="gradationCode";

    /**单位id*/
    public static final String ID_RBAC_DEPARTMENT="idRbacDepartment";



}
