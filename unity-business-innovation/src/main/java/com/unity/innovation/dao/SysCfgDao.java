
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.SysCfg;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统配置
 * @author zhang
 * 生成时间 2019-09-17 14:53:55
 */
public interface SysCfgDao  extends BaseDao<SysCfg>{

    @Select("<script>" +
            "       SELECT c.id, c.cfg_val FROM sys_cfg c WHERE c.is_deleted = 0 AND c.id IN" +
            "        <foreach collection='ids' item='id' open='(' close=')' separator=','>#{id}</foreach>" +
            "</script>")
    List<Map<String, Object>> getValues(@Param("ids") Set<Long> ids);
}

