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
    WORK_RELEASE_REVIEW(50, "工作动态发布审核"),
    ENTERPRISE_RELEASE_REVIEW(60, "企业信息发布审核"),
    YZGT_RELEASE_REVIEW(70, "入区企业信息发布管理"),
    SATB_RELEASE_REVIEW(71, "路演企业信息发布管理");


    public static String ofName(Integer id) {
        switch (id){
            case 1 : return COOPERATION.getName();
            case 2 : return DEVELOPING.getName();
            case 3 : return TARGET.getName();
            case 4 : return DEMAND.getName();
            case 5 : return SUGGEST.getName();
            case 6 : return PROPAGANDA.getName();
            case 7 : return INVESTMENT.getName();
            case 10 : return HELP.getName();
            case 20 : return COOPERATION_RELEASE.getName();
            case 21 : return DEVELOPING_RELEASE.getName();
            case 22 : return TARGET_RELEASE.getName();
            case 23 : return DEMAND_RELEASE.getName();
            case 24 : return RELATION_RELEASE.getName();
            case 30 : return LIST_RELEASE_REVIEW.getName();
            case 40 : return WORK_RELEASE_MANAGE.getName();
            case 50 : return WORK_RELEASE_REVIEW.getName();
            case 60 : return ENTERPRISE_RELEASE_REVIEW.getName();
            case 70 : return YZGT_RELEASE_REVIEW.getName();
            case 71 : return SATB_RELEASE_REVIEW.getName();
            default: return "";
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



