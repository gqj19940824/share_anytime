
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

    @Autowired
    private DepartmentServiceImpl departmentService;

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

    /**
     * 获取指定单位名称及所有直属上层单位名称
     *
     * @param  idRbacDepartment 指定单位id
     * @return 指定单位名称及所有直属上层单位名称
     * @author gengjiajia
     * @since 2019/07/29 15:48
     */
    public String getImmediateSupervisorName(Long idRbacDepartment) {
        Department department = departmentService.getById(idRbacDepartment);
        if(department == null){
            return new String();
        }
        List<String> codes = Lists.newArrayList();
        StringBuilder code = new StringBuilder();
        String[] codeArr = department.getGradationCode().split(ConstString.SPLIT_POINT);
        for (String gradationCode : codeArr){
            if(StringUtils.isNotEmpty(gradationCode)){
                code.append(ConstString.SEPARATOR_POINT).append(gradationCode);
                codes.add(code.toString());
            }
        }
        String name = baseMapper.getImmediateSupervisorName(codes);
        if(StringUtils.isBlank(name)){
            return new String();
        }
        name = name.replaceAll(",","");
        return name;
    }

    /**
     * 根据单位id获取单位信息
     *
     * @param  idRbacDepartment 单位id
     * @return 单位信息
     * @author gengjiajia
     * @since 2019/07/31 19:58
     */
    public Department getDepartmentById(Long idRbacDepartment) {
        return departmentService.getById(idRbacDepartment);
    }
}
