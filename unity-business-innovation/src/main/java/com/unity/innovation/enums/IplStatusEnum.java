package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * @author qinhuan
 * @create 2019-09-17 19:54
 */
@AllArgsConstructor
public enum IplStatusEnum {
    /**
     *  状态
     * */
    UNDEAL(1,"待处理"),
    DEALING(2,"处理中"),
    DONE(3,"处理完毕");

    /**
     * 功能描述 根据枚举值返回类型名
     * @param id 枚举值
     * @return com.unity.safety.enums.AccidentLevelEnum 对应的类型
     * @author qinh
     * @date 2019/7/11 21:29
     */
    public static String ofName(Integer id) {
        if (UNDEAL.getId().equals(id)) {
            return UNDEAL.name;
        }
        if (DEALING.getId().equals(id)) {
            return DEALING.getName();
        }
        if (DONE.getId().equals(id)) {
            return DONE.getName();
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
