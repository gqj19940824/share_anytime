package com.unity.me.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.me.entity.generated.mMessageReciver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "me_e_message_reciver")
public class MessageReciver extends mMessageReciver{
        
        
        
        
        
        
        
        
        
        
  
}

