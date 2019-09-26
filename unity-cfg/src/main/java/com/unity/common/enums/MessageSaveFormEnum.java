package com.unity.common.enums;

import lombok.AllArgsConstructor;

/**
 * 消息存储方式
 *
 * @author zhang
 * 生成时间 2019-07-30 14:59:44
 */
@AllArgsConstructor
public enum MessageSaveFormEnum {
    //d
    SYS_MSG(1, "SysMessage"),
    NOTICE(2, "Notice");

    public static MessageSaveFormEnum of(Integer id) {
        //s
        switch (id) {
            case 1:
                return SYS_MSG;
            case 2:
                return NOTICE;
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
        for (MessageSaveFormEnum e : MessageSaveFormEnum.values()) {
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



