
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
     * 修改排序
     *
     * @param id     主键
     * @param sortId 排序id
     * @author JH
     * @date 2019/9/9 14:09
     */
    @Update("update rbac_department set i_sort =#{sortId} where is_deleted = '0' and id = #{id} ")
    void changeOrder(@Param("id") long id, @Param("sortId") long sortId);

    /**
     * 获取正序排列的第一天单位id
     *
     * @param departmentIds 数据权限
     * @return 单位id
     * @author gengjiajia
     * @since 2019/08/23 17:59
     */
    @Select("<script> " +
            "   SELECT id FROM rbac_department " +
            "   WHERE is_deleted = 0 " +
            "   <if test='departmentIds != null'> " +
            "   AND id IN " +
            "   <foreach item='item' index='index' collection='departmentIds' open='(' separator=',' close=')'> " +
            "       #{item} " +
            "   </foreach> " +
            "   </if>" +
            "   ORDER BY i_sort ASC " +
            "   LIMIT 0, 1" +
            "</script>")
    Long getTheFirstDepartmentBySortAsc(@Param("departmentIds") List<Long> departmentIds);

    /**
     * 获取倒序排列的第一天单位id
     *
     * @param departmentIds 数据权限
     * @return 单位id
     * @author gengjiajia
     * @since 2019/08/23 17:59
     */
    @Select("<script> " +
            "   SELECT id FROM rbac_department " +
            "   WHERE is_deleted = 0 " +
            "   <if test='departmentIds != null'> " +
            "   AND id IN " +
            "   <foreach item='item' index='index' collection='departmentIds' open='(' separator=',' close=')'> " +
            "       #{item} " +
            "   </foreach> " +
            "   </if>" +
            "   ORDER BY i_sort DESC " +
            "   LIMIT 0, 1" +
            "</script>")
    Long getTheFirstDepartmentBySortDesc(@Param("departmentIds") List<Long> departmentIds);
}

