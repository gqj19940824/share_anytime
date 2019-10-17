package com.unity.innovation.enums;

import com.unity.common.constant.InnovationConstant;
import lombok.AllArgsConstructor;

/**
 * 是否提请枚举
 * @author JH
 * */
@AllArgsConstructor
public enum InfoTypeEnum {
    /**
     * 未提请发布
     * */
    DEPARTMENT_YZGT(InnovationConstant.DEPARTMENT_YZGT_ID,"入区企业信息"),
    /**
     * 已提请发布
     * */
    DEPARTMENT_SATB(InnovationConstant.DEPARTMENT_SATB_ID,"路演企业信息"),;

    /**
     * 值
     * */
    private Long id;
    /**
     * 名称
     * */
    private String name;

    public static InfoTypeEnum of(Long id) {
        if (id.equals(DEPARTMENT_YZGT.getId())) {
            return DEPARTMENT_YZGT;
        } else {
            return DEPARTMENT_SATB;
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
