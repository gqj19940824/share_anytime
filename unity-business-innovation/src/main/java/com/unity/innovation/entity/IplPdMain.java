package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mIplPdMain;
import lombok.*;

import java.util.List;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "ipl_pd_main")
public class IplPdMain extends mIplPdMain {

    /**
     * 创建时间 yyyy-MM
     */
    @TableField(exist = false)
    private String createDate;

    /**
     * 附件
     */
    @TableField(exist = false)
    private List<Attachment> attachments;
}

