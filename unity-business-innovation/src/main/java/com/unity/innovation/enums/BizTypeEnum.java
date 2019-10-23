package com.unity.innovation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * create by qinhuan at 2019/10/23 1:46 下午
 */
@AllArgsConstructor
public enum BizTypeEnum {

    /**
     * 10城市创新合作(发改局) 20企业创新发展（企服局） 30成长目标投资（科技局） 40高端才智需求（组织部） 20亲清政商关系（纪检）
     */
    CITY(10),
    ENTERPRISE(20),
    GROW(30),
    INTELLIGENCE(40),
    POLITICAL(50),
    RQDEPTINFO(60),
    LYDEPTINFO(70),
    INVESTMENT(80);

    @Getter
    private Integer type;
}
