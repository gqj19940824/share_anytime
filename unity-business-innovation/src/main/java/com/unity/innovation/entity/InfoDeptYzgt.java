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

}

