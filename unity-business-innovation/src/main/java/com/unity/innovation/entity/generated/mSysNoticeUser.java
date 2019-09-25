package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 通知公告-浏览情况关联表
 * @author zhang
 * 生成时间 2019-09-23 15:00:35
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mSysNoticeUser extends BaseEntity{


        
        /**
        * 通告表id
        **/
        @CommentTarget("通告表id")
        @TableField("id_sys_notice")
        private Long idSysNotice ;
        
        
        
        /**
        * 单位id
        **/
        @CommentTarget("单位id")
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        
        
        /**
        * 用户id
        **/
        @CommentTarget("用户id")
        @TableField("id_rbac_user")
        private Long idRbacUser ;
        
        
        
        /**
        * 浏览情况
        **/
        @CommentTarget("浏览情况")
        @TableField("is_read")
        private Integer isRead ;
        
        
        
        /**
        * 浏览时间
        **/
        @CommentTarget("浏览时间")
        @TableField("gmt_read")
        private Long gmtRead ;

        /**
         * 是否显示
         * */
        @TableField("is_show")
        private Integer isShow ;

}




