package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 创新发布活动-活动管理一对多发布管理表
 * @author zhang
 * 生成时间 2019-09-21 15:45:33
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class IpaManageMainIplManageMain extends BaseEntity{


        
        /**
        * 编号_创新发布清单-发布管理主表
        **/
        @CommentTarget("编号_创新发布清单-发布管理主表")
        @TableField("id_ipl_manage_main2")
        private Long idIplManageMain2 ;
        
        
        
        /**
        * 编号_创新发布活动-管理-主表
        **/
        @CommentTarget("编号_创新发布活动-管理-主表")
        @TableField("id_ipa_manage_main2")
        private Long idIpaManageMain2 ;
        
        
        
        /**
        * 创新发布清单管理表id
        **/
        @CommentTarget("创新发布清单管理表id")
        @TableField("id_ipl_manage_main")
        private Long idIplManageMain ;
        
        
        
        /**
        * 创新发布活动管理表id
        **/
        @CommentTarget("创新发布活动管理表id")
        @TableField("id_ipa_manage_main")
        private Long idIpaManageMain ;
        
        

}




