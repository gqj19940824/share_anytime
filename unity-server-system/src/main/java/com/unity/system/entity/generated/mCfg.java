package com.unity.system.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 系统配置
 * @author creator
 * 生成时间 2018-12-24 19:52:54
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mCfg extends BaseEntity{


        
        /**
        * 类型
        **/
        @TableField("cfg_type")
        private String cfgType ;
        
        
        
        /**
        * 值
        **/
        @TableField("cfg_val")
        private String cfgVal ;
        
        

    public mCfg(){}
}




