package com.unity.system.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 流水号
 * @author creator
 * 生成时间 2018-12-24 19:52:55
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mSerial extends BaseEntity{


        
        /**
        * 前缀
        **/
        @TableField("serial_prefix")
        private String serialPrefix ;
        
        
        
        /**
        * 后缀
        **/
        @TableField("serial_suffixes")
        private String serialSuffixes ;
        
        
        
        /**
        * 类型
        **/
        @TableField("serial_type")
        private Integer serialType ;
        
        
        
        /**
        * 数值
        **/
        @TableField("serial_val")
        private String serialVal ;
        
        
        
        /**
        * 标识
        **/
        @TableField("serial_code")
        private String serialCode ;
        
        

    public mSerial(){}
}




