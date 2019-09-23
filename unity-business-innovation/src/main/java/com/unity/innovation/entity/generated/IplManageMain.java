package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 创新发布清单-发布管理主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:37
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class IplManageMain extends BaseEntity{


        
        /**
        * 编号_创新发布活动-活动管理一对多发布管理表
        **/
        @CommentTarget("编号_创新发布活动-活动管理一对多发布管理表")
        @TableField("id_ipa_manage_main_m_ipl_manage_main")
        private Long idIpaManageMainIplManageMain ;
        
        
        
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
        @CommentTarget("单位id")
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        
        
        /**
        * 发布结果
        **/
        @CommentTarget("发布结果")
        @TableField("publish_result")
        private String publishResult ;
        
        

}




