package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * sys_send_sms_log
 *
 * @author G
 * 生成时间 2019-10-17 21:24:33
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class mSysSendSmsLog extends BaseEntity {


    /**
     * 目标用户id
     **/
    @CommentTarget("目标用户id")
    @TableField("user_id")
    private Long userId;


    /**
     * 目标用户手机号
     **/
    @CommentTarget("目标用户手机号")
    @TableField("phone")
    private String phone;


    /**
     * 短信内容
     **/
    @CommentTarget("短信内容")
    @TableField("content")
    private String content;


    /**
     * 目标用户所属单位
     **/
    @CommentTarget("目标用户所属单位")
    @TableField("id_rbac_department")
    private Long idRbacDepartment;


    /**
     * 短信发送所属来源
     **/
    @CommentTarget("短信发送所属来源")
    @TableField("data_source_class")
    private Integer dataSourceClass;


    /**
     * 流程状态:status:1 新增清单/新增协同单位,2 超时未处理,3 超时未更新,4 清单删除,5 处理完毕,6 重新开启
     **/
    @CommentTarget("流程状态:status:1 新增清单/新增协同单位,2 超时未处理,3 超时未更新,4 清单删除,5 处理完毕,6 重新开启")
    @TableField("flow_status")
    private Integer flowStatus;

    @CommentTarget("源数据id")
    @TableField("source_id")
    private Long sourceId;
}




