package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 数据来源
 *
 * @author zhang
 * 生成时间 2019-07-30 14:59:44
 */
@AllArgsConstructor
public enum SourceEnum {
    ENTERPRISE(1, "企业"),
    SELF(2, "各局");

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}



