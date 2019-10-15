package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mInfoDeptSatb;
import lombok.*;

        

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "info_dept_satb")
public class InfoDeptSatb extends mInfoDeptSatb{

    /**
     * 行业类型名称
     */
    @TableField(exist = false)
    private String industryCategoryName;
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
  
}

