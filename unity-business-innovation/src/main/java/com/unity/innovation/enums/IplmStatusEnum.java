package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * @author zhqgeng
 * @create 2019-09-17 19:54
 */
@AllArgsConstructor
public enum IplmStatusEnum {
    /**
     *  类型 1：工作类别 2：关键字 3：产业类型 4：需求类型
     * */
    ONE(1,"工作类别"),
    TWO(2,"关键字"),
    THREE(3,"产业类型"),
    FOUR(4,"需求类型")
    ;

//    /**
//     * 功能描述 根据枚举值返回类型
//     * @param id 枚举值
//     * @return com.unity.safety.enums.AccidentLevelEnum 对应的类型
//     * @author gengzhiqiang
//     * @date 2019/7/11 21:29
//     */
//    public static String ofName(Integer id) {
//        if (id.equals(TWO.getId())) {
//            return TWO.name;
//        }
//        if (id.equals(ONE.getId())) {
//            return ONE;
//        }
//        if (id.equals(THREE.getId())) {
//            return THREE;
//        }
//        if (id.equals(FOUR.getId())) {
//            return FOUR;
//        }
//
//        return null;
//    }

    private Integer id;


    private String name;

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }

}
