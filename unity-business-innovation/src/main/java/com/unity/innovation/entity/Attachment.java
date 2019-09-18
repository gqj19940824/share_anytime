package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mAttachment;
import lombok.*;

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "sys_attachment")
public class Attachment extends mAttachment {



}

