package com.unity.me.enums;

import lombok.AllArgsConstructor;

/**
 * 聊天内容类型
 * @author creator
 * 生成时间 2019-02-12 12:47:33
 */
@AllArgsConstructor
public enum MsgTypeEnum {

    TEXT(1, "文字"),
        REDIO(2, "音频"),
        PICTURE(3, "图片"),
    ;
    
    
    public static MsgTypeEnum of(Integer id) {
        if (id.equals(TEXT.getId())) {
           return TEXT;
        }
            if (id.equals(REDIO.getId())) {
           return REDIO;
        }
            if (id.equals(PICTURE.getId())) {
           return PICTURE;
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



