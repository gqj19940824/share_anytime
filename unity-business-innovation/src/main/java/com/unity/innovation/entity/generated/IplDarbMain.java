package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.enums.BizTypeEnum;
import lombok.*;

import java.util.List;

/**
 * darb->Development and Reform Bureau\r\n\r\n
 * @author zhang
 * 生成时间 2019-09-21 15:45:36
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_darb_main")
public class IplDarbMain extends BaseEntity{

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
        * 需求名目
        **/
        @CommentTarget("需求名目")
        @TableField("demand_item")
        private Long demandItem ;
        
        
        
        /**
        * 需求类别
        **/
        @CommentTarget("需求类别")
        @TableField("demand_category")
        private Long demandCategory ;
        
        
        
        /**
        * 项目/产品/服务名称
        **/
        @CommentTarget("项目/产品/服务名称")
        @TableField("project_name")
        private String projectName ;
        
        
        
        /**
        * 内容及规模
        **/
        @CommentTarget("内容及规模")
        @TableField("content")
        private String content ;
        
        
        
        /**
        * 总投资
        **/
        @CommentTarget("总投资")
        @TableField("total_investment")
        private Double totalInvestment ;
        
        
        
        /**
        * 项目形象进度
        **/
        @CommentTarget("项目形象进度")
        @TableField("project_progress")
        private String projectProgress ;
        
        
        
        /**
        * 需求总额
        **/
        @CommentTarget("需求总额")
        @TableField("total_amount")
        private Double totalAmount ;
        
        
        
        /**
        * 银行
        **/
        @CommentTarget("银行")
        @TableField("bank")
        private Double bank ;
        
        
        
        /**
        * 债券
        **/
        @CommentTarget("债券")
        @TableField("bond")
        private Double bond ;
        
        
        /**
        * 自筹
        **/
        @CommentTarget("自筹")
        @TableField("self_raise")
        private Double selfRaise ;


        /**
        * 增信方式
        **/
        @CommentTarget("增信方式")
        @TableField("increase_trust_type")
        private String increaseTrustType ;
        
        
        
        /**
        * 是否引入社会资本
        **/
        @CommentTarget("是否引入社会资本")
        @TableField("whether_introduce_social_capital")
        private Integer whetherIntroduceSocialCapital ;
        
        
        
        /**
        * 建设类别
        **/
        @CommentTarget("建设类别")
        @TableField("construction_category")
        private String constructionCategory ;
        
        
        
        /**
        * 建设阶段
        **/
        @CommentTarget("建设阶段")
        @TableField("construction_stage")
        private String constructionStage ;
        
        
        
        /**
        * 建设模式
        **/
        @CommentTarget("建设模式")
        @TableField("construction_model")
        private String constructionModel ;
        
        
        
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
        * 备注状态
        **/
        @CommentTarget("最新进展")
        @TableField("latest_process")
        private String latestProcess ;

        /**
        * 主责单位id
        **/
        @CommentTarget("主责单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty;

        @TableField(exist = false)
        private List<Attachment> attachments;

        @TableField(exist = false)
        private String idRbacDepartmentName;

        @TableField(exist = false)
        private String creatTime;

        @TableField(exist = false)
        private String updateTime;

        @TableField(exist = false)
        private List<IplAssist> iplAssists;

        @TableField(exist = false)
        private Integer bizType = BizTypeEnum.CITY.getType();

        /**
         * 首次更新时间
         **/
        @CommentTarget("首次更新时间")
        @TableField("gmt_first_deal")
        private Long gmtFirstDeal ;

        /**
         * 是否打包页面  0否 1 是
         */
        @TableField(exist = false)
        private Integer isForPackage;
}




