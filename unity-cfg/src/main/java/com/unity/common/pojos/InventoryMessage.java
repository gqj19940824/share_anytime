package com.unity.common.pojos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 实时清单--添加系统消息参数对象
 * <p>
 * create by gengjiajia at 2019/09/24 15:20
 */
@Data
@AllArgsConstructor
@Builder(builderMethodName = "newInstance")
public class InventoryMessage {

    /**
     * 源数据id 必填项
     */
    private Long sourceId;

    /**
     * 源数据所属单位id 必填项
     */
    private Long idRbacDepartment;

    /**
     * 实时清单 此字段填写 企业名称
     * 工作动态 此字段填写 标题
     * 必填项
     */
    private String title;

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
     *   ======================当前类在业务中只需要使用到以上类型============================
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
     * 流程状态 必填项
     * 1 新增实时清单 / 清单协同处理
     * 2 清单超时未处理 / 清单协同处理超时未处理
     * 3 清单超时未更新 / 清单协同处理超时未更新
     * 4 清单更新--协同单位处理
     * 5 清单删除--协同单位处理
     * 6 清单处理完毕--协同单位处理
     * 7 清单重新开启--协同单位处理
     */
    private Integer flowStatus;

    /**
     * 清单协同处理单位id
     * 涉及清单协同处理的业务须填写
     */
    private List<Long> helpDepartmentIdList;

    /**
     * 超时时间 例如：24H 7D
     * 清单超时触发接口时需填写
     */
    private String time;

    /**
     * 清单类型
     */
    private Integer bizType;


    public InventoryMessage(Long sourceId, Long idRbacDepartment, String title, Integer dataSourceClass, Integer flowStatus) {
        this.sourceId = sourceId;
        this.idRbacDepartment = idRbacDepartment;
        this.title = title;
        this.dataSourceClass = dataSourceClass;
        this.flowStatus = flowStatus;
    }
}
