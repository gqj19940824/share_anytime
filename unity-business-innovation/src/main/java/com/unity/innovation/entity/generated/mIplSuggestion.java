package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 意见建议-纪检组
 * @author zhang
 * 生成时间 2019-09-23 15:38:10
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mIplSuggestion extends BaseEntity{


        
        /**
        * 标题
        **/
        @CommentTarget("标题")
        @TableField("title")
        private String title ;
        
        
        
        /**
        * 意见和建议
        **/
        @CommentTarget("意见和建议")
        @TableField("suggestion")
        private String suggestion ;
        
        
        
        /**
        * 联系人
        **/
        @CommentTarget("联系人")
        @TableField("contact_person")
        private String contactPerson ;
        
        
        
        /**
        * 联系方式
        **/
        @CommentTarget("联系方式")
        @TableField("contact_way")
        private String contactWay ;
        
        
        
        /**
        * 联系邮箱
        **/
        @CommentTarget("联系邮箱")
        @TableField("email")
        private String email ;
        
        
        
        /**
        * 附件
        **/
        @CommentTarget("附件")
        @TableField("attachment_code")
        private String attachmentCode ;
        
        
        
        /**
        * 状态
        **/
        @CommentTarget("状态")
        @TableField("status")
        private Integer status ;
        
        
        
        /**
        * 来源
        **/
        @CommentTarget("来源")
        @TableField("source")
        private Integer source ;
        
        
        
        /**
        * 是否超时
        **/
        @CommentTarget("状态")
        @TableField("process_status")
        private Integer processStatus ;


        /**
         * 企业名称
         **/
        @CommentTarget("企业名称")
        @TableField("enterprise_name")
        private String enterpriseName ;

}




