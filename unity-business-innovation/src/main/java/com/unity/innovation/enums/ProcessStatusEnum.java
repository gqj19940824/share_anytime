package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 数据状态
 *
 * @author zhang
 * 生成时间 2019-07-30 14:59:44
 */
@AllArgsConstructor
public enum ProcessStatusEnum {
    deal_overtime(1, "超时未开始处理"),
    update_overtime(2, "超时未更新"),
    normal(3, "进展正常");

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}



