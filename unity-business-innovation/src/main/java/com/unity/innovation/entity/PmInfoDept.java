package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.CommentTarget;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mPmInfoDept;

import java.util.List;

/**
 * @author JH
 * */

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "pm_info_dept")
public class PmInfoDept extends mPmInfoDept{

    /**
     * 提交时间
     */
    @TableField(exist = false)
    private String submitTime;

    /**
     * 模块标识
     **/
    @CommentTarget("模块标识")
    @TableField(exist = false)
    private String category;

    /**
     * 数据集合
     **/
    @CommentTarget("数据集合")
    @TableField(exist = false)
    private List<Long> dataIdList;

    /**
     * 附件集合
     */
    @TableField(exist = false)
    private List<Attachment> attachmentList;


  
}

