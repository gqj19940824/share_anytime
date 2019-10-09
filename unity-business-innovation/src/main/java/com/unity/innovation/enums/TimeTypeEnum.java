package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 超时类型
 * @author zhang
 * 生成时间 2019-10-08 16:14:17
 */
@AllArgsConstructor
public enum TimeTypeEnum {
    HOURS(1,"hours"),
    DAYS(1,"days")
    ;
    
    
    public static TimeTypeEnum of(Integer id) {
        if (id.equals(HOURS.getId())) {
            return HOURS;
        }
        if (id.equals(DAYS.getId())) {
            return DAYS;
        }
       return null;
    }
    
    /**
     * 判断值是否在枚举中存在
     * @param id
     * @return
     */
    public static boolean exist(int id){
        boolean flag = false;
        for (TimeTypeEnum e: TimeTypeEnum.values()){
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



