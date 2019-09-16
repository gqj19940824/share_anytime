package com.unity.rbac.enums;

import lombok.AllArgsConstructor;

/**
 * 用户来源
 * @author creator
 * 生成时间 2019-04-02 17:02:50
 */
@AllArgsConstructor
public enum UserSourceEnum {

    /**
     * 系统
     */
    SYSTEM(1, "系统"),

    /**
     * oa
     */
    OA(2,"OA");
    
    /**
     * 通过来源id获取来源信息
     *
     * @param  id 来源编码
     * @return 来源信息
     * @author gengjiajia
     * @since 2019/07/08 10:41
     */
    public static UserSourceEnum of(Integer id) {
        switch (id){
            case 1:return SYSTEM;
            case 2:return OA;
            default:return null;
        }
    }
    
    /**
     * 判断值是否在枚举中存在
     * @param id 来源编码
     * @return 表示是否存在
     */
    public static boolean exist(int id){
        boolean flag = false;
        for (UserSourceEnum e: UserSourceEnum.values()){
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



