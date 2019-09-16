package com.unity.system.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 数据字典
 * @author creator
 * 生成时间 2018-12-24 19:52:54
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mDictionary extends BaseEntity{


        
        /**
        * 字典名
        **/
        @TableField("name")
        private String name ;
        
        

    public mDictionary(){}
}




