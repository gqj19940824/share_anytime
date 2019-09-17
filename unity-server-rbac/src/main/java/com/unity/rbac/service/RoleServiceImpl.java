
package com.unity.rbac.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.XyDates;
import com.unity.rbac.dao.RoleDao;
import com.unity.rbac.entity.Role;
import com.unity.rbac.entity.RoleResource;
import com.unity.rbac.entity.User;
import com.unity.rbac.entity.UserRole;
import com.unity.rbac.pojos.Relation;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * 角色业务处理
 * <p>
 * ClassName: RoleService
 * date: 2018-12-12 20:21:10
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleDao, Role> implements IService<Role> {

    private final UserRoleServiceImpl userRoleService;
    private final RoleResourceServiceImpl roleResourceService;
    private final UserHelpServiceImpl userHelpService;

    public RoleServiceImpl(UserRoleServiceImpl userRoleService, RoleResourceServiceImpl roleResourceService,
                           UserHelpServiceImpl userHelpService) {
        this.userRoleService = userRoleService;
        this.roleResourceService = roleResourceService;
        this.userHelpService = userHelpService;
    }

    /**
     * 新增 or 修改角色
     *
     * @param dto 包含角色信息
     * @author gengjiajia
     * @since 2018/12/11 11:28
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateRole(Role dto) {
        // 非超级管理员不允许修改角色
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (!customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.OPERATION_NO_AUTHORITY)
                    .message("非超级管理员不允许创建或修改角色")
                    .build();
        }
        if (dto.getId() == null) {
            //校验名称是否重复
            if (0 < super.count(new QueryWrapper<Role>().lambda()
                    .eq(Role::getName, dto.getName()))) {
                //说明名称已存在
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                        .message("角色名称已存在")
                        .build();
            }
            Role role = new Role();
            role.setName(dto.getName().trim());
            role.setNotes(dto.getNotes());
            role.setIsDefault(YesOrNoEnum.NO.getType());
            role.setIsDeleted(YesOrNoEnum.NO.getType());
            super.save(role);
        } else {
            if (0 < super.count(new QueryWrapper<Role>().lambda().ne(Role::getId, dto.getId()).eq(Role::getName, dto.getName()))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                        .message("角色名称已存在")
                        .build();
            }
            Role role = super.getById(dto.getId());
            if (role == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                        .message("角色不存在")
                        .build();
            }
            if (role.getIsDefault().equals(YesOrNoEnum.YES.getType())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("系统默认角色不可编辑")
                        .build();
            }
            super.updateById(dto);
        }
    }

    /**
     * 后台角色列表
     *
     * @param search 包含角色列表查询条件
     * @return 角色列表
     * @author gengjiajia
     * @since 2018/12/11 14:05
     */
    public Map findRoleList(SearchElementGrid search) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        QueryWrapper<Role> wrapper = search.toEntityWrapper(Role.class);
        if (customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType())) {
            wrapper.lambda().orderByDesc(Role::getSort);
        } else if (CollectionUtils.isNotEmpty(customer.getRoleList())) {
            wrapper.lambda().in(Role::getId, customer.getRoleList().toArray())
//                    .or(w -> w.likeRight(Role::getCreator, customer.getId().toString().concat(ConstString.SEPARATOR_POINT))) 非超级管理员不允许新增或修改角色
                    .orderByDesc(Role::getSort);
        } else {
            return Maps.newHashMap();
        }
        //获取最后一条数据 列表是倒叙 获取正序第一条即可
        Long lastId = baseMapper.getTheFirstRoleBySortAsc(customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType()) ? null : customer.getRoleList());
        Long firstId = baseMapper.getTheFirstRoleBySortDesc(customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType()) ? null : customer.getRoleList());
        return JsonUtil.ObjectToMap(super.page(search.getPageable(), wrapper),
                new String[]{"id", "name", "notes", "isDefault", "creator", "editor"},
                (m, u) -> {
                    Role entity = (Role) u;
                    m.put("first",entity.getId().equals(firstId) ? YesOrNoEnum.YES.getType() : YesOrNoEnum.NO.getType());
                    m.put("last",entity.getId().equals(lastId) ? YesOrNoEnum.YES.getType() : YesOrNoEnum.NO.getType());
                    m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
                });
    }

    /**
     * 获取指定角色信息
     *
     * @param id 角色id
     * @return 角色信息
     * @author gengjiajia
     * @since 2019/07/10 18:54
     */
    public Map<String, Object> getRoleById(Long id) {
        return JsonUtil.ObjectToMap(this.getById(id),
                new String[]{"id", "name", "notes", "isDefault", "creator", "editor", "sort"}
                , (m, u) -> {
                    m.put("gmtCreate", DateUtils.timeStamp2Date(u.getGmtCreate()));
                    m.put("gmtModified", DateUtils.timeStamp2Date(u.getGmtModified()));
                });
    }

    /**
     * 删除指定角色（逻辑删除）
     *
     * @param id 指定角色id
     * @author gengjiajia
     * @since 2019/07/10 18:39
     */
    @Transactional(rollbackFor = Exception.class)
    public void delRole(Long id) {
        // 非超级管理员不允许修改角色
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (!customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.OPERATION_NO_AUTHORITY)
                    .message("非超级管理员不允许修改角色")
                    .build();
        }
        Role role = super.getById(id);
        if (role != null) {
            //判断角色是否为默认角色
            if (role.getIsDefault().equals(YesOrNoEnum.YES.getType())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("当前角色为系统默认角色，不允许删除")
                        .build();
            }
            //判断是否存在与用户的关联关系
            if (0 < userRoleService.count(new QueryWrapper<UserRole>().lambda().eq(UserRole::getIdRbacRole, id))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("当前角色存在与用户的关联关系，请先解除")
                        .build();
            }
            //关联删除与资源的关系
            roleResourceService.remove(new QueryWrapper<RoleResource>().lambda()
                    .eq(RoleResource::getIdRbacRole, id));
            super.removeById(id);
        }
    }

    /**
     * 用户分配角色 （包含解除关系）
     *
     * @param relation 包含用户id及角色id集
     * @author gengjiajia
     * @since 2019/07/08 15:24
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindUserAndRole(Relation relation) {
        //先删后增
        userRoleService.remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getIdRbacUser, relation.getId()));
        Customer customer = LoginContextHolder.getRequestAttributes();
        String creator = customer.getId().toString().concat(ConstString.SEPARATOR_POINT).concat(customer.getName());
        long time = XyDates.getTime(new Date());
        List<Long> roleList = customer.getRoleList();
        Long[] bindRoleIds = relation.getBindRoleIds();
        // 自身没有角色不能给别人分配角色
        if (!customer.getIsAdmin().equals(YesOrNoEnum.YES.getType())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.OPERATION_NO_AUTHORITY)
                    .message("非管理员不能分配角色")
                    .build();
        }
        if (YesOrNoEnum.YES.getType() != customer.isSuperAdmin && CollectionUtils.isEmpty(roleList)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.OPERATION_NO_AUTHORITY)
                    .message("当前管理员非超管或未拥有角色，不能给他人分配角色")
                    .build();
        }
        // 当前账号给其他用户分配角色时不能分配自身没有的角色
        if (ArrayUtils.isNotEmpty(bindRoleIds)) {
            if(YesOrNoEnum.YES.getType() != customer.isSuperAdmin) {
                Arrays.stream(bindRoleIds).forEach(roleId -> {
                    if (!roleList.contains(roleId)) {
                        throw UnityRuntimeException.newInstance()
                                .code(SystemResponse.FormalErrorCode.OPERATION_NO_AUTHORITY)
                                .message("当前管理员角色权限不足，不能分配部分角色")
                                .build();
                    }
                });
            }
            List<UserRole> userRoleList = Arrays.stream(bindRoleIds)
                    .map(roleId -> {
                        UserRole userRole = new UserRole();
                        userRole.setIdRbacUser(relation.getId());
                        userRole.setIdRbacRole(roleId);
                        userRole.setIsDeleted(YesOrNoEnum.NO.getType());
                        userRole.setGmtCreate(time);
                        userRole.setCreator(creator);
                        userRole.setGmtModified(time);
                        userRole.setEditor(creator);
                        userRole.setSort(time);
                        return userRole;
                    }).collect(toList());
            userRoleService.saveBatch(userRoleList);
        }
    }

    /**
     * 通过用户id获取角色列表及用户已绑定的角色id列表
     *
     * @param userId 用户id
     * @return 角色列表及用户已绑定的角色id列表
     * @author gengjiajia
     * @since 2019/07/08 15:12
     */
    public Map<String, Object> getRoleListAndUserLinkedRoleIdListByUserId(Long userId) {
        Map<String, Object> data = Maps.newHashMap();
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Role::getSort);
        Customer customer = LoginContextHolder.getRequestAttributes();
        if(customer.getIsAdmin().equals(YesOrNoEnum.NO.getType())){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("非管理员不允许此操作")
                    .build();
        }
        //判断当前管理员是否有权限处理指定的用户角色分配
        List<Long> idsByUserId = userRoleService.selectRoleIdsByUserId(userId);
        List<Long> customerRoleList = customer.getRoleList();
        //普通管理员判断权限范围，超管不做限制
        if(customer.getIsSuperAdmin().equals(YesOrNoEnum.NO.getType())){
            if (idsByUserId.size() > customerRoleList.size() || !customerRoleList.containsAll(idsByUserId)){
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("您的权限不足，请联系高级管理员")
                        .build();
            }
        }
        //1、指定用户的角色数量多于当前管理员，禁止当前管理员处理  2、判断管理员拥有的角色是否完全包含用户拥有的角色
        User user = userHelpService.getById(userId);
        //非管理员不能分配角色 指定用户基本信息不完整不能分配角色
        if (!customer.getIsAdmin().equals(YesOrNoEnum.YES.getType()) || user == null) {
            data.put("roleList", Lists.newArrayList());
            data.put("userBindRoleIdList", Lists.newArrayList());
            return data;
        }
        //超级管理员分配全部角色
        List<Long> roleList;
        if (YesOrNoEnum.YES.getType() == customer.getIsSuperAdmin()) {
            List<Role> list = super.list();
            if (CollectionUtils.isNotEmpty(list)) {
                roleList = list.stream().map(Role::getId).collect(toList());
            } else {
                roleList = Lists.newArrayList();
            }
        } else {
            roleList = customer.getRoleList();
        }
        wrapper.in(Role::getId, roleList.toArray());
        List<Map<String, Object>> allRoleList = JsonUtil.ObjectToList(super.list(wrapper), new String[]{"id", "name"},
                (m, entity) -> m.put("disabled", false));
        List<UserRole> userRoleList = userRoleService.list(new LambdaQueryWrapper<UserRole>().eq(UserRole::getIdRbacUser, userId));
        List<Long> userBindRoleIdList = userRoleList.stream().map(UserRole::getIdRbacRole).collect(toList());
        data.put("roleList", allRoleList);
        data.put("userBindRoleIdList", userBindRoleIdList);
        return data;
    }

    /**
     * 更改排序
     *
     * @param id 角色id
     * @param up 0 下降 1 上升
     * @author gengjiajia
     * @since 2019/08/14 20:43
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeOrder(Long id, Integer up) {
        Role entity = this.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        String msg;
        if (up == 1) {
            wrapper.gt(Role::getSort, sort);
            msg = "已经是第一条数据";
            wrapper.orderByAsc(Role::getSort);
        } else {
            wrapper.lt(Role::getSort, sort);
            msg = "已经是最后一条数据";
            wrapper.orderByDesc(Role::getSort);
        }
        Role entity1 = this.getOne(wrapper, false);
        if (entity1 == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message(msg)
                    .build();
        }
        entity.setSort(entity1.getSort());
        Role entitya = new Role();
        entitya.setSort(entity1.getSort());
        entitya.setId(entity.getId());
        this.updateById(entitya);
        Role entityb = new Role();
        entityb.setId(entity1.getId());
        entityb.setSort(sort);
        this.updateById(entityb);
    }

}
