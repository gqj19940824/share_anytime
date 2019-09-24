package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 数据来源归属
 *
 * @author G
 * 生成时间 2019-09-23 17:36:55
 */
@AllArgsConstructor
public enum SysMessageDataSourceClassEnum {
    /***/
    DEVELOPMENT(1, "发改局"),
    COMPANY_SERVER(2, "企服局"),
    TECHNOLOGY(3, "科技局"),
    ORGANIZATION(4, "组织部"),
    INSPECTION(5, "纪检组"),
    PROPAGANDA(6, "宣传部"),
    INVESTMENT(7, "亦庄国投"),
    ;


    public static SysMessageDataSourceClassEnum of(Integer id) {
        if (id.equals(DEVELOPMENT.getId())) {
            return DEVELOPMENT;
        }
        if (id.equals(COMPANY_SERVER.getId())) {
            return COMPANY_SERVER;
        }
        if (id.equals(TECHNOLOGY.getId())) {
            return TECHNOLOGY;
        }
        if (id.equals(ORGANIZATION.getId())) {
            return ORGANIZATION;
        }
        if (id.equals(INSPECTION.getId())) {
            return INSPECTION;
        }
        if (id.equals(PROPAGANDA.getId())) {
            return PROPAGANDA;
        }
        if (id.equals(INVESTMENT.getId())) {
            return INVESTMENT;
        }
        ;
        return null;
    }

    /**
     * 判断值是否在枚举中存在
     *
     * @param id
     * @return
     */
    public static boolean exist(int id) {
        boolean flag = false;
        for (SysMessageDataSourceClassEnum e : SysMessageDataSourceClassEnum.values()) {
            if (e.getId() == id) {
                flag = true;
                break;
            }
        }
        return flag;
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



