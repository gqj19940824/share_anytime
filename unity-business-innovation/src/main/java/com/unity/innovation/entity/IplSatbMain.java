package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.base.CommentTarget;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.mIplSatbMain;
import lombok.*;

import java.util.List;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "ipl_satb_main")
public class IplSatbMain extends mIplSatbMain {

    /**
     * 创建时间 yyyy-MM
     */
    @TableField(exist = false)
    private String createDate;

    /**
     * 附件
     */
    @TableField(exist = false)
    private List<Attachment> attachmentList;

    /**
     * 协同事项集合
     */
    @CommentTarget("协同事项集合")
    @TableField(exist = false)
    private  List<IplAssist> assistList;

    /**
     * 行业名称
     */
    @TableField(exist = false)
    private String industryCategoryTitle;

    /**
     * 需求名称
     */
    @TableField(exist = false)
    private String demandCategoryTitle;

    /**
     * 来源名称
     */
    @TableField(exist = false)
    private String sourceTitle;

    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String statusTitle;
}

