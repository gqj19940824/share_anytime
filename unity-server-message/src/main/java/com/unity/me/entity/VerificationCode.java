package com.unity.me.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import com.unity.me.entity.generated.mVerificationCode;
import org.apache.ibatis.annotations.Select;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "me_verification_code")
public class VerificationCode extends mVerificationCode{


    /**
     * 短信填充内容
     */
    @TableField(exist = false)
    private String[] content;
        
        
        
        
        
        
        
        
        
        
        
        
  
}

