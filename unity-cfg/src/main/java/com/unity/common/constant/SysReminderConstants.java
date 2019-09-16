package com.unity.common.constant;

/**
 * 定时任务常量类
 * <p>
 * create by gengjiajia at 2019/07/30 17:45
 */
public class SysReminderConstants {

    /**
     * 系统提醒 -- 通知公告标题
     */
    public final static String INTERNET_NOTICE_TITLE = "[通知公告]【TITLE】距离反馈截止日期还有一天，【NAME】仍未反馈。";

    /**
     * 系统提醒 -- 通知公告标题 占位符
     */
    public final static String TITLE_KEY = "TITLE";


    /**
     * 系统提醒 -- 隐患整改标题
     */
    public final static String INVESTIGATION_CHANG_TITLE = "[隐患整改]【NO】安全生产检查整改通知单，【NAME】仍未进行处理";

    /**
     * 系统提醒 -- 隐患整改标题 占位符
     */
    public final static String NO_KEY = "NO";

    /**
     * 系统提醒 -- 应急值班标题
     */
    public final static String EMER_NAME_TITLE = "[应急值班]临近【NAME】，请各单位合理安排值班人员，并及时上报值班表。";

    /**
     * 系统提醒 -- 应急值班标题 占位符
     */
    public final static String NAME_KEY = "NAME";


    /**
     * 系统提醒 -- 特种设备管理标题前缀
     */
    public final static String SPEC_EQUIPMENT_PREFIX_TITLE = "[特种设备管理]";

    /**
     * 系统提醒 -- 特种/环保 设备管理标题后缀
     */
    public final static String SPEC_OR_ENV_EQUIPMENT_SUFFIX_TITLE = "的【TYPE】设备超期未检验";

    /**
     * 系统提醒 -- 环保设备管理标题前缀
     */
    public final static String ENV_EQUIPMENT_PREFIX_TITLE = "[环保设备管理]";

    /**
     * 系统提醒 -- 特种设备管理设备种类 占位符
     */
    public final static String TYPE_KEY = "TYPE";

    /**
     * 系统提醒 -- 账号初步同步标题
     */
    public final static String ACCOUNT_INITIAL_SYNC_TITLE = "[账号同步]【NAME】的账号【ACCOUNT】已同步至系统，请为其指定所属单位";

    /**
     * 系统提醒 -- 账号二次同步标题
     */
    public final static String ACCOUNT_AGAIN_SYNC_TITLE = "[账号同步]【NAME】的账号【ACCOUNT】已同步至系统，请为其指定角色";

    /**
     * 系统提醒 -- 账号同步 占位符
     */
    public final static String ACCOUNT_KEY = "ACCOUNT";
    /**
     * 系统提醒 -- 账号同步 姓名
     */
    public final static String NAME = "NAME";

    /**
     * 系统提醒 -- 账号冲突标题
     */
    public final static String ACCOUNT_CONFLICT_TITLE = "[账号冲突]【NAME】的账号【ACCOUNT】与本地账号冲突，请为该用户新增其他账号";

    /**
     * 系统提醒 -- 账号冲突标题 用于名称为空时替换
     */
    public final static String NAME_OF_RE = "【NAME】的";
}
