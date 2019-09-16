package com.unity.common.client.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;



@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder(builderMethodName = "newInstance")
public class SysReminder {

    /**
     * 数据来源:status:1 通知公告 public_notice,2 月度隐患排查治理 risk_investigate,3 隐患整改 investigation_chang,4 应急值班 emer_duty,5 特种设备管理 spec_equipment
     **/
    private Integer dataSource;


    /**
     * 通知标题
     **/
    private String title;


    /**
     * 源数据id
     **/
    private Long sourceId;

    /**
     * 单位id
     */
    private Long idRbacDepartment;

}


