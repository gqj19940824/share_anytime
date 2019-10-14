package com.unity.innovation.enums;

import com.google.common.collect.Lists;
import com.unity.common.constant.InnovationConstant;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 清单类别
 * @author zhang
 * 生成时间 2019-10-08 16:14:17
 */
@AllArgsConstructor
public enum ListCategoryEnum {

    DEPARTMENT_DARB(InnovationConstant.DEPARTMENT_DARB_ID,"DEPARTMENT_DARB_CONTROL"),
    DEPARTMENT_ESB(InnovationConstant.DEPARTMENT_ESB_ID,"DEPARTMENT_ESB_CONTROL"),
    DEPARTMENT_SATB(InnovationConstant.DEPARTMENT_SATB_ID,"DEPARTMENT_SATB_CONTROL"),
    DEPARTMENT_OD(InnovationConstant.DEPARTMENT_OD_ID,"DEPARTMENT_OD_CONTROL"),
    DEPARTMENT_SUGGESTION_ID(InnovationConstant.DEPARTMENT_SUGGESTION_ID,"DEPARTMENT_SUGGESTION_CONTROL"),

    ;

    public static ListCategoryEnum of(Long id) {

        if (id.equals(DEPARTMENT_OD.getId())) {
            return DEPARTMENT_OD;
        }
        if (id.equals(DEPARTMENT_DARB.getId())) {
            return DEPARTMENT_DARB;
        }
        if (id.equals(DEPARTMENT_ESB.getId())) {
            return DEPARTMENT_ESB;
        }
        if (id.equals(DEPARTMENT_SATB.getId())) {
            return DEPARTMENT_SATB;
        }
        if (id.equals(DEPARTMENT_SUGGESTION_ID.getId())) {
            return DEPARTMENT_SUGGESTION_ID;
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



