package com.unity.rbac.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 响应部门表
 * @author creator
 * 生成时间 2019-04-02 17:02:55
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mRespDepartment extends BaseEntity{


        
        /**
        * 机构名称
        **/
        @TableField("name")
        private String name ;
        
        

}




