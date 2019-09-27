package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import com.unity.innovation.entity.generated.mIplSupervisionMain;


        
        
        
        
        
        
        
        
        
        
        

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_supervision_main")
public class IplSupervisionMain extends mIplSupervisionMain{

    /**
     * 创建时间
     */
    @TableField(exist = false)
    private String createTime;
  
}

