package com.unity.rbac.enums;

import lombok.AllArgsConstructor;

/**
 * 类型
 * @author creator
 * 生成时间 2019-04-02 17:02:50
 */
@AllArgsConstructor
public enum UserTypeEnum {

    ROOT(0,"超级管理员"),
    COMPANY(1, "企业用户"),
    GOVERNMENT(2, "响应部门用户"),
    COMPLEX(3,"综合处理中心");
    
    
    public static UserTypeEnum of(Integer id) {
        switch (id){
            case 0:return ROOT;
            case 1:return COMPANY;
            case 2:return GOVERNMENT;
            case 3:return COMPLEX;
            default:return null;
        }
    }
    
    /**
     * 判断值是否在枚举中存在
     * @param id
     * @return
     */
    public static boolean exist(int id){
        boolean flag = false;
        for (UserTypeEnum e: UserTypeEnum.values()){
            if(e.getId()==id){
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



