package com.unity.rbac.enums;

import lombok.AllArgsConstructor;

/**
 * 来源
 *
 * @author zhang
 * 生成时间 2019-07-25 18:51:37
 */
@AllArgsConstructor
public enum UcsSourceEnum {
    //
    OA(10, "OA"),
    ASSETS(20, "资产"),
    SAFE(30, "安全"),
    PROJECT(40, "工程"),
    INNOVATION(50,"创新");


    public static UcsSourceEnum of(Integer id) {
        switch (id) {
            case 10:
                return OA;
            case 20:
                return ASSETS;
            case 30:
                return SAFE;
            case 40:
                return PROJECT;
            case 50:
                return INNOVATION;
            default:
                return null;
        }
    }

    /**
     * 判断值是否在枚举中存在
     *
     * @param id
     * @return
     */
    public static boolean exist(int id) {
        boolean flag = false;
        for (UcsSourceEnum e : UcsSourceEnum.values()) {
            if (e.getId() == id) {
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



