package com.unity.me.enums;

import lombok.AllArgsConstructor;

/**
 * 发送类型
 * @author creator
 * 生成时间 2019-02-12 12:47:36
 */
@AllArgsConstructor
public enum PushTypeEnum {

    UNICAST(1, "单播"),
        LISTCAST(2, "列播"),
        FILECAST(3, "文件播"),
        BROADCAST(4, "广播"),
        GROUPCAST(5, "组播"),
        CUSTOMIZEDCAST(6, "自定义播"),
    ;
    
    
    public static PushTypeEnum of(Integer id) {
        if (id.equals(UNICAST.getId())) {
           return UNICAST;
        }
            if (id.equals(LISTCAST.getId())) {
           return LISTCAST;
        }
            if (id.equals(FILECAST.getId())) {
           return FILECAST;
        }
            if (id.equals(BROADCAST.getId())) {
           return BROADCAST;
        }
            if (id.equals(GROUPCAST.getId())) {
           return GROUPCAST;
        }
            if (id.equals(CUSTOMIZEDCAST.getId())) {
           return CUSTOMIZEDCAST;
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



