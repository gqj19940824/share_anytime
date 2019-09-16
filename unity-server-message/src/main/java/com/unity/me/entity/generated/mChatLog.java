package com.unity.me.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 聊天记录信息
 * @author creator
 * 生成时间 2019-02-12 12:47:33
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mChatLog extends BaseEntity{


        
        /**
        * 发送人
        **/
        @TableField("sender")
        private Long sender ;
        
        
        
        /**
        * 接收人
        **/
        @TableField("receiver")
        private Long receiver ;
        
        
        
        /**
        * 内容:textarea:
        **/
        @TableField("content")
        private String content ;
        
        
        
        /**
        * 聊天内容类型:status:1 文字 text,2 音频 redio,3 图片 picture
        **/
        @TableField("msg_type")
        private Integer msgType ;
        
        
        
        /**
        * 是否已读:flag:1 是,0 否
        **/
        @TableField("is_read")
        private Integer isRead ;
        
        
        
        /**
        * 聊天方式:status:1 单聊 single,2 群聊 group
        **/
        @TableField("way")
        private Integer way ;
        
        

}




