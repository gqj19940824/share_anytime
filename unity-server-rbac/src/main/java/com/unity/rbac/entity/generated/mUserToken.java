package com.unity.rbac.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 用户令牌
 * @author creator
 * 生成时间 2018-12-24 19:44:05
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mUserToken extends BaseEntity{


        
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
        
        
        
        /**
        * 令牌
        **/
        @TableField("token")
        private String token ;
        
        
        
        /**
        * 过期时间
        **/
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS" )
        @TableField("gmt_expr")
        private java.util.Date gmtExpr ;
        
        
        
        /**
        * 登录平台:status:1 web,2 android,3 ios,4 微信,5 小程序
        **/
        @TableField("login_platform")
        private Integer loginPlatform ;
        
        

    public mUserToken(){}
}




