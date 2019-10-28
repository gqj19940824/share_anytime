package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * EnterpriseServiceBreau->esb;\r\nInnovationPublishList->ip
 * @author zhang
 * 生成时间 2019-09-25 14:51:39
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mIplEsbMain extends BaseEntity{


        

        
        
        
        /**
        * 行业类别
        **/
        @CommentTarget("行业类别")
        @TableField("industry_category")
        private Long industryCategory ;
        
        
        
        /**
        * 企业名称
        **/
        @CommentTarget("企业名称")
        @TableField("enterprise_name")
        private String enterpriseName ;
        
        
        
        /**
        * 企业简介
        **/
        @CommentTarget("企业简介")
        @TableField("enterprise_profile")
        private String enterpriseProfile ;
        
        
        
        /**
        * 概述
        **/
        @CommentTarget("概述")
        @TableField("summary")
        private String summary ;
        
        
        
        /**
        * 新产品
        **/
        @CommentTarget("新产品")
        @TableField("new_product")
        private String newProduct ;
        
        
        
        /**
        * 新技术
        **/
        @CommentTarget("新技术")
        @TableField("new_tech")
        private String newTech ;
        
        
        
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
        * 附件
        **/
        @CommentTarget("附件")
        @TableField("attachment_code")
        private String attachmentCode ;
        
        
        
        /**
        * 来源
        **/
        @CommentTarget("来源")
        @TableField("source")
        private Integer source ;
        
        
        
        /**
        * 状态
        **/
        @CommentTarget("状态")
        @TableField("status")
        private Integer status ;
        
        
        
        /**
        * 单位id
        **/
        @CommentTarget("单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty ;

        /**
         * 备注状态
         **/
        @CommentTarget("备注状态")
        @TableField("process_status")
        private Integer processStatus ;


        /**
         * 最新进展
         **/
        @TableField("latest_process")
        private String latestProcess ;

        /**
         * 首次更新时间
         **/
        @CommentTarget("首次更新时间")
        @TableField("gmt_first_deal")
        private Long gmtFirstDeal ;
}




