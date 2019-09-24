package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * sys_message_read_log
 * @author G
 * 生成时间 2019-09-23 09:39:17
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mSysMessageReadLog extends BaseEntity{


        
        /**
        * 目标用户id
        **/
        @CommentTarget("目标用户id")
        @TableField("target_user_id")
        private Long targetUserId ;
        
        
        
        /**
        * 消息id
        **/
        @CommentTarget("消息id")
        @TableField("message_id")
        private Long messageId ;
        
        
        
        /**
        * 是否已读 0 否  1 是
        **/
        @CommentTarget("是否已读")
        @TableField("is_read")
        private Integer isRead ;
        
        

}




