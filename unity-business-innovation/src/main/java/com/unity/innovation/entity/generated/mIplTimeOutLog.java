package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 超时日志记录表
 * @author zhang
 * 生成时间 2019-10-08 16:14:17
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mIplTimeOutLog extends BaseEntity{


        
        /**
        * 超时类型:status:1 hours,2 days
        **/
        @CommentTarget("超时类型:status: hours,days")
        @TableField("time_type")
        private String timeType ;
        
        
        
        /**
        * 单位类别:status:10 main,20 coordination
        **/
        @CommentTarget("单位类别:status:10 main,20 coordination")
        @TableField("unit_category")
        private Integer unitCategory ;
        
        

        /**
        * 主数据id
        **/
        @CommentTarget("主数据id")
        @TableField("main_id")
        private Long mainId ;
        
        
        
        /**
        * 单位id（主责或协同）
        **/
        @CommentTarget("单位id")
        @TableField("department_id")
        private Long departmentId ;

        /**
        * 类别
        **/
        @CommentTarget("类别")
        @TableField("biz_type")
        private String bizType ;

        

}




