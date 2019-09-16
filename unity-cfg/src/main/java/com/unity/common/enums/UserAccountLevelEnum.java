package com.unity.common.enums;

import lombok.AllArgsConstructor;

/**
 * 类型
 * @author creator
 * 生成时间 2019-04-02 17:02:50
 */
@AllArgsConstructor
public enum UserAccountLevelEnum {

    /**
     * 集团账号
     */
    GROUP(1,"集团账号"),
    /**
     * 二级单位账号
     */
    SECOND_COMPANY(2, "二级单位账号"),
    /**
     * 三级单位账号
     */
    THIRD_COMPANY(3, "三级单位账号"),
    /**
     * 项目账号
     */
    PROJECT(4,"项目账号");
    
    /**
     * 通过级别id获取级别信息
     *
     * @param  id 级别编码
     * @return 级别信息
     * @author gengjiajia
     * @since 2019/07/08 10:41
     */
    public static UserAccountLevelEnum of(Integer id) {
        switch (id){
            case 1:return GROUP;
            case 2:return SECOND_COMPANY;
            case 3:return THIRD_COMPANY;
            case 4:return PROJECT;
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
        for (UserAccountLevelEnum e: UserAccountLevelEnum.values()){
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



