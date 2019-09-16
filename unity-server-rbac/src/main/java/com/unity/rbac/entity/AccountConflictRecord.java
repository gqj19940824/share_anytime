package com.unity.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.rbac.entity.generated.mAccountConflictRecord;
import lombok.*;


/**
 * @author gengjiajia
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "rbac_account_conflict_record")
public class AccountConflictRecord extends mAccountConflictRecord {

    /**
     * 公司名称
     **/
    @TableField(exist=false)
    private String department;

    /**
     * 手机号
     **/
    @TableField(exist=false)
    private String phone ;

    /**
     * 账号来源 1：系统，2：OA
     **/
    @TableField(exist=false)
    private Integer source ;

    /**
     * 信息完善状态 0 待完善 1 已完善
     **/
    @TableField(exist=false)
    private Integer perfectStatus ;

    /**
     * 账号级别 1：集团账号  2：二级单位账号  3：三级单位  4：项目账号
     **/
    @TableField(exist=false)
    private Integer accountLevel ;

    /**
     * 用户创建时间
     **/
    @TableField(exist=false)
    private Long  userGmtCreate ;

    /**
     * 解决冲突标识 0：表示保留本地账号  1：表示合并用户中心账号
     **/
    @TableField(exist=false)
    private Integer conflictFlag ;
}

