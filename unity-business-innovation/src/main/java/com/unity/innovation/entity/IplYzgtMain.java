package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.CommentTarget;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import com.unity.innovation.entity.generated.mIplYzgtMain;

import java.util.List;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_yzgt_main")
public class IplYzgtMain extends mIplYzgtMain{

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
     * 附件集合
     */
    @TableField(exist = false)
    private List<Attachment> attachmentList;


    /**
     * 单位名称
     */
    @TableField(exist = false)
    private String deptName;
        
        
        
        
        
        
        
        
        
        
        
  
}

