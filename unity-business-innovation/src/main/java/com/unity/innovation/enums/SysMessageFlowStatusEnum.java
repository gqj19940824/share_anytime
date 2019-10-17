package com.unity.innovation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * 数据来源
 *
 * @author G
 * 生成时间 2019-09-23 17:36:55
 */
@AllArgsConstructor
public enum SysMessageFlowStatusEnum {
    /***/
    ONE(1, "新增实时清单/清单协同处理"),
    TWO(2, "清单超时未处理/清单协同处理超时未处理"),
    THREE(3, "清单超时未更新/清单协同处理超时未更新"),
    FOUR(4, "清单更新--协同单位处理"),
    FIVES(5, "清单删除--协同单位处理"),
    SIX(6, "清单处理完毕--协同单位处理"),
    SEVEN(7, "清单重新开启--协同单位处理");


    public static String ofName(Integer id) {
        switch(id) {
            case 1:
                return ONE.getName();
            case 2:
                return TWO.getName();
            case 3:
                return THREE.getName();
            case 4:
                return FOUR.getName();
            case 5:
                return FIVES.getName();
            case 6:
                return SIX.getName();
            case 7:
                return SEVEN.getName();
            default:
                return "";
        }
    }

    /**
     * 判断值是否在枚举中存在
     *
     * @param id
     * @return
     */
    public static boolean exist(int id) {
        boolean flag = false;
        for (SysMessageFlowStatusEnum e : SysMessageFlowStatusEnum.values()) {
            if (e.getId() == id) {
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



