package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * @author zhqgeng
 * @create 2019-09-19 14:40
 */
@AllArgsConstructor
public enum WorkStatusAuditingProcessEnum {
    /**
     *  状态(10.待提交 20.已提交 30.已通过 40.已驳回 50.已发布 60.已更新发布效果 )
     * */
    TEN(10,"待提交",""),
    TWENTY(20,"已提交","提交发布需求"),
    THIRTY(30,"已通过","审核发布需求"),
    FORTY(40,"已驳回","审核发布需求"),
    FIFTY(50,"已发布",""),
    SIXTY(60,"已更新发布效果","更新发布效果"),
    ;

    public static WorkStatusAuditingProcessEnum of(Integer id) {
        if (id.equals(TWENTY.getId())) {
            return TWENTY;
        }
        if (id.equals(TEN.getId())) {
            return TEN;
        }
        if (id.equals(THIRTY.getId())) {
            return THIRTY;
        }
        if (id.equals(FORTY.getId())) {
            return FORTY;
        }
        if (id.equals(FIFTY.getId())) {
            return FIFTY;
        }
        if (id.equals(SIXTY.getId())) {
            return SIXTY;
        }
        return null;
    }

    /**
     * 判断值是否在枚举中存在
     *
     * @param id 枚举值id
     * @return true：存在 false：不存在
     */
    public static boolean exist(int id){
        boolean flag = false;
        for (WorkStatusAuditingProcessEnum e: WorkStatusAuditingProcessEnum.values()){
            if(e.getId()==id){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 流程码
     */
    private Integer id;
    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程名称
     */
    private String actionDescribe;

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

    public String getActionDescribe() {
        return actionDescribe;
    }

    public void setActionDescribe(String actionDescribe) {
        this.actionDescribe = actionDescribe;
    }

}
