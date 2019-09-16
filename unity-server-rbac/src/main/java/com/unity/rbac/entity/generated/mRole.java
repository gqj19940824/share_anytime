package com.unity.rbac.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 角色
 *
 * @author creator
 * 生成时间 2019-01-11 17:16:19
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class mRole extends BaseEntity {


    /**
     * 角色名称
     **/
    @TableField("name")
    private String name;

    /**
     * 是否系统预制:flag:1 是,0 否
     **/
    @TableField("is_default")
    private Integer isDefault;

    public mRole() {
    }
}




