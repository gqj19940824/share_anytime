package com.unity.rbac.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 默认角色
 * @author creator
 * 生成时间 2019-01-11 17:13:27
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mDefaultRole extends BaseEntity{


        
        /**
        * 编号_身份
        **/
        @TableField("id_rbac_identity")
        private Long idRbacIdentity ;
        
        
        
        /**
        * 编号_角色
        **/
        @TableField("id_rbac_role")
        private Long idRbacRole ;
        
        
        
        /**
        * 是否系统角色:flag:1 是,0 否
        **/
        @TableField("is_system_role")
        private Integer isSystemRole ;
        
        

    public mDefaultRole(){}
}




