
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.UserResource;

import java.util.List;

/**
 * 用户资源
 * @author creator
 * 生成时间 2018-12-12 20:14:52
 */
public interface UserResourceDao  extends BaseDao<UserResource> {

    /*
     * 查询指定用户相关的功能资源ID集
     *
     * @param  userId 指定用户id
     * @return 功能资源ID集
     * @author gengjiajia
     * @since 2018/12/26 15:57  
     */
    /*@Select("SELECT r.id FROM rbac_resource r " +
            "INNER JOIN rbac_m_user_resource ur ON r.id = ur.id_rbac_resource INNER JOIN rbac_user u ON ur.id_rbac_user = u.id " +
            "WHERE r.is_deleted = 0 AND ur.is_deleted = 0 AND u.is_deleted = 0 AND resource_type != 4 AND u.id = #{userId}")
    List<Long> selectModuleResourceIdsByUserId(Long userId);*/

    /*
     * 查询指定用户相关的API资源ID集
     *
     * @param  userId 指定用户id
     * @return API资源ID集
     * @author gengjiajia
     * @since 2018/12/26 15:57
     */
    /*@Select("SELECT r.id FROM rbac_resource r " +
            "INNER JOIN rbac_m_user_resource ur ON r.id = ur.id_rbac_resource INNER JOIN rbac_user u ON ur.id_rbac_user = u.id " +
            "WHERE r.is_deleted = 0 AND ur.is_deleted = 0 AND u.is_deleted = 0 AND resource_type = 4 AND u.id = #{userId}")
    List<Long> selectApiResourceIdsByUserId(Long userId);*/

    /*
     * 查询指定用户相关的功能资源
     *
     * @param  userId 指定用户id
     * @return 功能资源ID集
     * @author gengjiajia
     * @since 2018/12/26 15:57
     */
    /*@Select("SELECT r.id,r.gradation_code FROM rbac_resource r " +
            "INNER JOIN rbac_m_user_resource ur ON r.id = ur.id_rbac_resource INNER JOIN rbac_user u ON ur.id_rbac_user = u.id " +
            "WHERE r.is_deleted = 0 AND ur.is_deleted = 0 AND u.is_deleted = 0 AND resource_type != 4 AND u.id = #{userId}")
    List<Resource> selectModuleResourceByUserId(Long userId);*/

    /*
     * 根据用户id获取用户与功能资源关联关系
     *
     * @param userId 用户id
     * @return 用户与功能资源关联关系
     * @author gengjiajia
     * @since 2019/01/04 09:34  
     */
    /*@Select("SELECT ur.id,ur.id_rbac_resource,ur.id_rbac_user,ur.function_power FROM rbac_m_user_resource ur " +
            "INNER JOIN rbac_resource r ON r.id = ur.id_rbac_resource INNER JOIN rbac_user u ON ur.id_rbac_user = u.id " +
            "WHERE r.is_deleted = 0 AND ur.is_deleted = 0 AND u.is_deleted = 0 AND resource_type != 4 AND u.id = #{userId}")
    List<UserResource> selectModuleUserResourceByUserId(Long userId);*/

    /*
     * 获取用户与资源相关的功能按钮
     *
     * @param  resourceCode 资源id
     * @param userId 用户id
     * @return 功能按钮
     * @author gengjiajia
     * @since 2019/01/04 09:36  
     */
    /*@Select("SELECT ur.function_power FROM rbac_m_user_resource ur " +
            "INNER JOIN rbac_resource r ON r.id = ur.id_rbac_resource " +
            "WHERE ur.is_deleted = 0 " +
            "AND r.gradation_code =  #{resourceCode} " +
            "AND ur.id_rbac_user = #{userId}")
    List<String> selectFunctionPowerByResourceCodeAndUserId(@Param("resourceCode") String resourceCode, @Param("userId") Long userId);*/

    /**
     * 批量插入
     *
     * @param userResourceList 用户与资源关系集
     * @author gengjiajia
     * @since 2019/05/10 13:51
     */
    void insertBatch(List<UserResource> userResourceList);

    /*
     * 通过用户id删除用户与资源关系
     *
     * @param  id 用户id
     * @author gengjiajia
     * @since 2019/07/04 19:10
     */
    /*@Delete("DELETE FROM `rbac_m_user_resource` WHERE id_rbac_user = #{id}")
    void deleteByUserId(Long id);*/
}

