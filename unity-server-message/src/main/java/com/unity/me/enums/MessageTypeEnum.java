package com.unity.me.enums;

import lombok.AllArgsConstructor;

/**
 * 消息类型
 *
 * @author creator
 * 生成时间 2019-01-24 19:52:49
 */
@AllArgsConstructor
public enum MessageTypeEnum {

    LOGIN(10, "登录"),
    REGISTRATION(20, "注册"),
    FORGOTTENPASSWORD(30, "找回密码"),
    ENTERPRISEWHISTLE(40, "哨声通知"),
    ENTERPRISEWHISTLES(50, "哨声批量通知"),;


    public static MessageTypeEnum of(Integer id) {
        switch (id){
            case 10:
                return LOGIN;
            case 20:
                return REGISTRATION;
            case 30:
                return FORGOTTENPASSWORD;
            case 40:
                return ENTERPRISEWHISTLE;
            case 50:
                return ENTERPRISEWHISTLES;
            default:
                return null;
        }
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



