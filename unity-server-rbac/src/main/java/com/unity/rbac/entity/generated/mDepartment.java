package com.unity.rbac.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织机构
 *
 * @author creator
 * 生成时间 2018-12-24 19:43:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class mDepartment extends BaseEntity {


    /**
     * 机构名称
     **/
    @TableField("name")
    private String name;

    /**
     * 树层次
     **/
    @TableField("i_level")
    private Integer level;

    /**
     * 类型:status:1 公司 company,2 党委 partyCommittee,3 支部 branch
     */
    @TableField("dep_type")
    private Integer depType;

    /**
     * 注册地址
     **/
    @TableField("address")
    private String address;

    /**
     * 是否启用  0 否 1 是
     **/
    @TableField("use_status")
    private Integer useStatus;

    public mDepartment() {
    }
}




