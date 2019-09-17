package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 创新日常工作管理-工作动态日志表
 * @author zhang
 * 生成时间 2019-09-17 11:17:01
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mDailyWorkStatusLog extends BaseEntity{


        
        /**
        * 工作动态需求id
        **/
        @TableField("id_package")
        private Long idPackage ;
        
        
        
        /**
        * 状态(10.待提交 20.待审核 30.已通过 40.已驳回 50.已发布 60.已更新发布效果 )
        **/
        @TableField("state")
        private Integer state ;
        
        
        
        /**
        * 操作单位
        **/
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        
        
        /**
        * 审核意见
        **/
        @TableField("comment")
        private String comment ;
        
        
        
        /**
        * 操作描述
        **/
        @TableField("action_describe")
        private String actionDescribe ;
        
        

}




