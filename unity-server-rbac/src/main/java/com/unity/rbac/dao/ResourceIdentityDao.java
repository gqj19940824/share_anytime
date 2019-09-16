
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.Resource;
import com.unity.rbac.entity.ResourceIdentity;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 资源身份
 * @author creator
 * 生成时间 2018-12-12 20:14:50
 */
public interface ResourceIdentityDao  extends BaseDao<ResourceIdentity> {


    /**
     * 查询指定身份关联的资源
     *
     * @param  identityId 指定身份
     * @return 指定身份关联的资源
     * @author gengjiajia
     * @since 2018/12/15 13:45
     */
    @Select("SELECT r.id, r.`name`, r.gradation_code " +
            "FROM rbac_resource r LEFT JOIN rbac_m_resource_identity ri ON r.id = ri.id_rbac_resource LEFT JOIN rbac_identity i ON ri.id_rbac_identity = i.id " +
            "WHERE i.is_deleted = 0 AND r.is_deleted = 0 AND ri.is_deleted = 0 AND i.id = #{identityId}")
    List<Map<String,Object>> selectResourceByIdentity(Long identityId);

    /**
     * 查询指定身份关联的资源id集
     *
     * @param  identityId 指定身份
     * @return 指定身份关联的资源
     * @author gengjiajia
     * @since 2018/12/15 13:45
     */
    @Select("SELECT r.id " +
            "FROM rbac_resource r INNER JOIN rbac_m_resource_identity ri ON r.id = ri.id_rbac_resource INNER JOIN rbac_identity i ON ri.id_rbac_identity = i.id " +
            "WHERE i.is_deleted = 0 AND r.is_deleted = 0 AND ri.is_deleted = 0 AND i.id = #{identityId}")
    List<Long> selectResourceIdsByIdentity(Long identityId);

    /**
     * 查询指定身份关联的功能资源id集
     *
     * @param  identityId 指定身份
     * @return 指定身份关联的资源
     * @author gengjiajia
     * @since 2018/12/15 13:45
     */
    @Select("SELECT r.id " +
            "FROM rbac_resource r INNER JOIN rbac_m_resource_identity ri ON r.id = ri.id_rbac_resource INNER JOIN rbac_identity i ON ri.id_rbac_identity = i.id " +
            "WHERE i.is_deleted = 0 AND r.is_deleted = 0 AND ri.is_deleted = 0 AND r.resource_type != 4 AND i.id = #{identityId}")
    List<Long> selectModuleResourceIdsByIdentity(Long identityId);

    /**
     * 查询指定身份关联的接口资源id集
     *
     * @param  identityId 指定身份
     * @return 指定身份关联的资源
     * @author gengjiajia
     * @since 2018/12/15 13:45
     */
    @Select("SELECT r.id " +
            "FROM rbac_resource r INNER JOIN rbac_m_resource_identity ri ON r.id = ri.id_rbac_resource INNER JOIN rbac_identity i ON ri.id_rbac_identity = i.id " +
            "WHERE i.is_deleted = 0 AND r.is_deleted = 0 AND ri.is_deleted = 0 AND r.resource_type = 4 AND i.id = #{identityId}")
    List<Long> selectApiResourceIdsByIdentity(Long identityId);

    /**
     * 批量插入
     *
     * @param  newList 身份与资源关系
     * @author gengjiajia
     * @since 2019/07/03 10:48
     */
    void insertBatch(List<ResourceIdentity> newList);

    /**
     * 通过身份id获取关联的资源
     *
     * @param  identityId 身份id
     * @return 关联的资源级次编码
     * @author gengjiajia
     * @since 2019/07/03 11:15
     */
    @Select("SELECT " +
            "   r.* " +
            "FROM rbac_resource r INNER JOIN rbac_m_resource_identity ri ON r.id = ri.id_rbac_resource " +
            "WHERE " +
            "   r.is_deleted = 0 " +
            "   AND ri.is_deleted = 0 " +
            "   AND ri.id_rbac_identity = #{identityId}")
    List<Resource> getIdentityLinkedResourceListByIdentityId(Long identityId);

    /*
     * 通过身份id删除身份与资源关系
     *
     * @param  id 身份id
     * @author gengjiajia
     * @since 2019/07/04 19:20
     */
    /*@Delete("DELETE FROM `rbac_m_resource_identity` WHERE id_rbac_identity = #{id}")
    void deleteByIdentityId(Long id);*/
}

