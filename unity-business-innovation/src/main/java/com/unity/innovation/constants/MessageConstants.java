package com.unity.innovation.constants;

import com.google.common.collect.Maps;
import com.unity.innovation.enums.SysMessageDataSourceClassEnum;

import java.util.Map;

/**
 * 系统消息常量类
 *
 * <p>
 * create by gengjiajia at 2019/09/23 15:10
 */
public class MessageConstants {

    public static Map<Integer,String> addInventoryMessageMap;
    public static Map<Integer,String> addInventoryAssistanceMap;

    static {
        addInventoryMessageMap = Maps.newHashMap();
        addInventoryMessageMap.put(SysMessageDataSourceClassEnum.DEVELOPMENT.getId(),"【COMPANY_NAME】正在寻求参与开发区建设，请尽快处理！");
        addInventoryMessageMap.put(SysMessageDataSourceClassEnum.COMPANY_SERVER.getId(),"【COMPANY_NAME】正在寻求创新发展，请尽快处理！");
        addInventoryMessageMap.put(SysMessageDataSourceClassEnum.TECHNOLOGY.getId(),"【COMPANY_NAME】正在寻求资本支持，请尽快处理！");
        addInventoryMessageMap.put(SysMessageDataSourceClassEnum.ORGANIZATION.getId(),"【COMPANY_NAME】正在寻求高端人才，请尽快处理！");
        addInventoryMessageMap.put(SysMessageDataSourceClassEnum.INSPECTION.getId(),"【COMPANY_NAME】已反应意见和建议，请尽快处理！");
        addInventoryMessageMap.put(SysMessageDataSourceClassEnum.PROPAGANDA.getId(),"【COMPANY_NAME】报名参与发布会，请尽快处理！");
        addInventoryMessageMap.put(SysMessageDataSourceClassEnum.INVESTMENT.getId(),"【COMPANY_NAME】正在寻求合作伙伴，请尽快处理！");

        addInventoryAssistanceMap = Maps.newHashMap();
        addInventoryAssistanceMap.put(SysMessageDataSourceClassEnum.DEVELOPMENT.getId(),"【DEP_NAME】邀请协同处理【COMPANY_NAME】寻求参与开发区建设的事项，请及时处理！");
        addInventoryAssistanceMap.put(SysMessageDataSourceClassEnum.COMPANY_SERVER.getId(),"【DEP_NAME】邀请协同处理【COMPANY_NAME】寻求创新发展的事项，请及时处理！");
        addInventoryAssistanceMap.put(SysMessageDataSourceClassEnum.TECHNOLOGY.getId(),"【DEP_NAME】邀请协同处理【COMPANY_NAME】寻求资本支持的事项，请及时处理！");
        addInventoryAssistanceMap.put(SysMessageDataSourceClassEnum.ORGANIZATION.getId(),"【DEP_NAME】邀请协同处理【COMPANY_NAME】寻求高端人才的事项，请及时处理！");
        addInventoryAssistanceMap.put(SysMessageDataSourceClassEnum.INVESTMENT.getId(),"【DEP_NAME】邀请协同处理【COMPANY_NAME】寻找投资项目的事项，请及时处理！");
    }

    /**单位 名称*/
    public final static String DEP_NAME = "DEP_NAME";

    public final static String COMPANY_NAME = "COMPANY_NAME";

    /**创新发布实时清单字典组code*/
    public final static String REALTIME_INVENTORY = "REALTIME_INVENTORY";
}
