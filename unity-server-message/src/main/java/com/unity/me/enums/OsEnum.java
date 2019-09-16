package com.unity.me.enums;

import lombok.AllArgsConstructor;

/**
 * 操作系统
 * @author creator
 * 生成时间 2019-02-12 12:47:36
 */
@AllArgsConstructor
public enum OsEnum {

    ANDROID(1, "安卓"),
        IOS(2, "iOS"),
    ;
    
    
    public static OsEnum of(Integer id) {
        if (id.equals(ANDROID.getId())) {
           return ANDROID;
        }
            if (id.equals(IOS.getId())) {
           return IOS;
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



