package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mInfoDeptYzgt;

import java.util.List;

/**
 * @Author jh
 * */

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "info_dept_yzgt")
public class InfoDeptYzgt extends mInfoDeptYzgt{

    /**
     * 附件集合
     */
    @TableField(exist = false)
     private List<Attachment> attachmentList;

    /**
     * 行业类别名称
     */
    @TableField(exist = false)
     private String industryCategoryName;

    /**
     * 企业规模名称
     */
    @TableField(exist = false)
    private String enterpriseScaleName;

    /**
     * 企业规模名称
     */
    @TableField(exist = false)
    private String enterpriseNatureName;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    private String createTime;

    /**
     * 状态名
     */
    @TableField(exist = false)
    private String statusName;

}

