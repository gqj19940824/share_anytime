package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 数据状态
 *
 * @author qinhuan
 * 生成时间 2019-07-30 14:59:44
 */
@AllArgsConstructor
public enum ProcessStatusEnum {
    DEAL_OVERTIME(1, "超时未开始处理"),
    UPDATE_OVERTIME(2, "超时未更新"),
    NORMAL(3, "进展正常");

    /**
     * 功能描述 根据枚举值返回类型名
     * @param id 枚举值
     * @return com.unity.safety.enums.AccidentLevelEnum 对应的类型
     * @author qinh
     * @date 2019/7/11 21:29
     */
    public static String ofName(Integer id) {
        if (DEAL_OVERTIME.getId().equals(id)) {
            return DEAL_OVERTIME.name;
        }
        if (UPDATE_OVERTIME.getId().equals(id)) {
            return UPDATE_OVERTIME.getName();
        }
        if (NORMAL.getId().equals(id)) {
            return NORMAL.getName();
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



