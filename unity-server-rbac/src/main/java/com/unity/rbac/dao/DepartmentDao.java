
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

}

