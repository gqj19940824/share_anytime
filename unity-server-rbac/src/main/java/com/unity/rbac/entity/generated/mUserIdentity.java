package com.unity.rbac.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 用户身份
 * @author creator
 * 生成时间 2018-12-24 19:44:01
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mUserIdentity extends BaseEntity{


        
        /**
        * 编号_身份
        **/
        @TableField("id_rbac_identity")
        private Long idRbacIdentity ;
        
        
        
        /**
        * 编号_用户
        **/
        @TableField("id_rbac_user")
        private Long idRbacUser ;
        
        

    public mUserIdentity(){}
}




