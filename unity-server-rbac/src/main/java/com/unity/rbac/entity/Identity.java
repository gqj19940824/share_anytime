package com.unity.rbac.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

import com.unity.rbac.entity.generated.mIdentity;
import lombok.EqualsAndHashCode;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@TableName(value = "rbac_identity")
@EqualsAndHashCode(callSuper=false)
public class Identity extends mIdentity{
  
  
}

