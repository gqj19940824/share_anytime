package com.unity.common.constant;

/**
 * rbac Redis Keys 常量池
 * <p>
 * create by gengjiajia at 2019/01/23 16:22
 * @author gengjiajia
 */
public class DicConstants {

    // 账号组
    public final static String ACCOUNT = "001";

    // 行业类别
    public final static String INDUSTRY_CATEGORY = "INDUSTRY_CATEGORY";

    // 需求名目
    public final static String DEMAND_ITEM = "DEMAND_ITEM";

    // 需求类别
    public final static String DEMAND_CATEGORY = "DEMAND_CATEGORY";

    // 超级管理员
    public final static String SUPER_ADMIN = "superAdmin";
    /**
     * 禁止分配给项目账号的角色id集
     */
    public final static String NO_DIST_TO_PJ_OF_ROLE_IDS = "noDistToPjOfRoleIds";
    /**
     * 不能给二级账号的角色id集
     */
    public final static String NO_ALLOWED_DIST_2_OF_ROLE_IDS = "noDistTo2OfRoleIds";
    /**
     * 不能给二级管理员账号的角色id集
     */
    public final static String NO_ALLOWED_DIST_2_ADMIN_OF_ROLE_IDS = "no2AdminOfRoleIds";
    /**
     * 不能给三级账号的角色id集
     */
    public final static String NO_ALLOWED_DIST_3_OF_ROLE_IDS = "noDistTo3OfRoleIds";
    /**
     * 只允许分配给二三级账号的角色id集
     */
    public final static String NO_ALLOWED_DIST_1_OF_ROLE_IDS = "noDistTo1OfRoleIds";

    /**
     * 风险可能性
     */
    public final static String RISK_POSSIBILITY = "RiskPossibility";

    /**
     * 风险严重性
     */
    public final static String RISK_SERIOUSNESS = "RiskSeriousness";

    /**
     * 风险级别
     */
    public final static String RISK_LEVEL = "RiskLevel";

    /**
     * 风险类型
     */
    public final static String RISK_TYPE = "RiskType";

    /**
     * 风险级别计算的二维数组
     */
    public final static String RISK_CALCULATION = "RiskCalculation";

    /**
     * 风险级别计算的二维数组第一项项
     */
    public final static String RISK_CALCULATION_CODE = "0";

    /**
     * 角色编码组
     */
    public final static String ROLE_CODE = "roleCode";

    /**
     * 角色编码组字典项：集团安全部领导角色
     */
    public final static String ROLE_CODE_1 = "1";

    /**
     * 角色编码组字典项：集团安全部员工角色
     */
    public final static String ROLE_CODE_2 = "2";

    /**
     * 角色编码组字典项：集团其他部门账号角色
     */
    public final static String ROLE_CODE_3 = "3";

    /**
     * 角色编码组字典项：二级单位领导角色
     */
    public final static String ROLE_CODE_4 = "4";

    /**
     * 角色编码组字典项：二级单位员工角色
     */
    public final static String ROLE_CODE_5 = "5";

    /**
     * 角色编码组字典项：三级单位领导角色
     */
    public final static String ROLE_CODE_6 = "6";

    /**
     * 角色编码组字典项：三级单位员工角色
     */
    public final static String ROLE_CODE_7 = "7";

    /**
     * 角色编码组字典项：项目账号角色
     */
    public final static String ROLE_CODE_8 = "8";


}
