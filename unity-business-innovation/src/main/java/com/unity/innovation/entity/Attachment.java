package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mAttachment;
import lombok.*;

import java.util.List;

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "sys_attachment")
public class Attachment extends mAttachment {


    /**
     * 附件 CODE 列表
     */
    @TableField(exist = false)
    private List<String> attachmentCodeList;

}

