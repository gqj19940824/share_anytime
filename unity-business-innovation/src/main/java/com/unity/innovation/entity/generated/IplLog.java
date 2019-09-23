package com.unity.innovation.entity.generated;


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
        * 编号_创新发布清单-企服局-主表
        **/
        @CommentTarget("编号_创新发布清单-企服局-主表")
        @TableField("id_ipl_esb_main")
        private Long idIplEsbMain ;
        
        
        
        /**
        * 编号_创新发布清单-组织部-主表
        **/
        @CommentTarget("编号_创新发布清单-组织部-主表")
        @TableField("id_ipl_od_main")
        private Long idIplOdMain ;
        
        
        
        /**
        * 编号_创新发布清单-科技局-主表
        **/
        @CommentTarget("编号_创新发布清单-科技局-主表")
        @TableField("id_ipl_satb_main")
        private Long idIplSatbMain ;
        
        
        
        /**
        * 编号_创新发布清单-发改局-主表
        **/
        @CommentTarget("编号_创新发布清单-发改局-主表")
        @TableField("id_ipl_darb_main")
        private Long idIplDarbMain ;
        
        
        
        /**
        * 编号_意见建议-纪检组
        **/
        @CommentTarget("编号_意见建议-纪检组")
        @TableField("id_suggestion")
        private Long idSuggestion ;
        
        
        
        /**
        * 编号_创新发布清单-宣传部-主表
        **/
        @CommentTarget("编号_创新发布清单-宣传部-主表")
        @TableField("id_ipl_pd_main")
        private Long idIplPdMain ;
        
        
        
        /**
        * 编号_创新发布清单-亦庄国投-主表
        **/
        @CommentTarget("编号_创新发布清单-亦庄国投-主表")
        @TableField("id_ipl_yzgt_main")
        private Long idIplYzgtMain ;
        
        
        
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
        
        
        
        /**
        * 主责单位id
        **/
        @CommentTarget("主责单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty ;
        
        
        
        /**
        * 日志类别
        **/
        @CommentTarget("日志类别")
        @TableField("log_type")
        private Integer logType ;
        
        

}




