package com.unity.rbac.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 资源身份
 * @author creator
 * 生成时间 2018-12-24 19:43:59
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mResourceIdentity extends BaseEntity{


        
        /**
        * 编号_身份
        **/
        @TableField("id_rbac_identity")
        private Long idRbacIdentity ;
        
        
        
        /**
        * 编号_资源
        **/
        @TableField("id_rbac_resource")
        private Long idRbacResource ;
        
        

    public mResourceIdentity(){}
}




