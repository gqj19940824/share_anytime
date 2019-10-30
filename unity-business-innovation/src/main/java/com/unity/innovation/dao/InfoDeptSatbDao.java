
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.InfoDeptSatb;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 路演企业信息管理-科技局-基础数据表
 * @author zhang
 * 生成时间 2019-10-15 15:33:00
 */
public interface InfoDeptSatbDao  extends BaseDao<InfoDeptSatb>{

    /**
     * 与会路演企业成果创新水平统计
     *
     * @param  startTime 开始时间
     * @param  endTime 结束时间
     * @return 统计结果
     * @author gengjiajia
     * @since 2019/10/30 09:37
     */
    @Select("SELECT " +
            " ids.achievement_level AS achievementLevel, " +
            " count(ids.id) AS num " +
            "FROM " +
            " info_dept_satb ids " +
            "INNER JOIN pm_info_dept pid ON ids.id_pm_info_dept = pid.id " +
            "INNER JOIN ipa_manage_main imm ON pid.id_ipa_main = imm.id " +
            "WHERE " +
            " ids.gmt_create BETWEEN #{startTime} " +
            "AND #{endTime} " +
            "GROUP BY " +
            " ids.achievement_level")
    List<Map<String,Long>> avgStatistics(Long startTime,Long endTime);

    /**
     * 与会路演企业成果首次对外发布情况统计
     *
     * @param  startTime 开始时间
     * @param  endTime 结束时间
     * @return 统计结果
     * @author gengjiajia
     * @since 2019/10/30 09:37
     */
    @Select("SELECT " +
            " ids.is_publish_first AS yesOrNo, " +
            " count(ids.id) AS num " +
            "FROM " +
            " info_dept_satb ids " +
            "INNER JOIN pm_info_dept pid ON ids.id_pm_info_dept = pid.id " +
            "INNER JOIN ipa_manage_main imm ON pid.id_ipa_main = imm.id " +
            "WHERE " +
            " ids.gmt_create BETWEEN #{startTime} " +
            "AND #{endTime} " +
            "GROUP BY " +
            " ids.achievement_level")
    List<Map<String,Integer>> firstExternalRelease(Long startTime,Long endTime);
}

