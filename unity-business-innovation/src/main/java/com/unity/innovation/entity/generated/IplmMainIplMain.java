package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 企服局创新发展清单管理表一对多企服局创新发展清单表
 * @author zhang
 * 生成时间 2019-09-21 15:45:34
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class IplmMainIplMain extends BaseEntity{


        
        /**
        * 编号_创新发布清单-发布管理主表
        **/
        @CommentTarget("编号_创新发布清单-发布管理主表")
        @TableField("id_ipl_manage_main")
        private Long idIplManageMain ;
        
        
        
        /**
        * 编号_创新发布清单-企服局-主表
        **/
        @CommentTarget("编号_创新发布清单-企服局-主表")
        @TableField("id_ipl_esb_main2")
        private Long idIplEsbMain2 ;
        
        
        
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
        * 编号_创新发布清单-纪检组-主表
        **/
        @CommentTarget("编号_创新发布清单-纪检组-主表")
        @TableField("id_ipl_supervision_main")
        private Long idIplSupervisionMain ;
        
        
        
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
        * 创新发布清单id
        **/
        @CommentTarget("创新发布清单id")
        @TableField("id_ipl_esb_main")
        private Long idIplEsbMain ;
        
        
        
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
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        

}




