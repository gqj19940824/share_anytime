
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.Resource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 资源
 * @author creator
 * 生成时间 2018-12-12 20:14:53
 */
public interface ResourceDao  extends BaseDao<Resource> {

    /**
     * 根据资源id集获取对应的资源code
     *
     * @param  resourceIds 资源id集
     * @return 取所有的对应的资源
     * @author gengjiajia
     * @since 2018/12/14 20:28
     */
    @Select("<script> " +
            "SELECT r.gradation_code FROM rbac_resource r " +
            "WHERE r.is_deleted = 0 " +
            "AND r.id IN " +
            "<foreach item='item' index='index' collection='resourceIds' " +
            "open='(' separator=',' close=')'> " +
            "#{item} " +
            "</foreach> " +
            "</script>")
    List<String> selectResourceCodeByIdIn(@Param("resourceIds") Long[] resourceIds);

    /**
     * 根据资源id集获取对应的资源url
     *
     * @param  resourceIds 资源id集
     * @return 取所有的对应的资源
     * @author gengjiajia
     * @since 2018/12/14 20:28
     */
    @Select("<script> " +
            "SELECT r.resource_url FROM rbac_resource r " +
            "WHERE r.is_deleted = 0 " +
            "AND r.id IN " +
            "<foreach item='item' index='index' collection='resourceIds' " +
            "open='(' separator=',' close=')'> " +
            "#{item} " +
            "</foreach> " +
            "</script>")
    List<String> selectResourceUrlByIdIn(@Param("resourceIds") Long[] resourceIds);

    /**
     * 根据身份及资源类型获取资源ID
     *
     * @param  identityId 身份id
     * @param resourceTypes 4 接口资源  !4 功能资源
     * @return 资源id
     * @author gengjiajia
     * @since 2019/01/03 16:50
     */
    @Select("<script> " +
            "SELECT r.id FROM rbac_resource r " +
            "INNER JOIN rbac_m_resource_identity ri ON r.id = ri.id_rbac_resource " +
            "INNER JOIN rbac_identity i ON ri.id_rbac_identity = i.id " +
            "WHERE i.id = #{identityId} " +
            "AND r.resource_type IN " +
            "<foreach item='item' index='index' collection='resourceTypes' " +
            "open='(' separator=',' close=')'> " +
            "#{item} " +
            "</foreach> " +
            "AND r.is_deleted = 0 " +
            "AND ri.is_deleted = 0 " +
            "</script>")
    List<Long> selectResourceIdsByIdentityId(@Param("identityId") Long identityId, @Param("resourceTypes") Integer[] resourceTypes);

    /**
     * 根据用户及资源类型获取资源ID
     *
     * @param  userId 用户id
     * @param resourceTypes 4 接口资源  !4 功能资源
     * @return 资源id
     * @author gengjiajia
     * @since 2019/01/03 16:50
     */
    @Select("<script> " +
            "SELECT r.id FROM rbac_resource r " +
            "INNER JOIN rbac_m_user_resource ur ON r.id = ur.id_rbac_resource " +
            "INNER JOIN rbac_user u ON ur.id_rbac_user = u.id " +
            "WHERE u.id = #{userId} " +
            "AND r.resource_type IN " +
            "<foreach item='item' index='index' collection='resourceTypes' " +
            "open='(' separator=',' close=')'> " +
            "#{item} " +
            "</foreach> " +
            "AND r.is_deleted = 0 " +
            "AND ur.is_deleted = 0 " +
            "</script>")
    List<Long> selectResourceIdsByUserId(@Param("userId") Long userId, @Param("resourceTypes") Integer[] resourceTypes);

    /**
     * 根据角色及资源类型获取资源ID
     *
     * @param  roleIds 角色id集
     * @param resourceTypes 4 接口资源  !4 功能资源
     * @return 资源id
     * @author gengjiajia
     * @since 2019/01/03 16:50
     */
    @Select("<script> " +
            "SELECT r.id FROM rbac_resource r " +
            "INNER JOIN rbac_m_role_resource rr ON r.id = rr.id_rbac_resource " +
            "INNER JOIN rbac_role ro ON ro.id = rr.id_rbac_role " +
            "WHERE r.is_deleted = 0 " +
            "AND rr.is_deleted = 0 " +
            "AND r.resource_type IN " +
            "<foreach item='item' index='index' collection='resourceTypes' " +
            "open='(' separator=',' close=')'> " +
            "#{item} " +
            "</foreach> " +
            "AND ro.id IN " +
            "<foreach item='item' index='index' collection='roleIds' " +
            "open='(' separator=',' close=')'> " +
            "#{item} " +
            "</foreach> " +
            "</script>")
    List<Long> selectResourceIdsByRoleIds(@Param("roleIds") Long[] roleIds, @Param("resourceTypes") Integer[] resourceTypes);
}

