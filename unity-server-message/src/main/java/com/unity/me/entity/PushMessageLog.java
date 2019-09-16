package com.unity.me.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.me.entity.generated.mPushMessageLog;
import lombok.*;

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "me_push_message_log")
public class PushMessageLog extends mPushMessageLog {


    /**
     * 创建时间
     **/
    @TableField(exist = false)
    private String createTime;

    /**
     * 已读未读
     **/
    @TableField(exist = false)
    private Integer isRead;
}


