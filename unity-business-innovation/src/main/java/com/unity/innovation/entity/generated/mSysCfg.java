package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 系统配置
 * @author zhang
 * 生成时间 2019-09-17 14:53:55
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mSysCfg extends BaseEntity{


        
        /**
        * 类型 1：工作类别 2：关键字 3：产业类型 4：需求类型
        **/
        @TableField("cfg_type")
        private String cfgType ;
        
        
        
        /**
        * 值
        **/
        @TableField("cfg_val")
        private String cfgVal ;
        
        
        
        /**
        * 适用范围 0 代表全部  其余代表单位id
        **/
        @TableField("scope")
        private Long scope ;


        /**
         * 适用范围 0 代表全部  其余代表单位id
         **/
        @TableField("use_status")
        private Integer useStatus ;
        
        

}




