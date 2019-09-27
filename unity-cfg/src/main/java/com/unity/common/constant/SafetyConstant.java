package com.unity.common.constant;

/**
 * * 功能描述: <br> 常量定义
 *
 * @author zhqgeng
 * @create 2019-07-02 9:57
 */
public class SafetyConstant {

    public static final Short YSE_BUTTON = 1;//显示按钮
    public static final Short NO_BUTTON = 0;//不显示按钮
    public static final Integer YSE_STATUE = 1;//状态 是
    public static final Integer NO_STATUE = 0;//状态 否
    public static final Integer STATUE_ZERO = 0;//状态0 全部浏览
    public static final Integer STATUE_ONE = 1;//状态1  只显示已浏览
    public static final Integer STATUE_TWO = 2;//状态2  只显示未浏览

    public static final String SUCCESS = "操作成功";

    /**
     * 通知公告的通告名称(title)的最大长度
     */
    public static final String PROMPT = "PROMPT";

    /**
     * 通知公告的通告名称(title)的最大长度
     */
    public static final int INTERNET_NOTICE_TITLE_MAX_LENGTH = 100;

    /**
     * 安全教育培训导出excel的行数
     */
    public static final int SAFETY_PRODUCE_EXCEL_ROW = 6;

    /**
     * 安全教育培训导出excel的列数
     */
    public static final int SAFETY_PRODUCE_EXCEL_COL = 4;

    /**
     * hashmap默认长度
     */
    public static final int HASHMAP_DEFAULT_LENGTH = 16;

    /**
     * excel导入时的最大长度
     */
    public static final int EXCEL_IMPORT_MAX_LENGTH = 1000;

    /**
     * 工作动态的title最大长度
     */
    public static final int WORK_STATUS_TITLE_MAX_LENGTH = 100;

    /**
     * 基层项目类别管理title的最大长度
     */
    public static final int PROJECT_CATEGORY_TITLE_MAX_LENGTH = 20;

    /**
     * 前后台日期时间交互格式
     */
    public static final String YMD_DATE_PATTERN = "yyyy-MM-dd 00:00:00";

    /**
     * 应急值守值班日期格式化
     */
    public static final String YMD_DATE = "yyyy-MM-dd";

    /**
     * 集团安全部员工角色
     * */
    public static final  Long GROUP_SECURITY_ROLE = 32L;
    /**
     * 集团安全部领导角色
     * */
    public static final  Long GROUP_SECURITY_MANAGER_ROLE = 31L;
}
