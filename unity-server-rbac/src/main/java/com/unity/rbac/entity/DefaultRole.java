package com.unity.rbac.entity;

import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import com.unity.rbac.entity.generated.mDefaultRole;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "rbac_m_default_role")
public class DefaultRole extends mDefaultRole{
        
        
        
        
        
        
        
        
        
        
        
  
}

