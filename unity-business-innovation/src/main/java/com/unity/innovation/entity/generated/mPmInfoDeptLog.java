package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 企业信息发布管理-审批日志表
 * @author zhang
 * 生成时间 2019-10-15 15:33:01
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mPmInfoDeptLog extends BaseEntity{


        
        /**
        * 单位id
        **/
        @CommentTarget("单位id")
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        
        
        /**
        * 入区企业信息发布管理id
        **/
        @CommentTarget("入区企业信息发布管理id")
        @TableField("id_pm_info_dept")
        private Long idPmInfoDept ;
        
        
        
        /**
        * 状态
        **/
        @CommentTarget("状态")
        @TableField("status")
        private Integer status ;
        
        
        
        /**
        * 审核意见
        **/
        @CommentTarget("审核意见")
        @TableField("content")
        private String content ;
        
        

}




