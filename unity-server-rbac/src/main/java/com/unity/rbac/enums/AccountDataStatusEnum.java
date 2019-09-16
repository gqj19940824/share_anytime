package com.unity.rbac.enums;

import lombok.AllArgsConstructor;

/**
 * 冲突账号数据状态
 *
 * @author zhang
 * 生成时间 2019-07-25 18:51:37
 */
@AllArgsConstructor
public enum AccountDataStatusEnum {
    //
    WAIT(10, "待处理"),
    END(20, "已处理");


    public static AccountDataStatusEnum of(Integer id) {
        switch (id) {
            case 10:
                return WAIT;
            case 20:
                return END;
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
        for (AccountDataStatusEnum e : AccountDataStatusEnum.values()) {
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



