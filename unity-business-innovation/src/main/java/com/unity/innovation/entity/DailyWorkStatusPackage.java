package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.base.CommentTarget;
import com.unity.innovation.entity.generated.mDailyWorkStatusPackage;
import lombok.*;

import java.util.List;
import java.util.Map;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "daily_work_status_package")
public class DailyWorkStatusPackage extends mDailyWorkStatusPackage {


    /**
     * 提交时间查询
     */
    @TableField(exist = false)
    private String submitTime;

    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String stateName;

    /**
     * 内容
     */
    @CommentTarget("内容")
    @TableField(exist = false)
    private List<Long> workStatusList;

    /**
     * 附件集合
     */
    @TableField(exist = false)
    private List<Attachment> attachmentList;

    /**
     * 数据集合
     */
    @CommentTarget("数据")
    @TableField(exist = false)
    private List<DailyWorkStatus> dataList;

    /**
     * 日志集合
     */
    @TableField(exist = false)
    private List<DailyWorkStatusLog> logList;

    /**
     * 流程节点
     */
    @TableField(exist = false)
    private Map<Integer, DailyWorkStatusLog> processNode;


    /**
     * 通过驳回
     */
    @TableField(exist = false)
    private Integer flag;

    /**
     * 审核意见
     **/
    @CommentTarget("审核意见")
    @TableField(exist = false)
    private String comment ;

    /**
     * 单位名称
     */
    @TableField(exist = false)
    private String deptName;

}

