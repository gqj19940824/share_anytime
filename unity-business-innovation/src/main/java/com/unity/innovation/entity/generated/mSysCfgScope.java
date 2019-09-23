package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 系统配置-适用范围关联表
 * @author zhang
 * 生成时间 2019-09-21 15:22:23
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mSysCfgScope extends BaseEntity{


        
        /**
        * 系统配置表id
        **/
        @CommentTarget("系统配置表id")
        @TableField("id_sys_cfg")
        private Long idSysCfg ;
        
        
        
        /**
        * 单位id
        **/
        @CommentTarget("单位id")
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        

}




