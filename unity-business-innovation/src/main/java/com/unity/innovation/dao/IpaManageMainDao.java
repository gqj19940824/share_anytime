
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.controller.vo.PieVoByDoc;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.entity.generated.IplManageMain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 创新发布活动-管理-主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:33
 */
public interface IpaManageMainDao  extends BaseDao<IpaManageMain>{

    @Select("<script>" +
            "SELECT m.snapshot, FROM_UNIXTIME( a.gmt_create/1000, '%Y年%m月' )  AS month FROM ipa_manage_main a INNER JOIN ipl_manage_main m ON a.id = m.id_ipa_main" +
            " WHERE a.is_deleted = 0 AND m.is_deleted = 0 and a.gmt_create &gt;= #{start} and a.gmt_create &lt;= #{end}" +
            "<if test='bizType == null'>" +
            " and m.biz_type in(10, 20, 30, 40) " +
            "</if>" +
            "<if test='bizType != null'>" +
            " and m.biz_type = #{bizType}" +
            "</if>" +
            "</script>")
    List<Map<String, String>> demandTrendStatistics(@Param("start") Long start, @Param("end") Long end, @Param("bizType") Integer bizType);

    @Select("<script>" +
            "SELECT m.* FROM ipa_manage_main a INNER JOIN ipl_manage_main m ON a.id = m.id_ipa_main WHERE a.is_deleted = 0 AND m.is_deleted = 0 " +
            "<if test='start != null'> " +
            "and a.gmt_create &gt;= #{start} " +
            "</if>" +
            "<if test='end != null'> " +
            "and a.gmt_create &lt; #{end} " +
            "</if>" +
            "</script>")
    List<IplManageMain> getIplManageMain(@Param("start") Long start, @Param("end") Long end);

    @Select("<script>" +
            "select COUNT(1) value, sc.cfg_val name from ipa_manage_main a inner join daily_work_status_package p on a.id = p.id_ipa_main " +
            "inner join daily_m_work_package dmwp on p.id = dmwp.id_package inner join daily_work_status dws on dws.id = dmwp.id_daily_work_status " +
            "inner join sys_cfg sc on dws.type = sc.id " +
            "where a.is_deleted = 0 " +
            "<if test='start != null'>" +
            "and a.gmt_create &gt;= #{start} " +
            "</if>" +
            "<if test='end != null'>" +
            "and a.gmt_create &lt; #{end} " +
            "</if>" +
            "<if test='idRbacDepartment != null'>" +
            "and dws.id_rbac_department =#{idRbacDepartment} " +
            "</if>" +
            "group by sc.id  order by value desc" +
            "</script>")
    List<PieVoByDoc.DataBean> dwsTypeStatistics(@Param("start") Long start, @Param("end") Long end, @Param("idRbacDepartment") Long idRbacDepartment);

    @Select("<script>" +
            "select COUNT(1) value, sc.cfg_val name from ipa_manage_main a inner join daily_work_status_package p on a.id = p.id_ipa_main " +
            "inner join daily_m_work_package dmwp on p.id = dmwp.id_package inner join daily_work_status dws on dws.id = dmwp.id_daily_work_status " +
            "inner join daily_m_work_keyword dmwk on dws.id = dmwk.id_daily_work_status inner join sys_cfg sc on dmwk.id_keyword = sc.id " +
            "where a.is_deleted = 0 " +
            "<if test='start != null'>" +
            "and a.gmt_create &gt;= #{start} " +
            "</if>" +
            "<if test='end != null'>" +
            "and a.gmt_create &lt; #{end} " +
            "</if>" +
            "<if test='idRbacDepartment != null'>" +
            "and dws.id_rbac_department =#{idRbacDepartment} " +
            "</if>" +
            "group by sc.id order by value desc" +
            "</script>")
    List<PieVoByDoc.DataBean> dwsKewWordStatistics(@Param("start") Long start, @Param("end") Long end, @Param("idRbacDepartment") Long idRbacDepartment);
}

