package com.unity.rbac.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.entity.Role;
import com.unity.rbac.pojos.Relation;
import com.unity.rbac.service.RoleServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 角色信息后台控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 *
 * @author gengjiajia
 */
@Slf4j
@RestController
@RequestMapping("role")
public class RoleController extends BaseWebController {

    private final RoleServiceImpl roleService;

    public RoleController(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }


    /**
     * 新增 or 修改 --> 角色
     *
     * @param dto 包含角色信息
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/11 13:52
     */
    @PostMapping("saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody Role dto) {
        if (dto == null || StringUtils.isBlank(dto.getName())) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到角色名称");
        }
        dto.setName(dto.getName().replaceAll(" ",""));
        if(dto.getName().length() > UserConstants.USER_NAME_COMPANY_POSITION_MAX_LENGTH){
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "角色名称为1到20个字符");
        }
        roleService.saveOrUpdateRole(dto);
        return success("操作成功");
    }

    /**
     * 后台角色列表
     *
     * @param pageEntity 包含角色列表查询条件
     * @return code 0 表示成功
     * 500 表示缺少查询条件
     * @author gengjiajia
     * @since 2018/12/11 14:31
     */
    @PostMapping("listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<Role> pageEntity) {
        return success(roleService.findRoleList(pageEntity));
    }


    /**
     * 获取指定的角色信息
     *
     * @param role 包含指定角色id
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/11 14:52
     */
    @PostMapping("detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody Role role) {
        if (role == null || role.getId() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到指定角色ID");
        }
        return success(roleService.getRoleById(role.getId()));
    }

    /**
     * 删除指定角色
     *
     * @param role 包含指定角色id
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/11 15:00
     */
    @PostMapping("deleteById")
    public Mono<ResponseEntity<SystemResponse<Object>>> delRole(@RequestBody Role role) {
        if (role == null || role.getId() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到指定角色ID");
        }
        roleService.delRole(role.getId());
        return success("删除成功");
    }

    /**
     * 用户分配角色 （包含解除关系）
     *
     * @param relation 包含用户与角色的关系
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/17 17:12
     */
    @PostMapping("bindUserAndRole")
    public Mono<ResponseEntity<SystemResponse<Object>>> bindUserAndRole(@RequestBody Relation relation) {
        if (relation == null || relation.getId() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到用户ID");
        }
        roleService.bindUserAndRole(relation);
        return success("分配成功");
    }

    /**
     * 通过用户id 获取角色列表（包含用户与角色绑定关系）
     *
     * @param relation 包含用户id
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2019/07/08 15:12
     */
    @PostMapping("getRoleListAndUserLinkedRoleIdListByUserId")
    public Mono<ResponseEntity<SystemResponse<Object>>> getRoleListAndUserLinkedRoleIdListByUserId(@RequestBody Relation relation) {
        if (relation == null || relation.getId() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到用户ID");
        }
        return success(roleService.getRoleListAndUserLinkedRoleIdListByUserId(relation.getId()));
    }

    /**
     * 用户列表角色下拉列表
     *
     * @return code 0 表示成功
     * 500 表示缺少查询条件
     * @author gengjiajia
     * @since 2018/12/11 14:31
     */
    @PostMapping("listByPageToUserList")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPageToUserList() {
        Customer customer = LoginContextHolder.getRequestAttributes();
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType())) {
            //超管或集团管理员查所有
            wrapper.orderByDesc(Role::getGmtCreate);
        } else if (customer.getIsAdmin().equals(YesOrNoEnum.NO.getType())) {
            //非管理员不予查询
            return success(Lists.newArrayList());
        } else {
            //二三级管理员查询拥有的和自己创建的
            if (CollectionUtils.isNotEmpty(customer.getRoleList())) {
                wrapper.in(Role::getId,customer.getRoleList()).or().likeRight(Role::getCreator, customer.getId().toString().concat(ConstString.SEPARATOR_POINT));
            } else {
                wrapper.likeRight(Role::getCreator, customer.getId().toString().concat(ConstString.SEPARATOR_POINT));
            }
        }
        List<Role> roleList = roleService.list(wrapper);
        return success(JsonUtil.ObjectToList(roleList, null, Role::getId, Role::getName));
    }

    /**
     * 更改排序
     *
     * @param map id
     *            up 0 下降 1 上升
     * @return code 0 表示成功
     */
    @PostMapping("/changeOrder")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@RequestBody Map<String, Object> map) {
        if (MapUtils.isEmpty(map)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        Integer up = MapUtils.getInteger(map, "up");
        Long id = MapUtils.getLong(map, "id");
        roleService.changeOrder(id,up);
        return success("移动成功");
    }
}