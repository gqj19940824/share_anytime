
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.PmInfoDept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 企业信息发布管理
 * @author zhang
 * 生成时间 2019-10-15 15:33:01
 */
public interface PmInfoDeptDao  extends BaseDao<PmInfoDept>{

    @Update("<script>" +
            "UPDATE pm_info_dept p SET p.status = 30, p.id_ipa_main = NULL WHERE" +
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

