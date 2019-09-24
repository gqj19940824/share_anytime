package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 通知公告
 * @author zhang
 * 生成时间 2019-09-23 15:00:35
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mSysNotice extends BaseEntity{


        
        /**
        * 标题
        **/
        @CommentTarget("标题")
        @TableField("title")
        private String title ;
        
        
        
        /**
        * 通告内容
        **/
        @CommentTarget("通告内容")
        @TableField("content")
        private String content ;
        
        
        
        /**
        * 附件码
        **/
        @CommentTarget("附件码")
        @TableField("attachment_code")
        private String attachmentCode ;
        
        
        
        /**
        * 发送情况
        **/
        @CommentTarget("发送情况")
        @TableField("is_send")
        private Integer isSend ;



        /**
         * 发送时间
         **/
        @CommentTarget("发送时间")
        @TableField("gmt_send")
        private Long gmtSend ;

}




