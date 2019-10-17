package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mPmInfoDeptLog;


/**
 * @Author jh
 * */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "pm_info_dept_log")
public class PmInfoDeptLog extends mPmInfoDeptLog{



    /**
     * 通过/驳回  1:通过 0：驳回
     **/
    @TableField(exist = false)
    private Integer passOrReject;


    /**
     * 单位名称
     * */
    @TableField(exist = false)
    private String departmentName ;


    /**
     * 状态名称
     * */
    @TableField(exist = false)
    private String statusName ;
  
}

