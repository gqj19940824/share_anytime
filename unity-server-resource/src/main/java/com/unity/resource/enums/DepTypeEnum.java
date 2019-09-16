package com.unity.resource.enums;

import lombok.AllArgsConstructor;

/**
 * 类型
 * @author creator
 * 生成时间 2019-01-25 13:46:28
 */
@AllArgsConstructor
public enum DepTypeEnum {

    COMPANY(1, "公司"),
        PARTYCOMMITTEE(2, "党委"),
        BRANCH(3, "支部"),
    ;
    
    
    public static DepTypeEnum of(Integer id) {
        if (id.equals(COMPANY.getId())) {
           return COMPANY;
        }
            if (id.equals(PARTYCOMMITTEE.getId())) {
           return PARTYCOMMITTEE;
        }
            if (id.equals(BRANCH.getId())) {
           return BRANCH;
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



