package com.unity.innovation.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * create by qinhuan at 2019/10/23 1:46 下午
 */
@AllArgsConstructor
public enum BizTypeEnum {

    /**
     * 10城市创新合作(发改局) 20企业创新发展（企服局） 30成长目标投资（科技局） 40高端才智需求（组织部）
     * 50亲清政商关系（纪检 ) 60入区企业信息发布管理  70路演企业信息发布管理  80投资机构信息发布管理
     * 90意见与建议（纪检组）
     */
    CITY(10,"城市创新合作"), // 发展局
    ENTERPRISE(20,"企业创新发展"), // 科技局
    GROW(30,"成长目标投资"), // 科技局
    INTELLIGENCE(40,"高端才智需求"), // 组织部
    POLITICAL(50,"亲清政商关系"),
    RQDEPTINFO(60,"入区企业信息"),
    LYDEPTINFO(70,"路演企业信息"),
    INVESTMENT(80,"投资机构信息"),
    SUGGESTION(90,"意见与建议"),
    SIGNUP(100,"报名参与发布会");

    public static String ofName(Integer bizType){
        String name = "";
        switch (BizTypeEnum.of(bizType)){
            case CITY:
                name = BizTypeEnum.CITY.name;
                break;
            case ENTERPRISE:
                name = BizTypeEnum.ENTERPRISE.name;
                break;
            case GROW:
                name = BizTypeEnum.GROW.name;
                break;
            case INTELLIGENCE:
                name = BizTypeEnum.INTELLIGENCE.name;
                break;
            case POLITICAL:
                name = BizTypeEnum.POLITICAL.name;
                break;
            case INVESTMENT:
                name = BizTypeEnum.INVESTMENT.name;
                break;
            case LYDEPTINFO:
                name = BizTypeEnum.LYDEPTINFO.name;
                break;
            case RQDEPTINFO:
                name = BizTypeEnum.RQDEPTINFO.name;
                break;

        }
        return name;
    }

    public static BizTypeEnum of(Integer id) {

        if (id.equals(CITY.getType())) {
            return CITY;
        }
        if (id.equals(ENTERPRISE.getType())) {
            return ENTERPRISE;
        }
        if (id.equals(GROW.getType())) {
            return GROW;
        }
        if (id.equals(INTELLIGENCE.getType())) {
            return INTELLIGENCE;
        }
        if (id.equals(POLITICAL.getType())) {
            return POLITICAL;
        }
        if (id.equals(RQDEPTINFO.getType())) {
            return RQDEPTINFO;
        }
        if (id.equals(LYDEPTINFO.getType())) {
            return LYDEPTINFO;
        }
        if (id.equals(INVESTMENT.getType())) {
            return INVESTMENT;
        }
        if (id.equals(SIGNUP.getType())) {
            return SIGNUP;
        }
        if (id.equals(SUGGESTION.getType())) {
            return SUGGESTION;
        }
        return null;
    }

    @Getter @JsonValue
    private Integer type;
    @Getter @JsonValue
    private String name;

    public Map<String, Object> toMap(){
        Map map = new HashMap<Integer, String>();
        map.put("type", type);
        map.put("name", name);
        return map;
    }
}
