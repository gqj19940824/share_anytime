package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mSysCfg;

/**
 * @author jh
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "sys_cfg")
public class SysCfg extends mSysCfg{

    /**
     * 公司名称
     * */
    @TableField(exist = false)
    private String departmentName;
        

}

