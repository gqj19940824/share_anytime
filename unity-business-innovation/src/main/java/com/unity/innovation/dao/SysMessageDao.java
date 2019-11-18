
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.SysMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * sys_message
 *
 * @author G
 * 生成时间 2019-09-23 09:39:17
 */
public interface SysMessageDao extends BaseDao<SysMessage> {

    /**
     * 统计列表总条数
     *
     * @param  paramMap 查询参数
     * @return 列表总条数
     * @author gengjiajia
     * @since 2019/09/23 10:29
     */
    long countListTotalByParam(Map<String, Object> paramMap);

    /**
     * 获取分页列表
     *
     * @param  paramMap 查询参数
     * @return 列表数据
     * @author gengjiajia
     * @since 2019/09/23 11:04
     */
    List<SysMessage> findPageListByParam(Map<String, Object> paramMap);

    /**
     * 通过目标用户id获取已有的数据类型
     *
     * @param  targetUserId 目标用户id
     * @return 数据类型列表
     * @author G
     * @since 2019/11/18 10:46
     */
    List<Integer> findDataSourceClassByTargetUserId(@Param("targetUserId") Long targetUserId);
}

