package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mIplYzgtMain;
import java.util.List;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_yzgt_main")
public class IplYzgtMain extends mIplYzgtMain{

    /**
     * 创建时间 yyyy-MM
     */
    @TableField(exist = false)
    private String createDate;


    /**
     * 附件集合
     */
    @TableField(exist = false)
    private List<Attachment> attachmentList;


    /**
     * 来源名称
     */
    @TableField(exist = false)
    private String sourceTitle;
    /**
     * 行业类别名称
     */
    @TableField(exist = false)
    private String industryCategoryTitle;

    /**
     * 行业性质名称
     */
    @TableField(exist = false)
    private String enterpriseNatureTitle;

    /**
     * 行业规模名称
     */
    @TableField(exist = false)
    private String enterpriseScaleTitle;

    /**
     * 行业属地名称
     */
    @TableField(exist = false)
    private String enterpriseLocationTitle;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    private String createTime;
    /**
     * 修改时间
     */
    @TableField(exist = false)
    private String modifiedTime;
    /**
     * 附件url
     */
    @TableField(exist = false)
    private String attachmentUrl;


}

