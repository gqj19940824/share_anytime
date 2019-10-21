

package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.generated.IplManageMain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 创新发布清单-发布管理主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:37
 */
public interface IplManageMainDao  extends BaseDao<IplManageMain>{

    @Update("<script>" +
            "UPDATE ipl_manage_main p SET p.status = 30, p.id_ipa_main = NULL WHERE" +
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

