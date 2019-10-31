package com.unity.innovation.entity.POJO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.CommentTarget;
import lombok.*;

/**
 * @author zhqgeng
 * @create 2019-10-28 15:06
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class StatisticsSearch {

    /**
     * 开始时间
     */
    @CommentTarget("开始时间")
    @TableField(exist = false)
    private String beginTime;

    /**
     * 截止时间
     */
    @CommentTarget("截止时间")
    @TableField(exist = false)
    private String endTime;

    /**
     * 月份查询 2018-06
     */
    @CommentTarget("时间")
    @TableField(exist = false)
    private String monthTime;

    /**
     * 单位id
     */
    @TableField(exist = false)
    private String idRbacDepartment;

    /**
     * 类型 10 20 30 40 50 60
     */
    @CommentTarget("类型")
    @TableField(exist = false)
    private Integer bizType;




    /**
     * 类型
     */
    @TableField(exist = false)
    private Integer type;

}
