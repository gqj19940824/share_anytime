package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 消息发送类型
 *
 * @author G
 * 生成时间 2019-09-23 09:39:17
 */
@AllArgsConstructor
public enum SysMessageSendTypeEnum {
    /** * */
    ONE(1, "点对点"),
    MANY(2, "广播");


    public static SysMessageSendTypeEnum of(Integer id) {
        switch (id) {
            case 1 : return ONE;
            case 2 : return MANY;
            default : return null;
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
        for (SysMessageSendTypeEnum e : SysMessageSendTypeEnum.values()) {
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



