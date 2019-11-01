package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.base.CommentTarget;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.mIplOdMain;
import com.unity.innovation.enums.BizTypeEnum;
import lombok.*;

import java.util.List;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_od_main")
public class IplOdMain extends mIplOdMain{

    /**
     * 行业类型名称
     */
    @TableField(exist = false)
    private String industryCategoryName;


    /**
     * 创建时间
     */
    @TableField(exist = false)
    private String createTime;

    /**
     * 来源名称
     */
    @TableField(exist = false)
    private String sourceName;

    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String statusName;

    /**
     * 备注名称
     */
    @TableField(exist = false)
    private String processStatusName;

    /**
     * 附件集合
     */
    @TableField(exist = false)
    private List<Attachment> attachmentList;


    /**
     * 协同事项集合
     */
    @CommentTarget("协同事项集合")
    @TableField(exist = false)
    private  List<IplAssist> assistList;


    @TableField(exist = false)
    private Integer bizType = BizTypeEnum.INTELLIGENCE.getType();

    /**
     * 单位名称
     */
    @TableField(exist = false)
    private String idRbacDepartmentName;
}

