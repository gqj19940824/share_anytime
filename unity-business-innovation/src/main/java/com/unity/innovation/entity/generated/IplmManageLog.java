package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 创新发布清单-发布管理日志
 * @author zhang
 * 生成时间 2019-09-21 15:45:34
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class IplmManageLog extends BaseEntity{


        
        /**
        * 编号_创新发布清单-发布管理主表
        **/
        @CommentTarget("编号_创新发布清单-发布管理主表")
        @TableField("id_ipl_manage_main2")
        private Long idIplManageMain2 ;
        
        
        
        /**
        * 单位
        **/
        @CommentTarget("单位")
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        
        
        /**
        * 状态
        **/
        @CommentTarget("状态")
        @TableField("status")
        private Integer status ;
        
        
        
        /**
        * 意见
        **/
        @CommentTarget("意见")
        @TableField("content")
        private String content ;
        
        
        
        /**
        * 创新发布清单管理id
        **/
        @CommentTarget("创新发布清单管理id")
        @TableField("id_ipl_manage_main")
        private Long idIplManageMain ;
        
        

}




