package com.unity.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.rbac.entity.generated.mDepartment;
import lombok.*;


/**
 * @author gengjiajia
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "rbac_department")
@EqualsAndHashCode(callSuper=false)
public class Department extends mDepartment{

    /**
     * 0:上移 1：下移
     */
    @TableField(exist = false)
    private Integer up;

}

