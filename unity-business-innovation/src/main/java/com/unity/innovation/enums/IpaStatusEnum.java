package com.unity.innovation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author qihuan
 * @create 2019-09-17 19:54
 */
@AllArgsConstructor
public enum IpaStatusEnum {

    UNCOMMIT(10,"待提交"),
    UNAUDIT(20,"待审核"),
    UNPUBLISH(30,"待发布"),
    REJECTED(40,"已驳回"),
    UNUPDATE(50, "待更新发布结果"),
    UPDATED(60, "已更新发布结果");

    public static String getNameById(Integer id){
        String name = null;
        IpaStatusEnum[] values = IpaStatusEnum.values();
        for (IpaStatusEnum e:values) {
            if (e.getId().equals(id)){
                name = e.getName();
                break;
            }
        }
        return name;
    }

    @Getter @Setter
    private Integer id;
    @Getter @Setter
    private String name;
}
