package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 创新日常工作管理-工作动态需求表
 * @author zhang
 * 生成时间 2019-09-17 11:17:02
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mDailyWorkStatusPackage extends BaseEntity{


        
        /**
        * 标题
        **/
        @TableField("title")
        private String title ;
        
        
        
        /**
        * 状态(10.待提交 20.待审核 30.已通过 40.已驳回 50.已发布 60.已更新发布效果 )
        **/
        @TableField("state")
        private Integer state ;
        
        
        
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
        

}




