package com.unity.system.enums;

import lombok.AllArgsConstructor;

/**
 * 业务模块类型
 *
 * @author creator
 * 生成时间 2019-03-04 15:58:24
 */
@AllArgsConstructor
public enum SystemTypeEnum {

    ANDROID(2, "android"),
    IOS(3, "ios");


    //系统类型:status:2 android,3 ios
    public static SystemTypeEnum of(Integer id) {
        if (id.equals(ANDROID.getId())) {
            return ANDROID;
        }
        if (id.equals(IOS.getId())) {
            return IOS;
        }
        ;
        return null;
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



