package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 是否提请枚举
 * @author JH
 * */
@AllArgsConstructor
public enum IsCommitEnum {
    /**
     * 未提请发布
     * */
    NO(0, "未提请发布"),
    /**
     * 已提请发布
     * */
    YES(1, "已提请发布");

    /**
     * 值
     * */
    private Integer id;
    /**
     * 名称
     * */
    private String name;

    public static IsCommitEnum of(Integer id) {
        if (id.equals(NO.getId())) {
            return NO;
        } else {
            return YES;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
