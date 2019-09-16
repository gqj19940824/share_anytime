package com.unity.system.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * app版本
 * @author creator
 * 生成时间 2018-12-24 19:52:53
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mAppVersion extends BaseEntity{


        
        /**
        * 包名
        **/
        @TableField("pack_name")
        private String packName ;
        
        
        
        /**
        * 名称
        **/
        @TableField("name")
        private String name ;
        
        
        
        /**
        * 版本
        **/
        @TableField("version")
        private String version ;
        
        
        
        /**
        * 内部版本
        **/
        @TableField("version_inside")
        private Integer versionInside ;
        
        
        
        /**
        * 是否必须升级
        **/
        @TableField("is_must_upgrade")
        private Byte isMustUpgrade ;
        
        
        
        /**
        * 系统类型:status:2 android,3 ios
        **/
        @TableField("system_type")
        private Integer systemType ;
        
        
        
        /**
        * 下载路径
        **/
        @TableField("download_path")
        private String downloadPath ;
        
        
        
        /**
        * sql更新
        **/
        @TableField("sql_change")
        private String sqlChange ;
        
        

    public mAppVersion(){}
}




