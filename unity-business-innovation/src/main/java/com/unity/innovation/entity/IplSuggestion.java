package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.base.CommentTarget;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.mIplSuggestion;
import lombok.*;

import java.util.List;

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_suggestion")
public class IplSuggestion extends mIplSuggestion{

    /**
     * 更新时间
     */
    @TableField(exist = false)
    private String modifiedTime;

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
     * 日志
     */
    @TableField(exist = false)
    private List<IplLog> iplLogList;

    /**
     * 数据集合
     */
    @TableField(exist = false)
    private List<IplAssist> logList;



    /**
     * 日志名称
     */
    @TableField(exist = false)
    private String logEnterpriseName;

    /**
     * 处理进展
     */
    @CommentTarget("处理进展")
    @TableField(exist = false)
    private String processMessage;


    /**
     * 日志名称
     */
    @TableField(exist = false)
    private String deptName;

}

