package com.unity.rbac.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 账号冲突记录
 *
 * @author zhang
 * 生成时间 2019-07-25 18:51:37
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class mAccountConflictRecord extends BaseEntity {


    /**
     * 本地账号
     **/
    @TableField("login_name")
    private String loginName;


    /**
     * 本地id
     **/
    @TableField("local_id")
    private Long localId;


    /**
     * 用户中心id
     **/
    @TableField("ucs_id")
    private Long ucsId;

    /**
     * 密码
     **/
    @TableField("ucs_pwd")
    private String ucsPwd;


    /**
     * 来源:status:10 OA oa,20 资产 assets,30 安全 safe,40 工程 project
     **/
    @TableField("ucs_source")
    private Integer ucsSource;


    /**
     * 数据状态 10 待处理  20 已处理
     **/
    @TableField("data_status")
    private Integer dataStatus;

    /**
     * 组织名称
     **/
    @TableField("depart_name_ucs_user")
    private String departNameUcsUser;

    /**
     * 名称
     **/
    @TableField("name")
    private String name;
}




