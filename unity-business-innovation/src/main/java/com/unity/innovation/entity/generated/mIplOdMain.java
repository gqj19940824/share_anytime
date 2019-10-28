package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * od->organization department
 * @author zhang
 * 生成时间 2019-10-14 09:47:50
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mIplOdMain extends BaseEntity{


        

        
        
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
        @TableField("enterprise_introduction")
        private String enterpriseIntroduction ;
        
        
        
        /**
        * 岗位需求名称
        **/
        @CommentTarget("岗位需求名称")
        @TableField("jd_name")
        private String jdName ;
        
        
        
        /**
        * 需求人员专业领域
        **/
        @CommentTarget("需求人员专业领域")
        @TableField("major_demand")
        private String majorDemand ;
        
        
        
        /**
        * 岗位需求数量
        **/
        @CommentTarget("岗位需求数量")
        @TableField("job_demand_num")
        private Integer jobDemandNum ;
        
        
        
        /**
        * 工作职责
        **/
        @CommentTarget("工作职责")
        @TableField("duty")
        private String duty ;
        
        
        
        /**
        * 任职资格
        **/
        @CommentTarget("任职资格")
        @TableField("qualification")
        private String qualification ;
        
        
        
        /**
        * 支持条件和福利待遇
        **/
        @CommentTarget("支持条件和福利待遇")
        @TableField("specific_cause")
        private String specificCause ;
        
        
        
        /**
        * 联系邮箱
        **/
        @CommentTarget("联系邮箱")
        @TableField("email")
        private String email ;
        
        
        
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
         * 单位id
         **/
        @CommentTarget("单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty ;

        /**
         * 首次更新时间
         **/
        @CommentTarget("首次更新时间")
        @TableField("gmt_first_deal")
        private Long gmtFirstDeal ;
}




