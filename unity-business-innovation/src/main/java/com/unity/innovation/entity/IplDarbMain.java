package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_darb_main")
public class IplDarbMain extends com.unity.innovation.entity.generated.IplDarbMain {

     @TableField(exist = false)
     private List<Attachment> attachments;

     @TableField(exist = false)
     private String creatTime;

     @TableField(exist = false)
     private String updateTime;


        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
  
}

