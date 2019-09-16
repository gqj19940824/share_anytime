package com.unity.me.enums;

import lombok.AllArgsConstructor;

/**
 * 业务类型
 *
 * @author creator
 * 生成时间 2019-03-04 15:58:24
 */
@AllArgsConstructor
public enum BizTypeEnum {

    PUSH(1, "推送"),
    ANNOUNCEMENT(2, "公告"),
    OTHER(3, "其它");


    public static BizTypeEnum of(Integer id) {
        if (id.equals(PUSH.getId())) {
            return PUSH;
        }
        if (id.equals(ANNOUNCEMENT.getId())) {
            return ANNOUNCEMENT;
        }
        if (id.equals(OTHER.getId())) {
            return OTHER;
        }
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



