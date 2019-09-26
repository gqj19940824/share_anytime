package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 企服局创新发展清单管理表一对多企服局创新发展清单表
 * @author zhang
 * 生成时间 2019-09-21 15:45:34
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "iplm_main_m_ipl_main")
public class IplmMainIplMain extends BaseEntity{
        
        /**
        * 创新发布清单id
        **/
        @CommentTarget("创新发布清单id")
        @TableField("id_ipl_main")
        private Long idIplMain ;
        
        
        
        /**
        * 创新发布清单管理id
        **/
        @CommentTarget("创新发布清单管理id")
        @TableField("id_iplm_main")
        private Long idIplmMain ;
        
        
        
        /**
        * 单位id
        **/
        @CommentTarget("单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty ;
}




