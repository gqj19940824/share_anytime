package com.unity.resource.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 部署环境操作记录
 * @author zhang
 * 生成时间 2019-09-03 10:12:42
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mDeployEnvOpt extends BaseEntity{


        
        /**
        * 操作名称
        **/
        @TableField("name")
        private String name ;
        
        
        
        /**
        * 类型:status:10 jar上传 jar_type,20 执行脚本 sh_type
        **/
        @TableField("opt_type")
        private Integer optType ;
        
        
        
        /**
        * 服务名称
        **/
        @TableField("server_name")
        private String serverName ;
        
        
        
        /**
        * 文件上传路径
        **/
        @TableField("file_path")
        private String filePath ;
        
        
        
        /**
        * 是否执行
        **/
        @TableField("is_impl")
        private Integer isImpl ;
        
        
        /**
        * 脚本命令
        **/
        @TableField("sh_command")
        private String shCommand ;

        /**
        * 文件地址信息
        **/
        @TableField("file_info")
        private String fileInfo ;



}




