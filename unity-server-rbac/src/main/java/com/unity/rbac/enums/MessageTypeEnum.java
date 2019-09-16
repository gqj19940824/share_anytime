package com.unity.rbac.enums;

import lombok.AllArgsConstructor;

/**
 * 消息类型
 * @author creator
 * 生成时间 2019-01-24 19:52:49
 */
@AllArgsConstructor
public enum MessageTypeEnum {

    LOGIN(10, "登录"),
        REGISTRATION(20, "注册"),
        FORGOTTENPASSWORD(30, "找回密码"),
    ;
    
    
    public static MessageTypeEnum of(Integer id) {
        if (id.equals(LOGIN.getId())) {
           return LOGIN;
        }
            if (id.equals(REGISTRATION.getId())) {
           return REGISTRATION;
        }
            if (id.equals(FORGOTTENPASSWORD.getId())) {
           return FORGOTTENPASSWORD;
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



