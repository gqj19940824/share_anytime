package com.unity.system.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
public class mDic extends BaseEntity{


        
        /**
        * 
        **/
        @TableField("dic_code")
        private String dicCode ;
        
        
        
        /**
        * 
        **/
        @TableField("dic_value")
        private String dicValue ;
        
        
        /**
        *
        **/
        @TableField("dic_name")
        private String dicName ;



        /**
        * 
        **/
        @TableField("group_code")
        private String groupCode ;


        /**
        *
        **/
        @TableField("status")
        private String status ;

}




