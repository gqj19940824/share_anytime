package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 是否浏览枚举
 * @author JH
 * */
@AllArgsConstructor
public enum IsReadEnum {
    /**
     * 未浏览
     * */
    READ(0, "未浏览"),
    /**
     * 已浏览
     * */
    NOREAD(1, "已浏览");
    /**
     * 值
     * */
    private Integer id;
    /**
     * 名称
     * */
    private String name;

    public static IsReadEnum of(Integer id) {
        if (id.equals(READ.getId())) {
            return READ;
        } else {
            return NOREAD;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
