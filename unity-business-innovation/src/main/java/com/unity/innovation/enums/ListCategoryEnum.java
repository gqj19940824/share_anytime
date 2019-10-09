package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * 清单类别
 * @author zhang
 * 生成时间 2019-10-08 16:14:17
 */
@AllArgsConstructor
public enum ListCategoryEnum {

    CITYCONTROL(10L,"cityControl"),
    INNOVATIONCONTROL(12L,"innovationControl"),
    TARGETCONTROL(13L,"targetControl"),
    INTELLIGENCECONTROL(2L,"intelligenceControl"),
    ;
    
    
    public static ListCategoryEnum of(Long id) {

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

    public static ListCategoryEnum valueOfName(String name){
        for (ListCategoryEnum e: ListCategoryEnum.values()){
            if(e.getName().equals(name)){
               return e;
            }
        }
        return  null;
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
    
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}



