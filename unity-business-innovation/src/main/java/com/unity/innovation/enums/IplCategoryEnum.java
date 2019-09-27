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
    FMQD(2,"处理中"),
    YDQD(3,"处理完毕");

    /**
     * 功能描述 根据枚举值返回类型名
     * @param id 枚举值
     * @return com.unity.safety.enums.AccidentLevelEnum 对应的类型
     * @author qinh
     * @date 2019/7/11 21:29
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
