package com.unity.innovation.dao;

import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.generated.IplLog;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 创新发布清单-操作日志
 * @author zhang
 * 生成时间 2019-09-21 15:45:36
 */
public interface IplLogDao  extends BaseDao<IplLog>{


    /**
     * 统计月度需求完成情况数量
     *
     * @param  startTime 开始统计时间
     * @param  endTime 结束统计时间
     * @param bizType 清单类型
     * @return 完成情况数量
     * @author gengjiajia
     * @since 2019/10/30 14:59
     */
    @Select("SELECT " +
            " FROM_UNIXTIME(gmt_create / 1000, '%Y年%m月') AS MONTH, " +
            "IF ( " +
            " SUM(complete_num) IS NULL, " +
            " 0, " +
            " SUM(complete_num) " +
            ") AS num " +
            "FROM " +
            " ipl_log " +
            "WHERE " +
            " biz_type = #{bizType} " +
            " AND " +
            " gmt_create BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY " +
            "  `month`")
    List<Map<String,Object>> statisticsMonthlyDemandCompletionNum(Long startTime, Long endTime, Integer bizType);
}

