package com.unity.innovation.enums;

import lombok.AllArgsConstructor;

/**
 * 数据来源归属
 *
 * @author G
 * 生成时间 2019-09-23 17:36:55
 */
@AllArgsConstructor
public enum SysMessageDataSourceClassEnum {
    /***/
    COOPERATION(1, "城市创新合作实时清单"),
    DEVELOPING(2, "企业创新发展实时清单"),
    TARGET(3, "成长目标投资实时清单"),
    DEMAND(4, "高端才智需求实时清单"),
    SUGGEST(5, "意见和建议"),
    PROPAGANDA(6, "报名参与发布会"),
    INVESTMENT(7, "寻找投资项目"),
    HELP(10, "清单协同处理"),
    COOPERATION_RELEASE(20, "城市创新合作实时清单发布管理"),
    DEVELOPING_RELEASE(21, "企业创新发展实时清单发布管理"),
    TARGET_RELEASE(22, "成长目标投资实时清单发布管理"),
    DEMAND_RELEASE(23, "高端才智需求实时清单发布管理"),
    RELATION_RELEASE(24, "亲清政商关系清单发布管理"),
    LIST_RELEASE_REVIEW(30, "清单发布审核"),
    WORK_RELEASE_MANAGE(40, "工作动态发布管理"),
    WORK_RELEASE_REVIEW(50, "工作动态发布审核");


    public static SysMessageDataSourceClassEnum of(Integer id) {
        switch (id){
            case 1 : return COOPERATION;
            case 2 : return DEVELOPING;
            case 3 : return TARGET;
            case 4 : return DEMAND;
            case 5 : return SUGGEST;
            case 6 : return PROPAGANDA;
            case 7 : return INVESTMENT;
            case 10 : return HELP;
            case 20 : return COOPERATION_RELEASE;
            case 21 : return DEVELOPING_RELEASE;
            case 22 : return TARGET_RELEASE;
            case 23 : return DEMAND_RELEASE;
            case 24 : return RELATION_RELEASE;
            case 30 : return LIST_RELEASE_REVIEW;
            case 40 : return WORK_RELEASE_MANAGE;
            case 50 : return WORK_RELEASE_REVIEW;
            default: return null;
        }
    }

    /**
     * 判断值是否在枚举中存在
     *
     * @param id 类型
     * @return boolean
     */
    public static boolean exist(int id) {
        boolean flag = false;
        for (SysMessageDataSourceClassEnum e : SysMessageDataSourceClassEnum.values()) {
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



