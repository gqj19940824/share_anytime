package com.unity.system.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 数据字典项
 * @author creator
 * 生成时间 2018-12-24 19:52:55
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mDictionaryItem extends BaseEntity{


        
        /**
        * 编号_数据字典
        **/
        @TableField("id_sys_dictionary")
        private Long idSysDictionary ;
        
        
        
        /**
        * 字典项
        **/
        @TableField("name")
        private String name ;
        
        
        
        /**
        * 级次编码
        **/
        @TableField("gradation_code")
        private String gradationCode ;
        
        
        
        /**
        * 树层级
        **/
        @TableField("i_level")
        private Integer level ;
        
        
        
        /**
        * 父记录id
        **/
        @TableField("id_parent")
        private Long idParent ;
        
        

    public mDictionaryItem(){}
}




