
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.Resource;
import com.unity.rbac.entity.RoleResource;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色资源
 * @author creator
 * 生成时间 2018-12-12 20:14:50
 */
public interface RoleResourceDao  extends BaseDao<RoleResource> {

    /*
     * 查询指定角色关联的资源
     *
     * @param  roleId 角色id
     * @return 指定角色关联的资源
     * @author gengjiajia
     * @since 2018/12/15 14:17
     */
    /*@Select("SELECT r.id FROM rbac_resource r " +
            "INNER JOIN rbac_m_role_resource rr ON r.id = rr.id_rbac_resource INNER JOIN rbac_role ro ON rr.id_rbac_role = ro.id " +
            "WHERE r.is_deleted = 0 AND rr.is_deleted = 0 AND ro.is_deleted = 0 AND ro.id = #{roleId}")
    List<Map<String,Object>> selectResourceByRoleId(Long roleId);*/

    /*
     * 查询指定角色关联的资源id集
     *
     * @param  roleId 角色id
     * @return 指定角色关联的资源
     * @author gengjiajia
     * @since 2018/12/15 14:17
     */
    /*@Select("SELECT r.id FROM rbac_resource r " +
            "INNER JOIN rbac_m_role_resource rr ON r.id = rr.id_rbac_resource INNER JOIN rbac_role ro ON rr.id_rbac_role = ro.id " +
            "WHERE r.is_deleted = 0 AND rr.is_deleted = 0 AND ro.is_deleted = 0 AND ro.id = #{roleId}")
    List<Long> selectResourceIdsByRole(Long roleId);*/

    /*
     * 查询指定角色关联的功能资源id集
     *
     * @param  roleId 角色id
     * @return 指定角色关联的资源
     * @author gengjiajia
     * @since 2018/12/15 14:17
     */
    /*@Select("SELECT r.id FROM rbac_resource r " +
            "INNER JOIN rbac_m_role_resource rr ON r.id = rr.id_rbac_resource INNER JOIN rbac_role ro ON rr.id_rbac_role = ro.id " +
            "WHERE r.is_deleted = 0 AND rr.is_deleted = 0 AND ro.is_deleted = 0 AND r.resource_type != 4 AND ro.id = #{roleId}")
    List<Long> selectModuleResourceIdsByRole(Long roleId);*/

    /*
     * 查询指定角色关联的接口资源id集
     *
     * @param  roleId 角色id
     * @return 指定角色关联的资源
     * @author gengjiajia
     * @since 2018/12/15 14:17
     */
    /*@Select("SELECT r.id FROM rbac_resource r " +
            "INNER JOIN rbac_m_role_resource rr ON r.id = rr.id_rbac_resource INNER JOIN rbac_role ro ON rr.id_rbac_role = ro.id " +
            "WHERE r.is_deleted = 0 AND rr.is_deleted = 0 AND ro.is_deleted = 0 AND r.resource_type = 4 AND ro.id = #{roleId}")
    List<Long> selectApiResourceIdsByRole(Long roleId);*/


    /**
     * 批量插入
     *
     * @param roleResourceList 角色与资源关系集
     * @author gengjiajia
     * @since 2019/05/10 13:51
     */
    void insertBatch(List<RoleResource> roleResourceList);

    /**
     * 通过角色id查询所关联的资源
     *
     * @param  roleId 角色id
     * @return 关联的资源
     * @author gengjiajia
     * @since 2019/07/03 09:51
     */
    @Select("SELECT " +
            "   r.* " +
            "FROM rbac_resource r INNER JOIN rbac_m_role_resource rr ON r.id = rr.id_rbac_resource " +
            "WHERE r.is_deleted = 0 " +
            "AND rr.is_deleted = 0 " +
            "AND rr.id_rbac_role = #{roleId}")
    List<Resource> getRoleLinkedResourceListByRoleId(Long roleId);

    /**
     * 通过用户id查询用户关联的角色来获取角色关联的资源列表
     *
     * @param  userId 用户id
     * @return 角色关联的资源列表
     * @author gengjiajia
     * @since 2019/07/03 18:59
     */
    @Select("SELECT " +
            " re.* " +
            "FROM " +
            " rbac_resource re " +
            "INNER JOIN rbac_m_role_resource rr ON re.id = rr.id_rbac_resource " +
            "INNER JOIN rbac_role r ON rr.id_rbac_role = r.id " +
            "INNER JOIN rbac_m_user_role ur ON ur.id_rbac_role = r.id " +
            "WHERE " +
            " re.is_deleted = 0 " +
            "AND rr.is_deleted = 0 " +
            "AND r.is_deleted = 0 " +
            "AND ur.is_deleted = 0 " +
            "AND ur.id_rbac_user = #{userId}")
    List<Resource> getRoleResourceCodeListByUserId(Long userId);

    /*
     * 通过角色id删除角色与资源关系
     *
     * @param  id 角色id
     * @author gengjiajia
     * @since 2019/07/04 19:15
     */
    /*@Delete("DELETE FROM `rbac_m_role_resource` WHERE id_rbac_role = #{id}")
    void deleteByRoleId(Long id);*/
}

