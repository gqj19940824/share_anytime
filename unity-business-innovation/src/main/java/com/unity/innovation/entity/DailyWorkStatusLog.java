package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mDailyWorkStatusLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


        
        
        
        
        
        
        
        
        
        
        
        
        

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "daily_work_status_log")
public class DailyWorkStatusLog extends mDailyWorkStatusLog {


    /**
     * 审批名称
     */
    @TableField(exist = false)
    private String logName;


    /**
     * 审批名称
     */
    @TableField(exist = false)
    private String deptName;









}

