package com.unity.resource.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 资源类型
 * @author creator
 * 生成时间 2019-01-25 13:46:29
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mResourceType extends BaseEntity{


        
        /**
        * 文件类型:dic:re_file_dic
        **/
        @TableField("file_type")
        private String fileType ;
        
        
        
        /**
        * 文件类型名称
        **/
        @TableField("type_name")
        private String typeName ;
        
        
        
        /**
        * 简介
        **/
        @TableField("remark")
        private String remark ;
        
        

}




