package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * <p>
 * create by qinhuan at 2019/10/23 1:46 下午
 */
@AllArgsConstructor
public enum BizTypeEnum {

    /**
     * 10城市创新合作
     * 20企业创新发展
     * 30成长目标投资
     * 40高端才智需求
     * 50亲清政商关系
     * 60入区企业信息发布管理
     * 70路演企业信息发布管理
     * 80投资机构信息发布管理
     */
    CITY(10),
    ENTERPRISE(20),
    GROW(30),
    INTELLIGENCE(40),
    POLITICAL(50),
    RQDEPTINFO(60),
    LYDEPTINFO(70),
    INVESTMENT(80);

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
        } else if(type.equals(POLITICAL.getType())) {
            return POLITICAL;
        } else if(type.equals(RQDEPTINFO.getType())) {
            return RQDEPTINFO;
        } else if(type.equals(LYDEPTINFO.getType())) {
            return LYDEPTINFO;
        } else {
            return INVESTMENT;
        }

    }

    public int getType() {
        return type;
    }

}
