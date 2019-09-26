package com.unity.innovation.constants;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 系统消息常量类
 *
 * <p>
 * create by gengjiajia at 2019/09/23 15:10
 */
public class MessageConstants {

    /**新增实时清单*/
    public static Map<String,String> addInventoryMsgTitleMap;
    /**清单协同处理*/
    public static Map<String,String> addInventoryHelpMsgTitleMap;
    /**发布审核*/
    public static Map<String,String> reviewMsgTitleMap;
    /**清单数据来源类型对应角色字典组编码*/
    public static Map<Integer,String> inventoryDataSourceClassToRoleMap;
    /**审核数据来源类型对应角色字典组编码*/
    public static Map<Integer,String> reviewDataSourceClassToRoleMap;
    /**单位 名称 占位符*/
    public final static String DEP_NAME = "DEP_NAME";
    /**标题 占位符*/
    public final static String TITLE = "TITLE";
    /**时间 占位符*/
    public final static String TIME = "TIME";
    /**创新发布实时清单对应角色字典组code*/
    public final static String REALTIME_INVENTORY_ROLES = "realtime_list_roles";

    /*Map中的key的定义为 数据来源类型拼接流程编码*/
    static {
        addInventoryMsgTitleMap = Maps.newHashMap();
        addInventoryMsgTitleMap.put("11","【TITLE】正在寻求参与开发区建设，请尽快处理！");
        addInventoryMsgTitleMap.put("21","【TITLE】正在寻求创新发展，请尽快处理！");
        addInventoryMsgTitleMap.put("31","【TITLE】正在寻求资本支持，请尽快处理！");
        addInventoryMsgTitleMap.put("41","【TITLE】正在寻求高端人才，请尽快处理！");
        addInventoryMsgTitleMap.put("51","【TITLE】已反应意见和建议，请尽快处理！");
        addInventoryMsgTitleMap.put("61","【TITLE】报名参与发布会，请尽快处理！");
        addInventoryMsgTitleMap.put("71","【TITLE】正在寻求合作伙伴，请尽快处理！");

        addInventoryMsgTitleMap.put("12","【TITLE】寻求参与开发区建设的事项超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("22","【TITLE】寻求创新发展的事项超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("32","【TITLE】寻求资本支持的事项超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("42","【TITLE】寻求高端人才的事项超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("52","【TITLE】反应的意见和建议超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("62","【TITLE】报名参与发布会的事项超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("72","【TITLE】寻找投资项目的事项超过TIME未处理，请及时处理！");

        addInventoryMsgTitleMap.put("13","【TITLE】寻求参与开发区建设的事项超过TIME未更新进展，请及时更新！");
        addInventoryMsgTitleMap.put("23","【TITLE】寻求创新发展的事项超过TIME未更新进展，请及时更新！");
        addInventoryMsgTitleMap.put("33","【TITLE】寻求资本支持的事项超过TIME未更新进展，请及时更新！");
        addInventoryMsgTitleMap.put("43","【TITLE】寻求高端人才的事项超过TIME未更新进展，请及时更新！");
        addInventoryMsgTitleMap.put("53","【TITLE】反应的意见和建议超过TIME未更新进展，请及时更新！");

        addInventoryHelpMsgTitleMap = Maps.newHashMap();
        addInventoryHelpMsgTitleMap.put("11","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项，请及时处理！");
        addInventoryHelpMsgTitleMap.put("21","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项，请及时处理！");
        addInventoryHelpMsgTitleMap.put("31","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项，请及时处理！");
        addInventoryHelpMsgTitleMap.put("41","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项，请及时处理！");

        addInventoryHelpMsgTitleMap.put("12","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项超过TIME未处理，请及时处理！");
        addInventoryHelpMsgTitleMap.put("22","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项超过TIME未处理，请及时处理！");
        addInventoryHelpMsgTitleMap.put("32","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项超过TIME未处理，请及时处理！");
        addInventoryHelpMsgTitleMap.put("42","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项超过TIME未处理，请及时处理！");

        addInventoryHelpMsgTitleMap.put("13","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项超过TIME未更新进展，请及时更新！");
        addInventoryHelpMsgTitleMap.put("23","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项超过TIME未更新进展，请及时更新！");
        addInventoryHelpMsgTitleMap.put("33","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项超过TIME未更新进展，请及时更新！");
        addInventoryHelpMsgTitleMap.put("43","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项超过TIME未更新进展，请及时更新！");

        addInventoryHelpMsgTitleMap.put("14","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的基本信息已更新，请及时查看！");
        addInventoryHelpMsgTitleMap.put("24","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的基本信息已更新，请及时查看！");
        addInventoryHelpMsgTitleMap.put("34","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的基本信息已更新，请及时查看！");
        addInventoryHelpMsgTitleMap.put("44","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的基本信息已更新，请及时查看！");

        addInventoryHelpMsgTitleMap.put("15","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项已删除，请知悉！");
        addInventoryHelpMsgTitleMap.put("25","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项已删除，请知悉！");
        addInventoryHelpMsgTitleMap.put("35","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项已删除，请知悉！");
        addInventoryHelpMsgTitleMap.put("45","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项已删除，请知悉！");

        addInventoryHelpMsgTitleMap.put("16","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项已被强制关闭，请知悉！");
        addInventoryHelpMsgTitleMap.put("26","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项已被强制关闭，请知悉！");
        addInventoryHelpMsgTitleMap.put("36","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项已被强制关闭，请知悉！");
        addInventoryHelpMsgTitleMap.put("46","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项已被强制关闭，请知悉！");

        addInventoryHelpMsgTitleMap.put("17","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项已被重新开启，请及时处理！");
        addInventoryHelpMsgTitleMap.put("27","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项已被重新开启，请及时处理！");
        addInventoryHelpMsgTitleMap.put("37","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项已被重新开启，请及时处理！");
        addInventoryHelpMsgTitleMap.put("47","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项已被重新开启，请及时处理！");

        reviewMsgTitleMap = Maps.newHashMap();
        reviewMsgTitleMap.put("1","【DEP_NAME】已提交【TITLE】，请尽快审核！");
        reviewMsgTitleMap.put("2","【TITLE】已被驳回，请修改后重新提交！");
        reviewMsgTitleMap.put("3","【TITLE】已通过！");
        reviewMsgTitleMap.put("4","【TITLE】已发布！");
        reviewMsgTitleMap.put("5","【TITLE】已更新发布效果！");

        inventoryDataSourceClassToRoleMap = Maps.newHashMap();
        inventoryDataSourceClassToRoleMap.put(1,"devel_reform_role");
        inventoryDataSourceClassToRoleMap.put(2,"company_server_role");
        inventoryDataSourceClassToRoleMap.put(3,"science_role");
        inventoryDataSourceClassToRoleMap.put(4,"organization_role");
        inventoryDataSourceClassToRoleMap.put(5,"supervise_role");
        inventoryDataSourceClassToRoleMap.put(6,"investment_role");
        inventoryDataSourceClassToRoleMap.put(7,"publicity_role");

        reviewDataSourceClassToRoleMap = Maps.newHashMap();
        reviewDataSourceClassToRoleMap.put(1,"publicity_role_b");
    }


}
