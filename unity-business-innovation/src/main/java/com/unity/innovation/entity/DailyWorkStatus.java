package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mDailyWorkStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "daily_work_status")
public class DailyWorkStatus extends mDailyWorkStatus {

    /**
     * 关键字
     */
    @TableField(exist = false)
    private Long keyWord;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    private String createTime;

    /**
     * 提请时间
     */
    @TableField(exist = false)
    private String submitTime;
}


