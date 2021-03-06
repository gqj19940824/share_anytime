package com.unity.common.pojos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 工作动态--添加系统消息参数对象
 * <p>
 * create by gengjiajia at 2019/09/24 15:20
 */
@Data
@AllArgsConstructor
@Builder(builderMethodName = "newInstance")
public class ReviewMessage {

    /**
     * 源数据id 必填项
     */
    private Long sourceId;

    /**
     * 源数据所属单位id 必填项
     */
    private Long idRbacDepartment;

    /**
     * 标题 必填项
     */
    private String title;

    /**
     * 流程状态 必填项
     * 1 提交
     * 2 驳回
     * 3 通过
     * 4 发布
     * 5 更新发布效果
     */
    private Integer flowStatus;

    /**
     * 数据来源归属
     *     (1, "城市创新合作实时清单"),
     *     (2, "企业创新发展实时清单"),
     *     (3, "成长目标投资实时清单"),
     *     (4, "高端才智需求实时清单"),
     *     (5, "意见和建议"),
     *     (6, "报名参与发布会"),
     *     (7, "寻找投资项目"),
     *     (10, "清单协同处理"),
     * ========================当前类在业务中只需要使用到以下类型============================
     *     (20, "城市创新合作实时清单发布管理"),
     *     (21, "企业创新发展实时清单发布管理"),
     *     (22, "成长目标投资实时清单发布管理"),
     *     (23, "高端才智需求实时清单发布管理"),
     *     (24, "亲清政商关系清单发布管理"),
     *     (30, "清单发布审核"),
     *     (40, "工作动态发布管理"),
     *     (50, "工作动态发布审核")
     */
    private Integer dataSourceClass;

    /**
     * 清单类型
     */
    private Integer bizType;
}
