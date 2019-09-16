package com.unity.rbac.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 默认角色
 * <p>
 * create by gengjiajia at 2018/12/13 11:23
 */
@AllArgsConstructor
public enum DefaultRoleEnum {

    PC(1L,"PC默认角色"),
    ANDROID(2L,"安卓默认角色"),
    IOS(3L,"IOS默认角色"),
    WX(4L,"微信默认角色"),
    APPLETS(5L,"小程序默认角色"),
    SYSTEM(6L,"后台默认角色"),
    ROOT(7L,"后台系统管理员"),
    INSIDE(8L,"内部用户默认角色"),
    OUTER(9L,"外部用户默认角色");

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String name;
}
