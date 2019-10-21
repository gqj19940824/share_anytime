
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.DailyWorkStatusPackage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 创新日常工作管理-工作动态需求表
 * @author zhang
 * 生成时间 2019-09-17 11:17:02
 */
public interface DailyWorkStatusPackageDao  extends BaseDao<DailyWorkStatusPackage>{

    @Update("<script>" +
            "UPDATE daily_work_status_package p SET p.state = 30, p.id_ipa_main = NULL WHERE" + // TODO 空格
            "<if test='ids != null'>" +
            "p.id IN" +
            "<foreach collection='ids' item='id' open='(' close=')' separator=','>" +
            "#{id}" +
            "</foreach>" +
            "</if>" +
            "<if test='idIpaMains != null'>" +
            "p.id_ipa_main IN" +
            "<foreach collection='idIpaMains' item='idIpaMain' open='(' close=')' separator=','>" +
            "#{idIpaMain}" +
            "</foreach>" +
            "</if>" +
            "</script>")
    void updateIdIpaMain(@Param("ids") List<Long> ids, @Param("idIpaMains") List<Long> idIpaMains);
}

