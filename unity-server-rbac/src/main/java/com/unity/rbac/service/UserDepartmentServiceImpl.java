
package com.unity.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constants.ConstString;
import com.unity.rbac.dao.UserDepartmentDao;
import com.unity.rbac.entity.Department;
import com.unity.rbac.entity.UserDepartment;
import jdk.nashorn.internal.runtime.ConsString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * ClassName: UserDepartmentService
 * date: 2018-12-12 20:21:07
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserDepartmentServiceImpl extends BaseServiceImpl<UserDepartmentDao, UserDepartment> implements IService<UserDepartment> {

    /**
     * 查询指定用户数据权限id集
     *
     * @param  userId 用户id
     * @return 数据权限id集
     * @author gengjiajia
     * @since 2019/07/08 09:35
     */
    public List<Long> findDataPermissionIdListByUserId(Long userId){
        return baseMapper.findDataPermissionIdListByUserId(userId);
    }

}
