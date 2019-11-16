package com.unity.innovation.dao;

import com.unity.innovation.entity.DailyWorkStatus;
import com.unity.innovation.entity.POJO.StatisticsChange;
import com.unity.innovation.entity.generated.IplManageMain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author zhqgeng
 * 生成日期 2019-10-28 20:37
 */
public interface StatisticsPublishWorkDao {

    /**
     * 功能描述 科技局 平均首次时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            "  select AVG((gmt_first_deal-gmt_create)/ (1000*60*60)) from ipl_satb_main  where STATUS IN ('2', '3') and is_deleted = 0  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    Double satbFirst(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 科技局 平均完成时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            "  select AVG((gmt_modified-gmt_create)/ (1000*60*60*24)) from ipl_satb_main  where   STATUS = '3' and is_deleted = 0  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    Double satbFinish(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 企服局 平均首次时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            " SELECT AVG( num ) from ( " +
            "  select AVG((gmt_first_deal-gmt_create)/ (1000*60*60)) num from ipl_esb_main  where  STATUS IN (2, 3) and is_deleted = 0  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            " union " +
            "  select AVG((gmt_first_deal-gmt_create)/ (1000*60*60)) num  from ipl_satb_main  where  STATUS IN (2, 3) and is_deleted = 0  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            " ) t " +
            "</script>")
    Double esbFirst(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 企服局 平均完成时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            " SELECT AVG( num ) from ( " +
            "  select AVG((gmt_modified-gmt_create)/ (1000*60*60*24)) num from ipl_esb_main  where   STATUS = 3  and is_deleted = 0 " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            " union " +
            "  select AVG((gmt_modified-gmt_create)/ (1000*60*60*24)) num from ipl_satb_main  where   STATUS = 3  and is_deleted = 0 " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            " ) t " +
            "</script>")
    Double esbFinish(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);


    /**
     * 功能描述 发改局 平均首次时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            "  select AVG((gmt_first_deal-gmt_create)/ (1000*60*60)) from ipl_darb_main  where  STATUS IN (2, 3) and is_deleted = 0  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    Double darbFirst(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 发改局 平均完成时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            "  select AVG((gmt_modified-gmt_create)/ (1000*60*60*24)) from ipl_darb_main  where   STATUS = 3  and is_deleted = 0 " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    Double darbFinish(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 纪检组 平均首次时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            "  select AVG((gmt_first_deal-gmt_create)/ (1000*60*60)) from ipl_suggestion  where  STATUS IN (2, 3)  and is_deleted = 0 " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    Double sugFirst(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 纪检组 平均完成时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            "  select AVG((gmt_modified-gmt_create)/ (1000*60*60*24)) from ipl_suggestion  where   STATUS = 3 and is_deleted = 0  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    Double sugFinish(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 组织部 平均首次时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            "  select AVG((gmt_first_deal-gmt_create)/ (1000*60*60)) from ipl_od_main  where  STATUS IN (2, 3)  and is_deleted = 0 " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    Double odFirst(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 组织部 平均完成时间
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            "  select AVG((gmt_modified-gmt_create)/ (1000*60*60*24)) from ipl_od_main  where   STATUS = 3  and is_deleted = 0 " +
            "  <if test=\"beginTime !=null\"> " +
            "  and gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    Double odFinish(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 贡献-工作动态
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return 集合
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            "  SELECT dw.id,dw.id_rbac_department  " +
            " FROM ipa_manage_main ipa,daily_work_status_package dp,daily_m_work_package dmp,daily_work_status dw " +
            " WHERE ipa.id = dp.id_ipa_main AND dp.id = dmp.id_package AND dmp.id_daily_work_status = dw.id " +
            " and ipa.is_deleted = 0   and dp.is_deleted = 0 and dmp.is_deleted = 0   and dw.is_deleted = 0 " +
            "  <if test=\"beginTime !=null\"> " +
            "  and ipa.gmt_create &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and ipa.gmt_create &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    List<DailyWorkStatus> workContribution(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 贡献-创新发布清单
     *
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return 集合
     * @author gengzhiqiang
     * @date 2019/10/28 20:55
     */
    @Select("<script>" +
            " select  ipl.id,ipl.biz_type,ipl.id_rbac_department_duty,ipl.SNAPSHOT  " +
            " FROM ipa_manage_main ipa,ipl_manage_main ipl " +
            " WHERE ipa.id = ipl.id_ipa_main  and ipa.is_deleted = 0   and ipl.is_deleted = 0  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and ipa.gmt_create &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and ipa.gmt_create &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    List<IplManageMain> publicContribution(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);


    /**
     * 功能描述 根据表明返回六个月的首次响应时间
     * @param tableName  表名
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/30 14:13
     */
    @Select("<script>" +
            " SELECT FROM_UNIXTIME( s.gmt_create/1000, '%Y年%m月' )  AS month, AVG((gmt_first_deal-gmt_create)/ (1000*60*60)) AS sum " +
            " FROM ${tableName} s " +
            " where s.is_deleted = 0  and   STATUS IN (2, 3) " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    List<Map<String, Object>> changeFirst(@Param("tableName") String tableName, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 根据表明返回六个月的时间
     * @param tableName  表名
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/30 14:13
     */
    @Select("<script>" +
            " SELECT FROM_UNIXTIME( s.gmt_create/1000, '%Y年%m月' )  AS month, AVG((gmt_modified-gmt_create)/ (1000*60*60*24)) AS sum " +
            " FROM ${tableName} s " +
            " where s.is_deleted = 0   and STATUS = 3   " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            "</script>")
    List<Map<String, Object>> changeFinish(@Param("tableName") String tableName, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 五大局六个月数据
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/30 14:13
     */
    @Select("<script>" +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_first_deal) firstSum," +
            "\"satb\" NAME FROM  ipl_satb_main s WHERE s.is_deleted = 0  and  STATUS IN (2, 3)  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_first_deal) firstSum," +
            "\"od\" NAME FROM  ipl_od_main s WHERE s.is_deleted = 0  and  STATUS IN (2, 3)  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_first_deal) firstSum," +
            "\"darb\" NAME FROM  ipl_darb_main s WHERE s.is_deleted = 0  and  STATUS IN (2, 3)  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_first_deal) firstSum," +
            "\"esb\" NAME FROM  ipl_esb_main s WHERE s.is_deleted = 0  and  STATUS IN (2, 3)  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_first_deal) firstSum," +
            "\"sug\" NAME FROM  ipl_suggestion s WHERE s.is_deleted = 0  and  STATUS IN (2, 3)  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  " +
            "</script>")
    List<StatisticsChange> changeFirstAll(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 五大局六个月数据
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/30 14:13
     */
    @Select("<script>" +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_modified) modifiedSum," +
            "\"satb\" NAME FROM  ipl_satb_main s WHERE s.is_deleted = 0  and  STATUS = 3   " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_modified) modifiedSum," +
            "\"od\" NAME FROM  ipl_od_main s WHERE s.is_deleted = 0  and  STATUS = 3   " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_modified) modifiedSum," +
            "\"darb\" NAME FROM  ipl_darb_main s WHERE s.is_deleted = 0  and STATUS = 3   " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_modified) modifiedSum," +
            "\"esb\" NAME FROM  ipl_esb_main s WHERE s.is_deleted = 0  and  STATUS = 3  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_modified) modifiedSum," +
            "\"sug\" NAME FROM  ipl_suggestion s WHERE s.is_deleted = 0  and  STATUS = 3  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  " +
            "</script>")
    List<StatisticsChange> changeFinishAll(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 五大局六个月数据
     * @param bizType 类型
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/30 14:13
     */
    @Select("<script>" +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(*) count " +
            " FROM  ipl_time_out_log s WHERE s.is_deleted = 0  " +
            "  <if test=\"bizType !=null\"> " +
            "  and s.biz_Type = #{bizType} " +
            "  </if>" +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_create &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_create &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month " +
            "</script>")
    List<StatisticsChange> overDealTimes(@Param("bizType") Integer bizType, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 科技创新局六个月数据
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/30 14:13
     */
    @Select("<script>" +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(*) count " +
            " FROM  ipl_time_out_log s WHERE s.is_deleted = 0  " +
            "  and s.biz_Type in ( 20 , 30 ) " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_create &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_create &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month " +
            "</script>")
    List<StatisticsChange> overDealTimesForTwo( @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 科技创新局六个月数据
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/30 14:13
     */
    @Select("<script>" +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_first_deal) firstSum," +
            "\"satb\" NAME FROM  ipl_satb_main s WHERE s.is_deleted = 0  and  STATUS IN (2, 3)  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_first_deal) firstSum," +
            "\"esb\" NAME FROM  ipl_esb_main s WHERE s.is_deleted = 0  and  STATUS IN (2, 3)  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_first_deal &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_first_deal &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  " +
            "</script>")
    List<StatisticsChange> changeFirstTwo(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    /**
     * 功能描述 科技创新局六个月数据
     * @param beginTime 开始时间
     * @param endTime   截止时间
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/30 14:13
     */
    @Select("<script>" +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_modified) modifiedSum," +
            "\"satb\" NAME FROM  ipl_satb_main s WHERE s.is_deleted = 0  and  STATUS = 3   " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  union " +
            "SELECT FROM_UNIXTIME(s.gmt_create / 1000,'%Y年%m月') AS month,count(gmt_create) count, SUM(gmt_create) createSum, SUM(gmt_modified) modifiedSum," +
            "\"esb\" NAME FROM  ipl_esb_main s WHERE s.is_deleted = 0  and  STATUS = 3  " +
            "  <if test=\"beginTime !=null\"> " +
            "  and s.gmt_modified &gt;= #{beginTime} " +
            "  </if>" +
            "  <if test=\"endTime !=null\"> " +
            "  and s.gmt_modified &lt;= #{endTime} " +
            "  </if>" +
            " GROUP BY month  " +
            "</script>")
    List<StatisticsChange> changeFinishTwo(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);
}
