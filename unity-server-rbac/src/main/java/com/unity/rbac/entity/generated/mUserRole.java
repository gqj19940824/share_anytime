package com.unity.rbac.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 用户角色
 * @author creator
 * 生成时间 2018-12-24 19:44:02
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mUserRole extends BaseEntity{


        
        /**
        * 编号_角色
        **/
        @TableField("id_rbac_role")
        private Long idRbacRole ;
        
        
        
        /**
        * 编号_用户
        **/
        @TableField("id_rbac_user")
        private Long idRbacUser ;
        
        

    public mUserRole(){}
}




