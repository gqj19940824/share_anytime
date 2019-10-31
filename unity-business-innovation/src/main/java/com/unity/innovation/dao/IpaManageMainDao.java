
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.controller.vo.PieVoByDoc;
import com.unity.innovation.entity.generated.IpaManageMain;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 创新发布活动-管理-主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:33
 */
public interface IpaManageMainDao  extends BaseDao<IpaManageMain>{

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
            "group by sc.id " +
            "</script>")
    List<PieVoByDoc.DataBean> dwsTypeStatistics(Long start, Long end, Long idRbacDepartment);

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
            "group by sc.id " +
            "</script>")
    List<PieVoByDoc.DataBean> dwsKewWordStatistics(Long start, Long end, Long idRbacDepartment);
}

