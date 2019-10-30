
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.controller.vo.PieVoByDoc;
import com.unity.innovation.entity.IplSatbMain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 创新发布清单-科技局-主表
 *
 * @author G
 * 生成时间 2019-10-08 17:03:09
 */
public interface IplSatbMainDao extends BaseDao<IplSatbMain> {

    @Select("select sum(m.total_amount) value, sc.cfg_val name " +
            "from ipl_satb_main m left join sys_cfg sc on m.industry_category = sc.id " +
            "where m.gmt_create >= #{start} and m.gmt_create < #{end} " +
            "group by sc.id")
    List<PieVoByDoc.DataBean> demandNew(@Param("start") Long start, @Param("end") Long end);

    @Select("select sum(m.bank) bank, sum(m.raise) raise, sum(m.bond) bond " +
            "from ipl_satb_main m " +
            "where m.gmt_create >= #{start} and m.gmt_create < #{end}")
    List<PieVoByDoc.DataBean> demandNewCatagory(@Param("start") Long start, @Param("end") Long end);
}

