package com.unity.me.enums;

import lombok.AllArgsConstructor;

/**
 * 业务模块类型
 * @author creator
 * 生成时间 2019-03-04 15:58:24
 */
@AllArgsConstructor
public enum BizModelEnum {

    ANNOUNCEMENT(201030, "公告"),
    MEMBERSHIP(212000, "党费"),
    ACTIVITY(506000, "活动"),
    LEARNING(204000, "学习天地"),
    NEWSFLASH(213000, "快报"),
    SCHOOL(400000, "亦党校"),
    EXAM(408020, "专题测评"),
    PUSH_MESSAGE(201020, "推送消息"),
    ;


    public static BizModelEnum of(Integer id) {
        if (id.equals(ANNOUNCEMENT.getId())) {
            return ANNOUNCEMENT;
        }
        if (id.equals(MEMBERSHIP.getId())) {
            return MEMBERSHIP;
        }
        if (id.equals(ACTIVITY.getId())) {
            return ACTIVITY;
        }
        if (id.equals(LEARNING.getId())) {
            return LEARNING;
        }
        if (id.equals(NEWSFLASH.getId())) {
            return NEWSFLASH;
        }
        if (id.equals(SCHOOL.getId())) {
            return SCHOOL;
        }
        if (id.equals(EXAM.getId())) {
            return EXAM;
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



