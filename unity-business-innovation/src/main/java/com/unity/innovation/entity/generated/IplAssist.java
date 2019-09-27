package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 创新发布清单-协同事项
 * @author zhang
 * 生成时间 2019-09-21 15:45:35
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_assist")
public class IplAssist extends BaseEntity{

        /**
        * 协同单位id
        **/
        @CommentTarget("协同单位id")
        @TableField("id_rbac_department_assist")
        private Long idRbacDepartmentAssist ;


        /**
         * 协同单位名称
         **/
        @TableField(exist = false)
        private String nameRbacDepartmentAssist ;


        /**
        * 邀请事项
        **/
        @CommentTarget("邀请事项")
        @TableField("invite_info")
        private String inviteInfo ;
        
        
        
        /**
        * 处理状态
        **/
        @CommentTarget("处理状态")
        @TableField("deal_status")
        private Integer dealStatus ;
        
        
        
        /**
        * 所属主表id
        **/
        @CommentTarget("所属主表id")
        @TableField("id_ipl_main")
        private Long idIplMain ;
        
        
        
        /**
        * 主责单位id
        **/
        @CommentTarget("主责单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty ;


        /**
        * 进展超时
        **/
        @CommentTarget("进展超时")
        @TableField("process_status")
        private Integer processStatus ;



}




