
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.UserDepartment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户机构权限
 *
 * @author creator
 * 生成时间 2018-12-12 20:14:51
 */
public interface UserDepartmentDao extends BaseDao<UserDepartment> {

    /*
     * 查询指定用户的组织架构信息
     *
     * @param userId 指定用户id
     * @return 指定用户的组织架构
     * @author zhangxiaogang
     * @since 2019/3/5 20:13
     */
    /*@Select("SELECT d.* FROM rbac_department d " +
            "INNER JOIN rbac_m_user_department ud ON d.id = ud.id_rbac_department INNER JOIN rbac_user u ON u.id = ud.id_rbac_user " +
            "WHERE u.is_deleted = 0 AND d.is_deleted = 0 AND ud.is_deleted = 0 AND u.id = #{userId}")
    List<Department> selectDepartmentsByUserId(Long userId);*/

    /*
     * 查询指定用户的组织架构信息
     *
     * @return 指定用户的组织架构
     * @author zhangxiaogang
     * @since 2019/3/7 9:13
     */
    /*@Select("SELECT d.id,d.name,d.i_level as level,d.id_parent,d.gradation_code,d.dep_type,d.creator,d.editor,d.i_sort as sort,d.is_deleted FROM rbac_department d " +
            "WHERE d.is_deleted = 0 ORDER BY d.i_sort ASC")
    List<Department> selectAllDepartments();*/

    /**
     * 查询指定用户数据权限id集
     *
     * @param  userId 用户id
     * @return 数据权限id集
     * @author gengjiajia
     * @since 2019/07/08 09:35
     */
    @Select("SELECT " +
            " DISTINCT c.id " +
            "FROM " +
            " rbac_department c " +
            "INNER JOIN rbac_department p ON c.gradation_code LIKE CONCAT(p.gradation_code, '%') " +
            "INNER JOIN rbac_user u ON u.id_rbac_department = p.id " +
            "WHERE u.is_deleted = 0 " +
            "AND p.is_deleted = 0 " +
            "AND c.is_deleted = 0 " +
            "AND u.id = #{userId}")
    List<Long> findDataPermissionIdListByUserId(Long userId);

    /**
     * 通过级次编码获取拼接起来的名称
     *
     * @param  codes 级次编码集
     * @return 拼接起来的名称
     * @author gengjiajia
     * @since 2019/07/29 16:05
     */
    @Select("<script> " +
            "   SELECT GROUP_CONCAT('/',`name`) " +
            "   FROM rbac_department " +
            "   WHERE is_deleted = 0 " +
            "   AND gradation_code in " +
            "   <foreach item='item' index='index' collection='codes' open='(' separator=',' close=')'> " +
            "     #{item} " +
            "   </foreach> " +
            "</script>")
    String getImmediateSupervisorName(@Param("codes") List<String> codes);
}

