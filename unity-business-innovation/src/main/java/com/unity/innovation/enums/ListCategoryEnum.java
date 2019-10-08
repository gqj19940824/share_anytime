package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 清单类别
 * @author zhang
 * 生成时间 2019-10-08 16:14:17
 */
@AllArgsConstructor
public enum ListCategoryEnum {

    CITYCONTROL(10,"cityControl"),
    INNOVATIONCONTROL(20,"innovationControl"),
    TARGETCONTROL(30,"targetControl"),
    INTELLIGENCECONTROL(40,"intelligenceControl"),
    ;
    
    
    public static ListCategoryEnum of(Integer id) {

        if (id.equals(CITYCONTROL.getId())) {
            return CITYCONTROL;
        }
        if (id.equals(INNOVATIONCONTROL.getId())) {
            return INNOVATIONCONTROL;
        }
        if (id.equals(TARGETCONTROL.getId())) {
            return TARGETCONTROL;
        }
        if (id.equals(INTELLIGENCECONTROL.getId())) {
            return INTELLIGENCECONTROL;
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
        for (ListCategoryEnum e: ListCategoryEnum.values()){
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



