package com.unity.innovation.enums;

import lombok.AllArgsConstructor;


/**
 * 数据来源
 *
 * @author G
 * 生成时间 2019-09-23 17:36:55
 */
@AllArgsConstructor
public enum SysMsgFlowStatusEnum {
    /**
     * 流程状态 必填项
     * 1 提交
     * 2 驳回
     * 3 通过
     * 4 发布
     * 5 更新发布效果
     */
    ONE(1, "提交"),
    TWO(2, "驳回"),
    THREE(3, "通过"),
    FOUR(4, "发布"),
    FIVES(5, "更新发布效果");


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
        for (SysMsgFlowStatusEnum e : SysMsgFlowStatusEnum.values()) {
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



