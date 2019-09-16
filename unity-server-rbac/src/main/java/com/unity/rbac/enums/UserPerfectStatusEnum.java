package com.unity.rbac.enums;

import lombok.AllArgsConstructor;

/**
 * 用户信息完善状态
 *
 * @author geng
 * 生成时间 2019-07-25 18:51:37
 */
@AllArgsConstructor
public enum UserPerfectStatusEnum {
    //
    UNPROCESSED(0, "待完善"),
    PROCESSED(1, "已完善");


    public static UserPerfectStatusEnum of(Integer id) {
        switch (id) {
            case 0:
                return UNPROCESSED;
            case 1:
                return PROCESSED;
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
        for (UserPerfectStatusEnum e : UserPerfectStatusEnum.values()) {
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



