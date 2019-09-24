package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 数据来源
 *
 * @author G
 * 生成时间 2019-09-23 17:36:55
 */
@AllArgsConstructor
public enum SysMessageDataSourceEnum {
    /***/
    ADD1(1, "新增清单"),
    ADD2(2, "新增清单协同"),
    TIMEOUT1(3, "清单超时未处理"),
    TIMEOUT2(4, "清单协同超时未处理"),
    NO_UPDATE1(5, "清单超时未更新进展"),
    NO_UPDATE2(6, "清单协同超时未更新进展"),
    UPDATE(7, "主责单位再次编辑基本信息"),
    DELETE(8, "主责单位删除"),
    COMPLETE(9, "处理中->处理完毕"),
    CONTINUED(10, "处理完毕->处理中"),
    ;


    public static SysMessageDataSourceEnum of(Integer id) {
        if (id.equals(ADD1.getId())) {
            return ADD1;
        }
        if (id.equals(ADD2.getId())) {
            return ADD2;
        }
        if (id.equals(TIMEOUT1.getId())) {
            return TIMEOUT1;
        }
        if (id.equals(TIMEOUT2.getId())) {
            return TIMEOUT2;
        }
        if (id.equals(NO_UPDATE1.getId())) {
            return NO_UPDATE1;
        }
        if (id.equals(NO_UPDATE2.getId())) {
            return NO_UPDATE2;
        }
        if (id.equals(UPDATE.getId())) {
            return UPDATE;
        }
        if (id.equals(DELETE.getId())) {
            return DELETE;
        }
        if (id.equals(COMPLETE.getId())) {
            return COMPLETE;
        }
        if (id.equals(CONTINUED.getId())) {
            return CONTINUED;
        }
        ;
        return null;
    }

    /**
     * 判断值是否在枚举中存在
     *
     * @param id
     * @return
     */
    public static boolean exist(int id) {
        boolean flag = false;
        for (SysMessageDataSourceEnum e : SysMessageDataSourceEnum.values()) {
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



