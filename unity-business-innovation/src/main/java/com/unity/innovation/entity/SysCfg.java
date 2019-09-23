package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.CommentTarget;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mSysCfg;

import java.util.List;

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
     * 适用范围 0 代表全部  其余代表单位id
     **/
    @CommentTarget("适用范围")
    @TableField(exist = false)
    private List<Long> scope ;

    /**
     * 适用范围 0 代表全部  其余代表单位id
     **/
    @TableField(exist = false)
    private List<SysCfgScope> scopeList ;

}

