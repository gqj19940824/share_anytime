package com.unity.me.enums;

import lombok.AllArgsConstructor;

/**
 * 聊天方式
 * @author creator
 * 生成时间 2019-02-12 12:47:33
 */
@AllArgsConstructor
public enum WayEnum {

    SINGLE(1, "单聊"),
        GROUP(2, "群聊"),
    ;
    
    
    public static WayEnum of(Integer id) {
        if (id.equals(SINGLE.getId())) {
           return SINGLE;
        }
            if (id.equals(GROUP.getId())) {
           return GROUP;
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



