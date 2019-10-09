package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 单位类别
 * @author zhang
 * 生成时间 2019-10-08 16:14:17
 */
@AllArgsConstructor
public enum UnitCategoryEnum {
    //主责
    MAIN(10,"main"),
    //协同
    COORDINATION(20,"coordination")
    ;
    
    
    public static UnitCategoryEnum of(Integer id) {
        if (id.equals(MAIN.getId())) {
            return MAIN;
        }
        if (id.equals(COORDINATION.getId())) {
            return COORDINATION;
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
        for (UnitCategoryEnum e: UnitCategoryEnum.values()){
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



