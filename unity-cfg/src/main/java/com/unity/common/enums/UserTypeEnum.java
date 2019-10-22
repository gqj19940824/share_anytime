package com.unity.common.enums;

import lombok.AllArgsConstructor;

/**
 * 类型
 * @author creator
 * 生成时间 2019-04-02 17:02:50
 */
@AllArgsConstructor
public enum UserTypeEnum {
    /**
     * 用户类型
     */
    ADMIN(1,"管理员账号"),
    LEADER(2, "领导账号"),
    ORDINARY(3, "普通账号");
    
    
    public static UserTypeEnum of(Integer id) {
        switch (id){
            case 1:return ADMIN;
            case 2:return LEADER;
            case 3:return ORDINARY;
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



