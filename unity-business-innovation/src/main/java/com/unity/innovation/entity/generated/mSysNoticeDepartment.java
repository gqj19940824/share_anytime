package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 通知公告-单位关联表
 * @author zhang
 * 生成时间 2019-09-23 15:00:35
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mSysNoticeDepartment extends BaseEntity{


        
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

}




