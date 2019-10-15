package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 路演企业信息管理-科技局-基础数据表
 * @author zhang
 * 生成时间 2019-10-15 15:33:00
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mInfoDeptSatb extends BaseEntity{


        
        /**
        * 企业名称
        **/
        @CommentTarget("企业名称")
        @TableField("enterprise_name")
        private String enterpriseName ;
        
        
        
        /**
        * 行业类别
        **/
        @CommentTarget("行业类别")
        @TableField("industry_category")
        private Long industryCategory ;
        
        
        
        /**
        * 企业规模
        **/
        @CommentTarget("企业规模")
        @TableField("enterprise_scale")
        private Long enterpriseScale ;
        
        
        
        /**
        * 企业性质
        **/
        @CommentTarget("企业性质")
        @TableField("enterprise_nature")
        private Long enterpriseNature ;
        
        
        
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
        * 企业简介
        **/
        @CommentTarget("企业简介")
        @TableField("enterprise_introduction")
        private String enterpriseIntroduction ;
        
        
        
        /**
        * 附件码
        **/
        @CommentTarget("附件码")
        @TableField("attachment_code")
        private String attachmentCode ;
        
        
        
        /**
        * 入区企业信息发布管理id
        **/
        @CommentTarget("入区企业信息发布管理id")
        @TableField("id_pm_info_dept")
        private Long idPmInfoDept ;
        
        
        
        /**
        * 创新成果概况
        **/
        @CommentTarget("创新成果概况")
        @TableField("in_general_situation")
        private String inGeneralSituation ;
        
        
        
        /**
        * 创新成功详情
        **/
        @CommentTarget("创新成功详情")
        @TableField("in_detail")
        private String inDetail ;
        
        
        
        /**
        * 创新成功水平
        **/
        @CommentTarget("创新成功水平")
        @TableField("achievement_level")
        private Long achievementLevel ;
        
        
        
        /**
        * 是否对外发布
        **/
        @CommentTarget("是否对外发布")
        @TableField("is_publish_first")
        private Integer isPublishFirst ;
        
        

}




