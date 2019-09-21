package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.base.CommentTarget;
import com.unity.innovation.entity.generated.mDailyWorkStatus;
import lombok.*;

import java.util.List;

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "daily_work_status")
public class DailyWorkStatus extends mDailyWorkStatus {

    /**
     * 关键字
     */
    @TableField(exist = false)
    private Long keyWord;

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
     * 关键字拼接
     */
    @TableField(exist = false)
    private String keyWordStr;

    /**
     * 工作类别名称
     */
    @TableField(exist = false)
    private String typeName;


    /**
     * 附件集合
     */
    @TableField(exist = false)
    private List<Attachment> attachmentList;

    /**
     * 关键字主键集合
     */
    @CommentTarget("关键字集合")
    @TableField(exist = false)
    private List<Long> keyWordList;

    /**
     * 单位名称
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 包id
     */
    @TableField(exist = false)
    private String idPackage;

}


