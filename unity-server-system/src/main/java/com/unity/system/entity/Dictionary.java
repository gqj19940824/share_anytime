package com.unity.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

import com.unity.system.entity.generated.mDictionary;
import lombok.EqualsAndHashCode;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "sys_dictionary")
public class   Dictionary extends mDictionary{
  
  
}

