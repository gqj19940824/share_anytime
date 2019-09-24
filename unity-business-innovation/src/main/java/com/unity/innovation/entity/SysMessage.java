package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mSysMessage;
import lombok.*;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_message")
public class SysMessage extends mSysMessage {

    /**
     * 开始时间
     */
    @TableField(exist = false)
    private Long startTime;

    /**
     * 截止时间
     */
    @TableField(exist = false)
    private Long endTime;

    /**
     * 是否已读 0 否 1 是
     */
    @TableField(exist = false)
    private Integer isRead;

    /**
     * 截止时间
     */
    @TableField(exist = false)
    private Long targetUserId;
}

