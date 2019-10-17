package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mPmInfoDept;

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
    @TableField(exist = false)
    private String category;


  
}

