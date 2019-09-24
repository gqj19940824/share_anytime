package com.unity.rbac.controller.feign;


import com.unity.rbac.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.rbac.entity.User;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


import java.util.stream.Collectors;






/**
 * 用户信息后台控制类
 * <p>
 *
 * @author gengjiajia
 * @since 2018/12/21 13:45
 */
@Slf4j
@RestController
@RequestMapping("/feign/user")
public class UserFeignController extends BaseWebController {

    private final UserServiceImpl userService;

    public UserFeignController(UserServiceImpl userService) {
        this.userService = userService;
    }



    /**
    * 查询属于某几个单位的用户，得到{用户id，单位id}集合
    *
    * @param ids 单位id的集合
    * @return java.util.Map<java.lang.Long,java.lang.Long>
    * @author JH
    * @date 2019/9/23 16:21
    */
    @PostMapping("listUserInDepartment")
    public Map<Long,List<Long>> listUserInDepartment(@RequestBody List<Long> ids) {
        return userService.listUserInDepartment(ids);
    }


    /**
     * 根据单位id列表获取关联的用户id列表
     *
     * @param departmentIdList 单位id列表
     * @return code : 0 表示成功
     * @author gengjiajia
     * @since 2018/12/11 10:04
     */
    @PostMapping("getUserIdListByDepIdList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getUserIdListByDepIdList(@RequestBody List<Long> departmentIdList) {
        if(CollectionUtils.isEmpty(departmentIdList)){
            return success(Lists.newArrayList());
        }
        List<User> userList = userService.list(new LambdaQueryWrapper<User>().in(User::getIdRbacDepartment, departmentIdList.toArray()));
        if(CollectionUtils.isEmpty(userList)){
            return success(Lists.newArrayList());
        }
        List<Long> userIdList = userList.stream().map(User::getId).collect(Collectors.toList());
        return success(userIdList);
    }

}