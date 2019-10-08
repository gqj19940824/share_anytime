package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * @author JH
 */
@AllArgsConstructor
public enum IplCategoryEnum {
    /**
     *  状态
     * */
    ZMQD(1,"正面清单"),
    FMQD(2,"负面清单"),
    YDQD(3,"引导清单");

    /**
     * 功能描述 根据枚举值返回类型名
     */
    public static String ofName(Integer id) {
        if (ZMQD.getId().equals(id)) {
            return ZMQD.name;
        }
        if (FMQD.getId().equals(id)) {
            return FMQD.getName();
        }
        if (YDQD.getId().equals(id)) {
            return YDQD.getName();
        }
        return null;
    }

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }

}
