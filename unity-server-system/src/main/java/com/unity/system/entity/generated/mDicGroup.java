package com.unity.system.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 
 * @author zhang
 * 生成时间 2019-07-23 16:34:48
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mDicGroup extends BaseEntity{


        
        /**
        * 
        **/
        @TableField("group_code")
        private String groupCode ;
        
        
        
        /**
        * 
        **/
        @TableField("group_name")
        private String groupName ;
        
        

}




