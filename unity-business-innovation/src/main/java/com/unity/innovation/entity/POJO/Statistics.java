package com.unity.innovation.entity.POJO;

import lombok.*;

/**
 * @author zhqgeng
 * 生成日期 2019-10-29 11:38
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Statistics {

    /**
     * 单位id
     */
    private Long deptId;

    /**
     * 单位名称
     */
    private String deptName;


    /**
     * 类别
     **/
    private String bizType ;

    /**
     * 计数
     **/
    private int count ;
}
