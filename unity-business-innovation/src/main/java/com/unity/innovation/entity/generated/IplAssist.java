package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 创新发布清单-协同事项
 * @author zhang
 * 生成时间 2019-09-21 15:45:35
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class IplAssist extends BaseEntity{


        
        /**
        * 编号_创新发布清单-企服局-主表
        **/
        @CommentTarget("编号_创新发布清单-企服局-主表")
        @TableField("id_ipl_esb_main2")
        private Long idIplEsbMain2 ;
        
        
        
        /**
        * 编号_创新发布清单-企服局-主表2
        **/
        @CommentTarget("编号_创新发布清单-企服局-主表2")
        @TableField("id_ipl_esb_main3")
        private Long idIplEsbMain3 ;
        
        
        
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
        * 协同单位id
        **/
        @CommentTarget("协同单位id")
        @TableField("id_rbac_department_assist")
        private Long idRbacDepartmentAssist ;
        
        
        
        /**
        * 邀请事项
        **/
        @CommentTarget("邀请事项")
        @TableField("invite_info")
        private String inviteInfo ;
        
        
        
        /**
        * 处理状态
        **/
        @CommentTarget("处理状态")
        @TableField("process")
        private Integer process ;
        
        
        
        /**
        * 所属主表id
        **/
        @CommentTarget("所属主表id")
        @TableField("id_ipl_esb_main")
        private Long idIplEsbMain ;
        
        
        
        /**
        * 主责单位id
        **/
        @CommentTarget("主责单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty ;
        
        

}




