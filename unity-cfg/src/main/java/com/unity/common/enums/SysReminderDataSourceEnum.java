package com.unity.common.enums;

import lombok.AllArgsConstructor;

/**
 * 数据来源
 *
 * @author zhang
 * 生成时间 2019-07-30 14:59:44
 */
@AllArgsConstructor
public enum SysReminderDataSourceEnum {
    //d
    PUBLIC_NOTICE(1, "通知公告"),
    INVESTIGATION_CHANG(2, "隐患整改"),
    EMER_DUTY(3, "应急值班"),
    SPEC_EQUIPMENT(4, "特种设备管理"),
    ACCOUNT_SYNC_DEP(5, "账号同步"),
    ACCOUNT_SYNC_ROLE(6, "账号同步"),
    ACCOUNT_CONFLICT(7, "账号冲突"),
    ENV_EQUIPMENT(8, "环保设备管理");

    public static SysReminderDataSourceEnum of(Integer id) {
        //s
        switch (id) {
            case 1:
                return PUBLIC_NOTICE;
            case 2:
                return INVESTIGATION_CHANG;
            case 3:
                return EMER_DUTY;
            case 4:
                return SPEC_EQUIPMENT;
            case 5:
                return ACCOUNT_SYNC_DEP;
            case 6:
                return ACCOUNT_SYNC_ROLE;
            case 7:
                return ACCOUNT_CONFLICT;
            case 8:
                return ENV_EQUIPMENT;
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
        for (SysReminderDataSourceEnum e : SysReminderDataSourceEnum.values()) {
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



