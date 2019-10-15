package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * @author zhqgeng
 * @create 2019-09-17 19:54
 */
@AllArgsConstructor
public enum SysCfgEnum {
    /**
     *  类型 1：工作类别 2：关键字 3：产业类型 4：需求类型
     * */
    ONE(1,"工作类别"),
    TWO(2,"关键字"),
    THREE(3,"产业类型"),
    FOUR(4,"需求类型"),
    FIVE(5,"需求名目"),
    SIX(6,"企业性质")
    ;

    /**
     * 功能描述 根据枚举值返回类型
     * @param id 枚举值
     * @return com.unity.safety.enums.AccidentLevelEnum 对应的类型
     * @author gengzhiqiang
     * @date 2019/7/11 21:29
     */
    public static SysCfgEnum of(Integer id) {
        if (id.equals(TWO.getId())) {
            return TWO;
        }
        if (id.equals(ONE.getId())) {
            return ONE;
        }
        if (id.equals(THREE.getId())) {
            return THREE;
        }
        if (id.equals(FOUR.getId())) {
            return FOUR;
        }
        if (id.equals(FIVE.getId())) {
            return FIVE;
        }
        if (id.equals(SIX.getId())) {
            return SIX;
        }
        return null;
    }

    /**
     * 判断值是否在枚举中存在
     * @param id 枚举值id
     * @return true：存在 false：不存在
     */
    public static boolean exist(int id){
        boolean flag = false;
        for (SysCfgEnum e: SysCfgEnum.values()){
            if(e.getId()==id){
                flag = true;
                break;
            }
        }
        return flag;
    }


    private Integer id;


    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
