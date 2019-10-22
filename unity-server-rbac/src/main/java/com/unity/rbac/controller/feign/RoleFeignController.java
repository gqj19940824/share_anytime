package com.unity.rbac.controller.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.rbac.entity.User;
import com.unity.rbac.entity.UserRole;
import com.unity.rbac.service.UserRoleServiceImpl;
import com.unity.rbac.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息后台控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 *
 * @author gengjiajia
 */
@Slf4j
@RestController
@RequestMapping("feign/role")
public class RoleFeignController extends BaseWebController {

    private final UserRoleServiceImpl userRoleService;
    private final UserServiceImpl userService;

    public RoleFeignController(UserRoleServiceImpl userRoleService, UserServiceImpl userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
    }




    /**
     * 根据角色id集获取所关联的用户id
     *
     * @param  roleIdList 角色id集
     * @return code 0 -> 表示成功
     * @author gengjiajia
     * @since 2019/09/23 16:07
     */
    @PostMapping("/getUserListByRoleIdList")
    public List<User> getUserListByRoleIdList(@RequestBody List<Long> roleIdList) {
        if(CollectionUtils.isEmpty(roleIdList)) {
            return Lists.newArrayList();
        }
        List<UserRole> userRoleList = userRoleService.list(new LambdaQueryWrapper<UserRole>().in(UserRole::getIdRbacRole, roleIdList.toArray()));
        List<Long> userIdList = userRoleList.stream().map(UserRole::getIdRbacUser).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return userService.list(new LambdaQueryWrapper<User>().in(User::getId, userIdList.toArray()));
    }
}