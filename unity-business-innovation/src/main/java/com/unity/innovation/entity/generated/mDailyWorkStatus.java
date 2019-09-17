package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 创新日常工作管理-工作动态
 * @author zhang
 * 生成时间 2019-09-17 11:17:01
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mDailyWorkStatus extends BaseEntity{


        
        /**
        * 标题
        **/
        @TableField("title")
        private String title ;
        
        
        
        /**
        * 工作类别
        **/
        @TableField("type")
        private Long type ;
        
        
        
        /**
        * 主题
        **/
        @TableField("theme")
        private String theme ;
        
        
        
        /**
        * 内容描述
        **/
        @TableField("description")
        private String description ;
        
        
        
        /**
        * 附件code
        **/
        @TableField("attachment_code")
        private String attachmentCode ;
        
        
        
        /**
        * 单位id
        **/
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        
        
        /**
        * 提请时间
        **/
        @TableField("gmt_submit")
        private Long gmtSubmit ;
        
        
        
        /**
        * 状态(是否被提请发布)
        **/
        @TableField("state")
        private Integer state ;
        
        

}




