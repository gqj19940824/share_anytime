package com.unity.rbac.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 组织机构类型:status:1 公司 company,2 党委 partyCommittee,3 支部 branch,4 人员
 * <p>
 * create by gengjiajia at 2018/12/13 11:23
 */
@AllArgsConstructor
public enum DepartmentDepTypeEnum {
    ROOT(0),
    COMPANY(1),
    PARTYCOMMITTEE(2),
    BRANCH(3),
    STAFF(4);

    @Getter @Setter
    private int type;
}
