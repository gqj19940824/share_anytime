package com.unity.innovation.enums;

import com.unity.common.constant.InnovationConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 清单类别
 * @author zhang
 * 生成时间 2019-10-08 16:14:17
 */
@AllArgsConstructor
public enum ListCategoryEnum {

    DEPARTMENT_DARB(InnovationConstant.DEPARTMENT_DARB_ID,"DEPARTMENT_DARB_CONTROL", "城市创新合作"),
    DEPARTMENT_ESB(InnovationConstant.DEPARTMENT_ESB_ID,"DEPARTMENT_ESB_CONTROL", "企业创新发展"),
    DEPARTMENT_SATB(InnovationConstant.DEPARTMENT_SATB_ID,"DEPARTMENT_SATB_CONTROL", "成长目标投资"),
    DEPARTMENT_OD(InnovationConstant.DEPARTMENT_OD_ID,"DEPARTMENT_OD_CONTROL", "高端才智需求"),
    DEPARTMENT_SUGGESTION_ID(InnovationConstant.DEPARTMENT_SUGGESTION_ID,"DEPARTMENT_SUGGESTION_CONTROL", "亲清政商关系"),
    DEPARTMENT_YZGT(InnovationConstant.DEPARTMENT_YZGT_ID,"DEPARTMENT_YZGT_CONTROL", "亦庄国投"),
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
        if (id.equals(DEPARTMENT_YZGT.getId())) {
            return DEPARTMENT_YZGT;
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

    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String listType;
}



