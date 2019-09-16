
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.User;
import com.unity.rbac.entity.UserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色
 * @author creator
 * 生成时间 2018-12-12 20:14:52
 */
public interface UserRoleDao  extends BaseDao<UserRole> {

    /**
     * 根据用户查询关联的角色id
     *
     * @param userId 用户id
     * @return 用户查询关联的角色信息列表
     * @author gengjiajia
     * @since 2018/12/14 17:23
     */
    @Select("SELECT " +
            " ur.id_rbac_role " +
            "FROM " +
            " rbac_m_user_role ur " +
            "INNER JOIN rbac_user u ON ur.id_rbac_user = u.id " +
            "WHERE " +
            " ur.is_deleted = 0 " +
            "AND u.id = #{userId}")
    List<Long> selectRoleIdsByUserId(Long userId);

    /**
     * 批量获取用户所有的角色名称列表
     *
     * @param  userIdList 用户id集
     * @return 角色名称列表
     * @author gengjiajia
     * @since 2019/08/28 20:27
     */
    @Select("<script> " +
            " SELECT " +
            " group_concat(r.`name`) AS groupConcatRoleName, " +
            " u.id as id " +
            "FROM " +
            " rbac_role r " +
            "INNER JOIN rbac_m_user_role ur ON r.id = ur.id_rbac_role " +
            "INNER JOIN rbac_user u ON u.id = ur.id_rbac_user " +
            "WHERE " +
            " ur.is_deleted = 0 " +
            " AND r.is_deleted = 0 " +
            " AND ur.id_rbac_user IN " +
            "   <foreach item='item' index='index' collection='userIdList' open='(' separator=',' close=')'> " +
            "     #{item} " +
            "   </foreach> " +
            "GROUP BY ur.id_rbac_user " +
            "</script>")
    List<User> getGroupConcatRoleNameListByUserIdIn(@Param("userIdList") List<Long> userIdList);
}

