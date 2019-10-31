
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.IplOdMain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * od->organization department
 * @author zhang
 * 生成时间 2019-10-14 09:47:50
 */
public interface IplOdMainDao  extends BaseDao<IplOdMain>{

    /**
     * 统计月度新增人员需求数量
     *
     * @param  startTime 开始统计时间
     * @param  endTime 结束统计时间
     * @return 人员需求数量
     * @author gengjiajia
     * @since 2019/10/30 14:09
     */
    @Select("SELECT " +
            " FROM_UNIXTIME(gmt_create / 1000, '%Y年%m月') AS MONTH, " +
            " SUM(job_demand_num) AS num " +
            "FROM " +
            " ipl_od_main " +
            "WHERE " +
            " is_deleted = 0 " +
            "AND gmt_create BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY " +
            " `month`")
    List<Map<String,Object>> statisticsAddEmployeeNeedsNum(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 根据行业统计新增人才需求
     *
     * @param  startTime 统计开始时间
     * @param  endTime 统计结束时间
     * @return 新增人才需求统计
     * @author gengjiajia
     * @since 2019/10/30 16:43
     */
    @Select("SELECT " +
            " industry_category AS industry, " +
            " SUM(job_demand_num) AS num " +
            "FROM " +
            " ipl_od_main " +
            "WHERE " +
            " is_deleted = 0 " +
            "AND gmt_create BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY " +
            " industry_category")
    List<Map<String, Object>> statisticsAddEmployeeNeedsNumByIndustry(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
}

