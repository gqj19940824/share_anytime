package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 创新日常工作管理-关键字中间表
 * @author zhang
 * 生成时间 2019-09-17 11:17:02
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mDailyWorkKeyword extends BaseEntity{


        
        /**
        * 关键字id
        **/
        @TableField("id_keyword")
        private Long idKeyword ;
        
        
        
        /**
        * 工作日常id
        **/
        @TableField("id_daily_work_status")
        private Long idDailyWorkStatus ;
        
        

}




