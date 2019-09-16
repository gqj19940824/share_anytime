package com.unity.rbac.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 资源类型
 * <p>
 * create by gengjiajia at 2018/12/13 11:23
 */
@AllArgsConstructor
public enum ResourceTypeEnum {

    MENU(10,"菜单"),
    BUTTON(20,"按钮"),
    API(30,"接口");

    @Getter @Setter
    private int type;

    @Getter @Setter
    private String name;
}
