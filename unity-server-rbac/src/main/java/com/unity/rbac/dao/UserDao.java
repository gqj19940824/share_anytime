
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 用户
 *
 * @author creator
 * 生成时间 2018-12-12 20:14:54
 */
@Transactional(rollbackFor = Exception.class)
@SuppressWarnings("unused")
public interface UserDao extends BaseDao<User> {

    /**
     * 获取用户信息及关联的组织信息
     *
     * @param userId 用户id
     * @return 用户信息及关联的组织信息
     * @author gengjiajia
     * @since 2019/03/21 14:42
     */
    @Select("SELECT " +
            " u.*, d. NAME AS department " +
            "FROM " +
            " rbac_user u " +
            "INNER JOIN rbac_department d ON u.id_rbac_department = d.id " +
            "WHERE u.id = #{userId}")
    User getUserInfoById(Long userId);

    /**
     * 根据登录账号获取用户信息及所属公司
     *
     * @param loginName 登录账号
     * @return 用户信息
     * @author gengjiajia
     * @since 2019/07/04 18:48
     */
    @Select(" SELECT u.*, d.name as department" +
            " FROM rbac_user u " +
            " LEFT JOIN rbac_department d ON u.id_rbac_department = d.id " +
            " AND d.is_deleted = 0 " +
            " WHERE u.is_deleted = 0 " +
            " AND u.login_name = #{loginName}")
    User getUserInfoByLoginName(String loginName);

    /**
     * 根据指定条件统计用户总数量
     *
     * @param data 包含查询条件
     * @return 用户总数量
     * @author gengjiajia
     * @since 2019/07/08 10:10
     */
    @Select("<script> " +
            " SELECT " +
            "   COUNT(u.id) " +
            " FROM " +
            "   rbac_user u " +
            " LEFT JOIN rbac_department d ON u.id_rbac_department = d.id " +
            " <if test='roleId != null and roleId != 0'> " +
            " INNER JOIN rbac_m_user_role mur ON mur.id_rbac_user = u.id " +
            " INNER JOIN rbac_role r ON r.id = mur.id_rbac_role " +
            " </if> " +
            " WHERE " +
            "   u.is_deleted = 0 " +
            " <if test='idRbacDepartment != null and idRbacDepartment != 0 '> " +
            " AND u.id_rbac_department = #{idRbacDepartment} " +
            " </if> " +
            " <if test='idRbacDepartment != null and idRbacDepartment == 0'> " +
            " AND u.id_rbac_department IS NULL " +
            " </if> " +
            " <if test='loginName != null'> " +
            " AND u.login_name like concat('%',#{loginName},'%') " +
            " </if> " +
            " <if test='roleId != null and roleId != 0'> " +
            " AND mur.is_deleted = 0 " +
            " AND r.id = #{roleId}" +
            " </if> " +
            " <if test='roleId != null and roleId == 0'> " +
            " AND u.id in (" +
            "   SELECT DISTINCT " +
            "       id " +
            "   FROM " +
            "       rbac_user " +
            "   WHERE " +
            "       id NOT IN ( " +
            "           SELECT DISTINCT " +
            "               mur.id_rbac_user " +
            "           FROM " +
            "           rbac_m_user_role mur " +
            "           WHERE mur.is_deleted = 0 " +
            "  ) " +
            " ORDER BY " +
            "  gmt_create DESC " +
            " ) " +
            " </if> " +
            " <if test='dataPermissionIdList != null'> " +
            " AND u.id_rbac_department IN " +
            "   <foreach item='item' index='index' collection='dataPermissionIdList' open='(' separator=',' close=')'> " +
            "     #{item} " +
            "   </foreach> " +
            " </if> " +
            "</script>")
    long countUserTotalNum(Map<String, Object> data);

    /**
     * 根据指定条件统计用户总数量
     *
     * @param data 包含查询条件
     * @return 用户总数量
     * @author gengjiajia
     * @since 2019/07/08 10:10
     */
    @Select("<script> " +
            " SELECT " +
            "   u.*, d.`name` AS department," +
            " <if test='roleId != null and roleId != 0'> " +
            " r.`name` AS groupConcatRoleName " +
            " </if> " +
            " <if test='roleId != null and roleId == 0'> " +
            " '' AS groupConcatRoleName " +
            " </if> " +
            " <if test='roleId == null'> " +
            " group_concat(r.`name`) AS groupConcatRoleName" +
            " </if> " +
            " FROM " +
            "   rbac_user u " +
            " LEFT JOIN rbac_department d ON u.id_rbac_department = d.id " +
            " <if test='roleId != null and roleId != 0'> " +
            " INNER JOIN rbac_m_user_role mur ON mur.id_rbac_user = u.id " +
            " INNER JOIN rbac_role r ON r.id = mur.id_rbac_role " +
            " </if> " +
            " <if test='roleId == null'> " +
            " LEFT JOIN rbac_m_user_role ur ON u.id = ur.id_rbac_user AND ur.is_deleted = 0 " +
            " LEFT JOIN rbac_role r ON r.id = ur.id_rbac_role " +
            " </if> " +
            " WHERE " +
            "   u.is_deleted = 0 " +
            " <if test='idRbacDepartment != null and idRbacDepartment != 0 '> " +
            " AND u.id_rbac_department = #{idRbacDepartment} " +
            " </if> " +
            " <if test='idRbacDepartment != null and idRbacDepartment == 0'> " +
            " AND u.id_rbac_department IS NULL " +
            " </if> " +
            " <if test='loginName != null'> " +
            " AND u.login_name like concat('%',#{loginName},'%') " +
            " </if> " +
            " <if test='roleId != null and roleId != 0'> " +
            " AND mur.is_deleted = 0 " +
            " AND r.id = #{roleId}" +
            " </if> " +
            " <if test='roleId != null and roleId == 0'> " +
            " AND u.id in (" +
            "   SELECT DISTINCT " +
            "       id " +
            "   FROM " +
            "       rbac_user " +
            "   WHERE " +
            "       id NOT IN ( " +
            "           SELECT DISTINCT " +
            "               mur.id_rbac_user " +
            "           FROM " +
            "           rbac_m_user_role mur " +
            "           WHERE mur.is_deleted = 0 " +
            "  ) " +
            " ORDER BY " +
            "  gmt_create DESC " +
            " ) " +
            " </if> " +
            " <if test='dataPermissionIdList != null'> " +
            " AND u.id_rbac_department IN " +
            "   <foreach item='item' index='index' collection='dataPermissionIdList' open='(' separator=',' close=')'> " +
            "     #{item} " +
            "   </foreach> " +
            " </if> " +
            " GROUP BY u.id " +
            " ORDER BY u.gmt_create desc" +
            " LIMIT #{offset},#{limit} " +
            "</script>")
    List<User> findUserListByPage(Map<String, Object> data);

    /**
     * 返回某一个单位的用户id和name的集合
     * @param idRbacDepartment 单位id
    * @return java.util.List<com.unity.rbac.entity.User> 返回符合条件的用户集合
     * @author lifeihong
     * @date 2019/7/11 10:56
    */
    @Select("select id,`name` from rbac_user where is_deleted=0 and id_rbac_department = #{idRbacDepartment}")
    List<User> listUsersByDeptId(@Param("idRbacDepartment") Long idRbacDepartment);

    /**
     * 功能描述 根据单位id查询下面所有的用户
     * @param idRbacDepartment 单位id
     * @return java.util.List<com.unity.rbac.entity.User> 用户集合
     * @author gengzhiqiang
     * @date 2019/7/9 10:51
     */
    @Select(" select id,`name` from rbac_user where is_deleted=0 and id_rbac_department = #{idRbacDepartment} " +
            " order by CONVERT(name USING gbk)  ")
    List<User> listUsersByAllDeptId(@Param("idRbacDepartment") Long idRbacDepartment);

    /**
     * 查询某几个单位下的所有用户，is_deleted为1的也查询
     *
     * @param map {id,单位id的集合}
     * @return java.util.List<com.unity.rbac.entity.User> 返回符合条件的用户集合
     * @author lifeihong
     * @date 2019/7/10 16:58
     */
    @Select("<script>" +
            " select id,name from rbac_user " +
            " <if test='ids!=null'>" +
            " where id_rbac_department in " +
            " <foreach collection='ids' index='index' open='(' separator=',' close=')' item='item'> " +
            "       #{item} " +
            "   </foreach>" +
            " </if>" +
            "</script>")
    List<User> listAllInDepartment(Map<String,Object> map);

    /**
     * 查询某几个单位下的所有用户
     *
     * @param map {id,单位id的集合}
     * @return java.util.List<com.unity.rbac.entity.User> 返回符合条件的用户集合
     * @author lifeihong
     * @date 2019/7/10 16:58
     */
    @Select("<script>" +
            " select id,id_rbac_department from rbac_user where is_deleted=0 " +
            " <if test='ids!=null'>" +
            "  and id_rbac_department in " +
            " <foreach collection='ids' index='index' open='(' separator=',' close=')' item='item'> " +
            "       #{item} " +
            "   </foreach>" +
            " </if>" +
            " AND id_rbac_department !='' " +
            "</script>")
    List<User> listUserInDepartment(Map<String,Object> map);

    /**
     * 获取用户及关联的单位信息列表
     *
     * @return 用户及关联的单位信息列表
     * @author gengjiajia
     * @since 2019/07/12 18:58
     */
    @Select("SELECT " +
            " u.*, d.name AS department,d.gradation_code as gradationCodeRbacDepartment " +
            "FROM " +
            " rbac_user u " +
            "INNER JOIN rbac_department d ON u.id_rbac_department = d.id " +
            "where u.is_deleted = 0")
    List<User> findUserAndDepartmentList();


    /**
     * 根据角色、单位集合获取用户id集合
     *
     * @param map 参数map
     * @return java.util.List<java.lang.Long>
     * @author JH
     * @date 2019/8/8 18:11
     */
    @Select("<script>" +
            " select a.id,a.id_rbac_department from rbac_user a" +
            " inner join rbac_m_user_role b" +
            " on a.id = b.id_rbac_user" +
            " where a.is_deleted = 0 " +
            " and b.is_deleted = 0 " +
            " and b.id_rbac_role = #{roleId} " +
            " and a.id_rbac_department in" +
            " <foreach collection='departmentIds' index='index' open='(' separator=',' close=')' item='item'> " +
            "       #{item} " +
            "   </foreach>" +
            "</script>")
    List<User> getUserIdsByRoleIdAndDepartmentIds(Map<String,Object> map);
}

