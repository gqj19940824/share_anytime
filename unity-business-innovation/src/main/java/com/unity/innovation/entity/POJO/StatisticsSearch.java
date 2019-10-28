package com.unity.innovation.entity.POJO;

import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField(exist = false)
    private String beginTime;

    /**
     * 截止时间
     */
    @TableField(exist = false)
    private String endTime;

    /**
     * 月份查询
     */
    @TableField(exist = false)
    private String monthTime;

    /**
     * 单位id
     */
    @TableField(exist = false)
    private String idRbacDepartment;
}
