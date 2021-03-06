package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 创新发布清单-宣传部-主表
 *
 * @author G
 * 生成时间 2019-09-29 15:50:28
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class mIplPdMain extends BaseEntity {


    /**
     *
     **/
    @CommentTarget("")
    @TableField("id_iplm_main_m_ipl_main")
    private Long idIplmMainIplMain;


    /**
     * 行业类别
     **/
    @CommentTarget("行业类别")
    @TableField("industry_category")
    private Long industryCategory;


    /**
     * 企业名称
     **/
    @CommentTarget("企业名称")
    @TableField("enterprise_name")
    private String enterpriseName;


    /**
     * 企业简介
     **/
    @CommentTarget("企业简介")
    @TableField("enterprise_introduction")
    private String enterpriseIntroduction;


    /**
     * 具体意向和事由
     **/
    @CommentTarget("具体意向和事由")
    @TableField("specific_cause")
    private String specificCause;


    /**
     * 身份证
     **/
    @CommentTarget("身份证")
    @TableField("id_card")
    private String idCard;


    /**
     * 联系人
     **/
    @CommentTarget("联系人")
    @TableField("contact_person")
    private String contactPerson;


    /**
     * 联系方式
     **/
    @CommentTarget("联系方式")
    @TableField("contact_way")
    private String contactWay;


    /**
     * 附件
     **/
    @CommentTarget("附件")
    @TableField("attachment_code")
    private String attachmentCode;


    /**
     * 来源
     **/
    @CommentTarget("来源")
    @TableField("source")
    private Integer source;


    /**
     * 状态
     **/
    @CommentTarget("状态")
    @TableField("status")
    private Integer status;


    /**
     * 职务
     **/
    @CommentTarget("职务")
    @TableField("post")
    private String post;

    /**
     * 主责单位id
     **/
    @CommentTarget("主责单位id")
    @TableField("id_rbac_department_duty")
    private Long idRbacDepartmentDuty;
}




