package com.unity.rbac.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 用户资源
 * @author creator
 * 生成时间 2018-12-24 19:44:01
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mUserResource extends BaseEntity{


        
        /**
        * 编号_资源
        **/
        @TableField("id_rbac_resource")
        private Long idRbacResource ;
        
        
        
        /**
        * 编号_用户
        **/
        @TableField("id_rbac_user")
        private Long idRbacUser ;



        /**
         * 资源授权标识:flag:1 拥有,0 排除
         **/
        @TableField("auth_flag")
        private Integer authFlag ;
        
        

    public mUserResource(){}
}




