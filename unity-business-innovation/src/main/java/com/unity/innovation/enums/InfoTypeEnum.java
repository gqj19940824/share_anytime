package com.unity.innovation.enums;

import com.unity.common.constant.InnovationConstant;
import lombok.AllArgsConstructor;

/**
 * 是否提请枚举
 *
 * @author JH
 */
@AllArgsConstructor
public enum InfoTypeEnum {

    /**
     * 投资机构信息管理
     */
    DEPARTMENT_INVEST(InnovationConstant.DEPARTMENT_YZGT_ID, "投资机构信息管理", 10),

    /**
     * 入区企业信息
     */
    DEPARTMENT_YZGT(InnovationConstant.DEPARTMENT_YZGT_ID, "入区企业信息", 20),

    /**
     * 路演企业信息
     */
    DEPARTMENT_SATB(InnovationConstant.DEPARTMENT_SATB_ID, "路演企业信息", 30),;

    /**
     * 值
     */
    private Long id;
    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private int type;

    public static InfoTypeEnum of(Long id) {
        if (id.equals(DEPARTMENT_YZGT.getId())) {
            return DEPARTMENT_YZGT;
        } else if (id.equals(DEPARTMENT_INVEST.getId())) {
            return DEPARTMENT_INVEST;
        } else {
            return DEPARTMENT_SATB;
        }
    }

    public static InfoTypeEnum infoByType(int type) {
        if (type == DEPARTMENT_INVEST.getType()) {
            return DEPARTMENT_INVEST;
        } else if (type == DEPARTMENT_YZGT.getType()) {
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

    public int getType() {
        return type;
    }

}
