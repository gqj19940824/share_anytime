package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.base.CommentTarget;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.mIplEsbMain;
import com.unity.innovation.enums.BizTypeEnum;
import lombok.*;

import java.util.List;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_esb_main")
public class IplEsbMain extends mIplEsbMain{


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
     * 行业类型名称
     */
    @TableField(exist = false)
    private String industryCategoryName;



    /**
     * 协同事项集合
     */
    @CommentTarget("协同事项集合")
    @TableField(exist = false)
    private  List<IplAssist> assistList;

    /**
     * 新产品和新技术
     **/
    @TableField(exist = false)
    private String newProductAndTech ;

    @TableField(exist = false)
    private Integer bizType = BizTypeEnum.ENTERPRISE.getType();

    /**
     * 单位名称
     */
    @TableField(exist = false)
    private String idRbacDepartmentName;
}

