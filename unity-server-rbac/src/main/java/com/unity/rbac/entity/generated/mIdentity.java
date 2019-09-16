package com.unity.rbac.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 身份
 * @author creator
 * 生成时间 2018-12-24 19:43:58
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mIdentity extends BaseEntity{


        
        /**
        * 身份名称
        **/
        @TableField("name")
        private String name ;
        
        
        
        /**
        * 平台:status:1 web,2 android,3 ios,4 微信,5 小程序
        **/
        @TableField("platform")
        private Integer platform ;
        
        

    public mIdentity(){}
}




