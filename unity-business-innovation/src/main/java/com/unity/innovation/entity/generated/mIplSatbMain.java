package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 创新发布清单-科技局-主表
 *
 * @author G
 * 生成时间 2019-10-08 17:03:09
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class mIplSatbMain extends BaseEntity {

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
     * 需求类别
     **/
    @CommentTarget("需求类别")
    @TableField("demand_category")
    private Long demandCategory;

    /**
     * 项目名称
     **/
    @CommentTarget("项目名称")
    @TableField("project_name")
    private String projectName;

    /**
     * 项目地点
     **/
    @CommentTarget("项目地点")
    @TableField("project_address")
    private String projectAddress;

    /**
     * 项目介绍
     **/
    @CommentTarget("项目介绍")
    @TableField("project_introduce")
    private String projectIntroduce;

    /**
     * 需求总额
     **/
    @CommentTarget("需求总额")
    @TableField("total_amount")
    private Double totalAmount;

    /**
     * 银行
     **/
    @CommentTarget("银行")
    @TableField("bank")
    private Double bank;

    /**
     * 债券
     **/
    @CommentTarget("债券")
    @TableField("bond")
    private Double bond;

    /**
     * 自筹
     **/
    @CommentTarget("自筹")
    @TableField("raise")
    private Double raise;

    /**
     * 技术需求情况
     **/
    @CommentTarget("技术需求情况")
    @TableField("tech_demond_info")
    private String techDemondInfo;

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
     * 主责单位id
     **/
    @CommentTarget("主责单位id")
    @TableField("id_rbac_department_duty")
    private Long idRbacDepartmentDuty;
}




