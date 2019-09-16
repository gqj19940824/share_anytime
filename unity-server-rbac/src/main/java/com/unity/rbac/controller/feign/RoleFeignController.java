package com.unity.rbac.controller.feign;

import com.unity.rbac.entity.Role;
import com.unity.rbac.service.ResourceServiceImpl;
import com.unity.rbac.service.RoleServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色信息后台控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 */
@RestController
@RequestMapping("feign/role")
public class RoleFeignController {

    private final RoleServiceImpl roleService;
    private final ResourceServiceImpl resourceService;

    public RoleFeignController(RoleServiceImpl roleService, ResourceServiceImpl resourceService){
        this.roleService = roleService;
        this.resourceService = resourceService;
    }

    /**
     * pbk 获取指定用户已有的角色列表
     *
     * @param  userId 包含角色列表查询条件
     * @return code 0 表示成功
     * 500 表示缺少查询条件
     * @author gengjiajia
     * @since 2018/12/12 13:51
     */
    @GetMapping("getUserRoleListByUserId/{userId}")
    @ResponseBody
    public List<Role> getUserRoleListByUserId(@PathVariable Long userId) {
        return roleService.getUserRoleListByUserId(userId);
    }


    /**
     * @desc: 根据角色ID查询该角色下所有的用户ID
     * @param: [roleId]
     * @return: java.util.List<java.lang.Long>
     * @author: vv
     * @date: 2019/7/15 14:59
     **/
    @GetMapping("getUserIdsByRoleId/{roleId}")
    @ResponseBody
    public Set<Long> getUserIdsByRoleId(@PathVariable Long roleId) {
        return roleService.getUserIdsByRoleId(roleId);
    }

}