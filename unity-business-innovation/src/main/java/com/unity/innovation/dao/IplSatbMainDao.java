
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.controller.vo.PieVoByDoc;
import com.unity.innovation.entity.IplSatbMain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 创新发布清单-科技局-主表
 *
 * @author G
 * 生成时间 2019-10-08 17:03:09
 */
public interface IplSatbMainDao extends BaseDao<IplSatbMain> {

    @Select("select cast(sum(m.total_amount) as decimal(20,2)) value, sc.cfg_val name " +
            "from ipl_satb_main m left join sys_cfg sc on m.industry_category = sc.id " +
            "where m.gmt_create >= #{start} and m.gmt_create < #{end} and m.is_deleted = 0 " +
            "group by sc.id")
    List<PieVoByDoc.DataBean> demandNew(@Param("start") Long start, @Param("end") Long end);

    @Select("select cast(sum(m.bank) as decimal(20,2)) bank, cast(sum(m.bond) as decimal(15,2)) bond, cast(sum(m.raise) as decimal(15,2)) raise " +
            "from ipl_satb_main m " +
            "where m.gmt_create >= #{start} and m.gmt_create < #{end} and m.is_deleted = 0 ")
    Map<String, Double> demandNewCatagory(@Param("start") Long start, @Param("end") Long end);

    @Select("SELECT FROM_UNIXTIME( s.gmt_create/1000, '%Y年%m月' )  AS month, cast(sum(s.total_amount) as decimal(20, 2)) AS sum " +
            "FROM ipl_satb_main s " +
            "where s.is_deleted = 0 and s.gmt_create >= #{start} and s.gmt_create < #{end} " +
            "group by month")
    List<Map<String, Object>> satbDemandTrend(@Param("start") Long start, @Param("end") Long end);
}

