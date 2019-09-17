
package com.unity.rbac.dao;


import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.Department;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 组织机构
 *
 * @author creator
 * 生成时间 2018-12-12 20:14:49
 */
@Transactional(rollbackFor = Exception.class)
public interface DepartmentDao extends BaseDao<Department> {

    /**
     * 根据单位级别查询单位的id列表
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-08-01 15:31
     */
    @Select("SELECT d.id FROM rbac_department d WHERE d.i_level = #{level} AND d.is_deleted = 0")
    List<Long> getDepartmentIdsByLevel(Integer level);

    /**
     * 获取组织架构所属人员ID
     *
     * @param departmentIds 组织ID列表
     * @return 组织架构所属人员ID
     * @author jiaww
     * @since 2019/02/20 11:14
     */
    @Select("select t.id_rbac_user from rbac_m_user_department t where t.is_deleted = 0 and t.id_rbac_department in(#{departmentIds})")
    List<Long> getUserIdsByDepartmentIds(String departmentIds);

    /**
     * 功能描述  根据多个单位id获取相对应的的user信息
     *
     * @param list 返回集合
     * @return java.util.List<com.unity.rbac.entity.Department>
     * @author gengzhiqiang
     * @date 2019/7/9 14:43
     */
    @Select("<script>" +
            " select id,name from rbac_department where is_deleted=0 and id in " +
            " <foreach collection=\"list\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "  #{item}" +
            " </foreach>" +
            "</script>")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "users", many = @Many(
                    select = "com.unity.rbac.dao.UserDao.listUsersByDeptId",
                    fetchType = FetchType.EAGER))
    })
    List<Department> listUserInDept(@Param("list") Long[] list);


    /**
     * 返回 集团单位置顶，其他按单位名排序的，相同的单位按时间倒序 的 集合
     *
     * @param ids 主键集合
     * @return Department集合
     * @author JH
     * @date 2019/7/9 15:37
     */
    @Select("<script> " +
            " SELECT  CASE WHEN" +
            " i_level = 1 THEN 1 ELSE 0 END" +
            " i_level , id , name  " +
            " FROM rbac_department " +
            " WHERE is_deleted = 0 " +
            " AND                  " +
            " id IN " +
            " <foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'> " +
            "       #{item} " +
            "   </foreach> " +
            "ORDER BY i_level DESC,CONVERT(name USING gbk) DESC" +
            "</script>")
    List<Department> getNameOrderdDepartmentsByIds(@Param("ids") List<Long> ids);

    /**
     * 功能描述 获取所有的单位附带用户列表
     *
     * @return java.util.List<com.unity.rbac.entity.Department> 返回单位集合
     * @author gengzhiqiang
     * @date 2019/7/9 14:51
     */
    @Select("<script>" +
            " select id, name from rbac_department where is_deleted = 0 order by CONVERT(name USING gbk) " +
            "</script>")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "users", many = @Many(
                    select = "com.unity.rbac.dao.UserDao.listUsersByAllDeptId",
                    fetchType = FetchType.EAGER))
    })
    List<Department> listUserInDeptAll();

    /**
     * 功能描述 条件判断获取单位名称
     *
     * @return java.util.List<com.unity.rbac.entity.Department>
     * @author gengzhiqiang
     * @date 2019/7/10 17:21
     */
    @Select(" select id,name from rbac_department ")
    List<Department> getAllDeptNames();

    /**
     * 功能描述
     *
     * @return java.util.List<com.unity.rbac.entity.Department>
     * @author gengzhiqiang
     * @date 2019/8/21 10:08
     */
    @Select(" select DISTINCT d.id,d.name from rbac_department d  ,rbac_user u " +
            " where  d.i_level in (2,3) and d.id=u.id_rbac_department and d.is_deleted=0 and u.is_deleted=0  ")
    List<Department> listDepartmentListForNotice();

    /**
     * 修改排序
     *
     * @param id     主键
     * @param sortId 排序id
     * @author JH
     * @date 2019/9/9 14:09
     */
    @Update("update rbac_department set i_sort =#{sortId} where is_deleted = '0' and id = #{id} ")
    void changeOrder(@Param("id") long id, @Param("sortId") long sortId);

}

