package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * <p>
 * create by qinhuan at 2019/10/23 1:46 下午
 */
@AllArgsConstructor
public enum BizTypeEnum {

    /**
     * 10城市创新合作 20企业创新发展 30成长目标投资 40高端才智需求 20亲清政商关系
     */
    CITY(10),
    ENTERPRISE(20),
    GROW(30),
    INTELLIGENCE(40),
    POLITICAL(50);

    private Integer type;

    public static BizTypeEnum of(Integer type) {
        if (type.equals(CITY.getType())) {
            return CITY;
        } else if (type.equals(ENTERPRISE.getType())) {
            return ENTERPRISE;
        } else if (type.equals(GROW.getType())) {
            return GROW;
        } else if (type.equals(INTELLIGENCE.getType())) {
            return INTELLIGENCE;
        } else {
            return POLITICAL;
        }
    }

    public int getType() {
        return type;
    }

}
