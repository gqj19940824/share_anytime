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
    /**通知主责单位短信内容*/
    public static Map<String,String> sendSmsContentMap;
    /**通知协同单位短信内容*/
    public static Map<String,String> sendHelpSmsContentMap;
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

    /*Map中的key的定义为 清单类型与数据来源拼接组成唯一性key*/
    static {
        addInventoryMsgTitleMap = Maps.newHashMap();
        addInventoryMsgTitleMap.put("101","【TITLE】正在寻求参与开发区建设，请尽快处理！");
        addInventoryMsgTitleMap.put("201","【TITLE】正在寻求创新发展，请尽快处理！");
        addInventoryMsgTitleMap.put("301","【TITLE】正在寻求资本支持，请尽快处理！");
        addInventoryMsgTitleMap.put("401","【TITLE】正在寻求高端人才，请尽快处理！");
        addInventoryMsgTitleMap.put("901","【TITLE】已反应意见和建议，请尽快处理！");
        addInventoryMsgTitleMap.put("1001","【TITLE】报名参与发布会，请尽快处理！");
        addInventoryMsgTitleMap.put("801","【TITLE】正在寻找投资项目，请尽快处理！");

        addInventoryMsgTitleMap.put("102","【TITLE】寻求参与开发区建设的事项超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("202","【TITLE】寻求创新发展的事项超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("302","【TITLE】寻求资本支持的事项超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("402","【TITLE】寻求高端人才的事项超过TIME未处理，请及时处理！");
        addInventoryMsgTitleMap.put("902","【TITLE】反应的意见和建议超过TIME未处理，请及时处理！");

        addInventoryMsgTitleMap.put("103","【TITLE】寻求参与开发区建设的事项超过TIME未更新进展，请及时更新！");
        addInventoryMsgTitleMap.put("203","【TITLE】寻求创新发展的事项超过TIME未更新进展，请及时更新！");
        addInventoryMsgTitleMap.put("303","【TITLE】寻求资本支持的事项超过TIME未更新进展，请及时更新！");
        addInventoryMsgTitleMap.put("403","【TITLE】寻求高端人才的事项超过TIME未更新进展，请及时更新！");
        addInventoryMsgTitleMap.put("903","【TITLE】反应的意见和建议超过TIME未更新进展，请及时更新！");

        addInventoryHelpMsgTitleMap = Maps.newHashMap();
        addInventoryHelpMsgTitleMap.put("101","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项，请及时处理！");
        addInventoryHelpMsgTitleMap.put("201","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项，请及时处理！");
        addInventoryHelpMsgTitleMap.put("301","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项，请及时处理！");
        addInventoryHelpMsgTitleMap.put("401","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项，请及时处理！");

        addInventoryHelpMsgTitleMap.put("102","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项超过TIME未处理，请及时处理！");
        addInventoryHelpMsgTitleMap.put("202","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项超过TIME未处理，请及时处理！");
        addInventoryHelpMsgTitleMap.put("302","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项超过TIME未处理，请及时处理！");
        addInventoryHelpMsgTitleMap.put("402","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项超过TIME未处理，请及时处理！");

        addInventoryHelpMsgTitleMap.put("103","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项超过TIME未更新进展，请及时更新！");
        addInventoryHelpMsgTitleMap.put("203","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项超过TIME未更新进展，请及时更新！");
        addInventoryHelpMsgTitleMap.put("303","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项超过TIME未更新进展，请及时更新！");
        addInventoryHelpMsgTitleMap.put("403","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项超过TIME未更新进展，请及时更新！");

        addInventoryHelpMsgTitleMap.put("104","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的基本信息已更新，请及时查看！");
        addInventoryHelpMsgTitleMap.put("204","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的基本信息已更新，请及时查看！");
        addInventoryHelpMsgTitleMap.put("304","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的基本信息已更新，请及时查看！");
        addInventoryHelpMsgTitleMap.put("404","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的基本信息已更新，请及时查看！");

        addInventoryHelpMsgTitleMap.put("105","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项已删除，请知悉！");
        addInventoryHelpMsgTitleMap.put("205","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项已删除，请知悉！");
        addInventoryHelpMsgTitleMap.put("305","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项已删除，请知悉！");
        addInventoryHelpMsgTitleMap.put("405","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项已删除，请知悉！");

        addInventoryHelpMsgTitleMap.put("106","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项已被强制关闭，请知悉！");
        addInventoryHelpMsgTitleMap.put("206","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项已被强制关闭，请知悉！");
        addInventoryHelpMsgTitleMap.put("306","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项已被强制关闭，请知悉！");
        addInventoryHelpMsgTitleMap.put("406","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项已被强制关闭，请知悉！");

        addInventoryHelpMsgTitleMap.put("107","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项已被重新开启，请及时处理！");
        addInventoryHelpMsgTitleMap.put("207","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项已被重新开启，请及时处理！");
        addInventoryHelpMsgTitleMap.put("307","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项已被重新开启，请及时处理！");
        addInventoryHelpMsgTitleMap.put("407","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项已被重新开启，请及时处理！");

        reviewMsgTitleMap = Maps.newHashMap();
        reviewMsgTitleMap.put("1","【DEP_NAME】已提交【TITLE】，请尽快审核！");
        reviewMsgTitleMap.put("2","【TITLE】已被驳回，请修改后重新提交！");
        reviewMsgTitleMap.put("3","【TITLE】已通过！");
        reviewMsgTitleMap.put("4","【TITLE】已发布！");
        reviewMsgTitleMap.put("5","【TITLE】已更新发布效果！");

        sendSmsContentMap = Maps.newHashMap();
        sendSmsContentMap.put("101","【TITLE】正在寻求参与开发区建设，请前往【城市创新合作实时清单】模块处理！");
        sendSmsContentMap.put("201","【TITLE】正在寻求创新发展，请前往【企业创新发展实时清单】模块处理！");
        sendSmsContentMap.put("301","【TITLE】正在寻求资本支持，请前往【成长目标投资实时清单】模块处理！");
        sendSmsContentMap.put("401","【TITLE】正在寻求高端人才，请前往【高端才智需求实时清单】模块处理！");
        sendSmsContentMap.put("901","【TITLE】已反应意见和建议，请前往【意见和建议】模块处理！");
        sendSmsContentMap.put("1001","【TITLE】报名参与发布会，请前往【发布会报名信息管理】模块处理！");
        sendSmsContentMap.put("801","【TITLE】正在寻找投资项目，请前往【投资机构信息管理】模块处理！");

        sendHelpSmsContentMap = Maps.newHashMap();
        sendHelpSmsContentMap.put("101","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项，请前往【实时清单协同处理】模块处理！");
        sendHelpSmsContentMap.put("201","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项，请前往【实时清单协同处理】模块处理！");
        sendHelpSmsContentMap.put("301","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项，请前往【实时清单协同处理】模块处理！");
        sendHelpSmsContentMap.put("401","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项，请前往【实时清单协同处理】模块处理！");

        sendHelpSmsContentMap.put("106","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项已被强制关闭，请知悉！");
        sendHelpSmsContentMap.put("206","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项已被强制关闭，请知悉！");
        sendHelpSmsContentMap.put("306","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项已被强制关闭，请知悉！");
        sendHelpSmsContentMap.put("406","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项已被强制关闭，请知悉！");

        sendHelpSmsContentMap.put("107","【DEP_NAME】邀请协同处理【TITLE】寻求参与开发区建设的事项已被重新开启，请前往【实时清单协同处理】模块处理！");
        sendHelpSmsContentMap.put("207","【DEP_NAME】邀请协同处理【TITLE】寻求创新发展的事项已被重新开启，请前往【实时清单协同处理】模块处理！");
        sendHelpSmsContentMap.put("307","【DEP_NAME】邀请协同处理【TITLE】寻求资本支持的事项已被重新开启，请前往【实时清单协同处理】模块处理！");
        sendHelpSmsContentMap.put("407","【DEP_NAME】邀请协同处理【TITLE】寻求高端人才的事项已被重新开启，请前往【实时清单协同处理】模块处理！");

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
