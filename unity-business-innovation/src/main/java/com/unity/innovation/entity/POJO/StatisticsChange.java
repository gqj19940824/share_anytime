package com.unity.innovation.entity.POJO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

/**
 * @author zhqgeng
 *  2019-10-30 16:43
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class StatisticsChange {

    /**
     *月份
     **/
    @TableField("month")
    private String month ;

    /**
     *总数
     **/
    @TableField("count")
    private Integer count ;

    /**
     *创建时间总数
     **/
    @TableField("createSum")
    private Long createSum ;

    /**
     *修改时间总数
     **/
    @TableField("modifiedSum")
    private Long modifiedSum ;

    /**
     *首次处理时间总数
     **/
    @TableField("firstSum")
    private Long firstSum ;

    /**
     *名字
     **/
    @TableField("name")
    private String name ;

    /**
     *平均首次响应时间
     **/
    @TableField(exist = false)
    private Integer firstAvg ;

    /**
     *平均完成时间
     **/
    @TableField(exist = false)
    private Integer finishAvg ;
}
