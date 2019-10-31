package com.unity.common.constant;

/**
 * * 功能描述: <br> 常量定义
 *
 * @author zhqgeng
 * @create 2019-07-02 9:57
 */
public class InnovationConstant {



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
     * 发改局 单位主键 ipl_darb_main
     */
    public final static Long DEPARTMENT_DARB_ID = 10L;

    /**
     * 企业发展服务局 单位主键 ipl_esb_main
     */
    public final static Long DEPARTMENT_ESB_ID = 12L;

    /**
     * 科技局 单位主键 ipl_satb_main
     */
    public final static Long DEPARTMENT_SATB_ID = 13L;

    /**
     * 组织部 单位主键 ipl_od_main
     */
    public final static Long DEPARTMENT_OD_ID = 2L;

    /**
     * 工位宣传部 单位主键 ipl_pd_main
     */
    public final static Long DEPARTMENT_PD_ID = 3L;
    /**
     * 亦庄国投 单位主键 ipl_pd_main
     */
    public final static String DEPARTMENT_YZGT = "亦庄国投";

    /**
     * 纪检 单位主键 ipl_suggestion_main
     */
    public final static Long DEPARTMENT_SUGGESTION_ID = 8L;

    /**
     * 亦庄国投 单位主键 ipl_Yzgt_main
     */
    public final static Long DEPARTMENT_YZGT_ID = 43L;

    /**
     * 换行符
     */
    public final static String LINE_SEPARATOR = "line.separator";

    /**
     * 小时
     */
    public final static Long HOUR = 1000*60*60L;

    /**
     * 天
     */
    public final static Long DAY = 1000*60*60*24L;
}
