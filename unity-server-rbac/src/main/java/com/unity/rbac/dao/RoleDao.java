
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色
 * @author creator
 * 生成时间 2018-12-12 20:14:54
 */
public interface RoleDao  extends BaseDao<Role> {

    /**
     * 获取正序排列的第一天角色id
     *
     * @param roleIds 数据权限
     * @return 角色id
     * @author gengjiajia
     * @since 2019/08/23 17:59
     */
    @Select("<script> " +
            "   SELECT id FROM rbac_role " +
            "   WHERE is_deleted = 0 " +
            "   <if test='roleIds != null'> " +
            "   AND id IN " +
            "   <foreach item='item' index='index' collection='roleIds' open='(' separator=',' close=')'> " +
            "       #{item} " +
            "   </foreach> " +
            "   </if>" +
            "   ORDER BY i_sort ASC " +
            "   LIMIT 0, 1" +
            "</script>")
    Long getTheFirstRoleBySortAsc(@Param("roleIds") List<Long> roleIds);

    /**
     * 获取倒序排列的第一天角色id
     *
     * @param roleIds 数据权限
     * @return 角色id
     * @author gengjiajia
     * @since 2019/08/23 17:59
     */
    @Select("<script> " +
            "   SELECT id FROM rbac_role " +
            "   WHERE is_deleted = 0 " +
            "   <if test='roleIds != null'> " +
            "   AND id IN " +
            "   <foreach item='item' index='index' collection='roleIds' open='(' separator=',' close=')'> " +
            "       #{item} " +
            "   </foreach> " +
            "   </if>" +
            "   ORDER BY i_sort DESC " +
            "   LIMIT 0, 1" +
            "</script>")
    Long getTheFirstRoleBySortDesc(@Param("roleIds") List<Long> roleIds);
	
}

