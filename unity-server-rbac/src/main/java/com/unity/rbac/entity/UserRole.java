package com.unity.rbac.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

import com.unity.rbac.entity.generated.mUserRole;
import lombok.EqualsAndHashCode;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@TableName(value = "rbac_m_user_role")
@EqualsAndHashCode(callSuper=false)
public class UserRole extends mUserRole{
  
  
}

