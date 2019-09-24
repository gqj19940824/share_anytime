package com.unity.innovation.entity.generated;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 创新发布清单-操作日志
 * @author zhang
 * 生成时间 2019-09-21 15:45:36
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class IplLog extends BaseEntity{


        /**
        * 处理状态
        **/
        @CommentTarget("处理状态")
        @TableField("process_status")
        private Integer processStatus ;
        
        
        
        /**
        * 处理进展
        **/
        @CommentTarget("处理进展")
        @TableField("process_info")
        private String processInfo ;
        
        
        
        /**
        * 协同单位id
        **/
        @CommentTarget("协同单位id")
        @TableField("id_rbac_department_assist")
        private Long idRbacDepartmentAssist ;
        
        
        
        /**
        * 主表id
        **/
        @CommentTarget("主表id")
        @TableField("id_ipl_main")
        private Long idIplMain ;
}




