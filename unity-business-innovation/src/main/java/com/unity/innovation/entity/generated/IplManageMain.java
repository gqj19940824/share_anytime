package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.Attachment;
import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

import java.util.List;

/**
 * 创新发布清单-发布管理主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:37
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_manage_main")
public class IplManageMain extends BaseEntity{

        /**
        * 标题
        **/
        @CommentTarget("标题")
        @TableField("title")
        private String title ;
        
        
        
        /**
        * 状态
        **/
        @CommentTarget("状态")
        @TableField("status")
        private Integer status ;
        
        
        
        /**
        * 附件
        **/
        @CommentTarget("附件")
        @TableField("attachment_code")
        private String attachmentCode ;
        
        
        
        /**
        * 单位id
        **/
        @CommentTarget("主责单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty ;
        
        
        
        /**
        * 发布结果
        **/
        @CommentTarget("发布结果")
        @TableField("publish_result")
        private String publishResult ;

        /**
         * 提请时间
         **/
        @TableField("gmt_submit")
        private Long gmtSubmit ;
        
        /**
        * 发改局列表
        **/
        @CommentTarget("发改局列表ID")
        @TableField(exist = false)
        private List<Long> idiplDarbMains ;

        @TableField(exist = false)
        private List<Attachment> attachments;

}




