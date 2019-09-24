package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 是否发送枚举
 * @author JH
 * */
@AllArgsConstructor
public enum IsSendEnum {
    /**
     * 草稿
     * */
    DRAFT(0, "草稿"),
    /**
     * 已发送
     * */
    SENDED(1, "已发送");

    /**
     * 值
     * */
    private Integer id;
    /**
     * 名称
     * */
    private String name;

    public static IsSendEnum of(Integer id) {
        if (id.equals(DRAFT.getId())) {
            return DRAFT;
        } else {
            return SENDED;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
