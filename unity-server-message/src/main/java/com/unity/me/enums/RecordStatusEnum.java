package com.unity.me.enums;

import lombok.AllArgsConstructor;

/**
 * 发送状态
 * @author creator
 * 生成时间 2019-02-12 12:47:36
 */
@AllArgsConstructor
public enum RecordStatusEnum {

    SUCCESS(1, "成功"),
        FAIL(-1, "失败"),
    ;
    
    
    public static RecordStatusEnum of(Integer id) {
        if (id.equals(SUCCESS.getId())) {
           return SUCCESS;
        }
            if (id.equals(FAIL.getId())) {
           return FAIL;
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



