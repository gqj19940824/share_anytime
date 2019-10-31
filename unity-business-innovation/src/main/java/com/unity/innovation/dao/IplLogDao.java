package com.unity.innovation.dao;

import com.unity.common.base.BaseDao;
import com.unity.innovation.controller.vo.PieVoByDoc;
import com.unity.innovation.entity.generated.IplLog;
import org.apache.ibatis.annotations.Param;
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
            "AND is_deleted = 0 " +
            " AND " +
            " gmt_create BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY " +
            "  `month`")
    List<Map<String,Object>> statisticsMonthlyDemandCompletionNum(Long startTime, Long endTime, Integer bizType);

    @Select("select if(sum(l.complete_num) is null, 0, cast(sum(l.complete_num) as decimal(20,2))) value, sc.cfg_val name " +
            "from ipl_log l inner join ipl_satb_main ism on l.id_ipl_main = ism.id and l.biz_type = #{bizType} " +
            "    inner join sys_cfg sc on ism.industry_category = sc.id " +
            "where l.gmt_create >= #{start} and l.gmt_create < #{end} " +
            "group by ism.industry_category")
    List<PieVoByDoc.DataBean> satbDemandDone(@Param("start") Long start, @Param("end") Long end, @Param("bizType") Integer bizType);

    /**
     * 某年某月指定行业人才需求完成情况统计
     *
     * @param  startTime 统计开始时间范围
     * @param endTime 统计截止时间范围
     * @return 需求完成情况统计
     * @author gengjiajia
     * @since 2019/10/30 19:38
     */
    @Select("SELECT " +
            " od.industry_category AS industry, " +
            "IF ( " +
            " SUM(il.complete_num) IS NULL, " +
            " 0, " +
            " CAST( " +
            "  SUM(il.complete_num) AS DECIMAL (11, 2) " +
            " ) " +
            ") AS num " +
            "FROM " +
            " ipl_od_main od " +
            "INNER JOIN ipl_log il ON od.id = il.id_ipl_main " +
            "WHERE " +
            " il.biz_type = 40 " +
            "AND od.is_deleted = 0 " +
            "AND il.is_deleted = 0 " +
            "AND il.gmt_create BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY od.industry_category")
    List<Map<String,Object>> statisticsIndustryDemandCompletionNum(Long startTime, Long endTime);
}

