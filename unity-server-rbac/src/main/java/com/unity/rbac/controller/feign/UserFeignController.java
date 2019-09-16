package com.unity.rbac.controller.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.JsonUtil;
import com.unity.rbac.entity.User;
import com.unity.rbac.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户信息API控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 * @author gengjiajia
 */
@Slf4j
@RestController
@RequestMapping("feign/user")
public class UserFeignController {

    private final UserServiceImpl userService;

    public UserFeignController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * 查询属于某几个单位的用户，得到{用户id，单位id}集合
     *
     * @param ids 单位id的集合
     * @return java.util.Map<java.lang.Long, java.lang.Long>返回符合条件的用户的{用户id，单位id}集合
     * @author lifeihong
     * @date 2019/7/10 17:26
     */
    @PostMapping("listUserInDepartment")
    public Map<Long, Long> listUserInDepartment(@RequestBody List<Long> ids) {
        return userService.listUserInDepartment(ids);
    }

    /**
     * 查询某几个单位的用户，包括is_deleted为1的，不进行分组
     *
     * @param ids 单位id的集合
     * @return java.util.Map<java.lang.Long, java.lang.String> 返回符合条件的用户集合
     * @author lifeihong
     * @date 2019/7/10 17:09
     */
    @PostMapping("/listAllInDepartment")
    public Map<Long, String> listAllInDepartment(@RequestBody List<Long> ids) {
        return userService.listAllInDepartment(ids);
    }

    /**
     *  根据用户ID集合获取用户信息
     *
     * @param ids 用户id的集合
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 返回符合条件的用户集合
     * @author vv
     * @date 2019/7/10 17:09
     */
    @PostMapping("/getUserListByIds")
    public List<Map<String, Object>> getUserListByIds(@RequestBody Set<Long> ids) {
        List<User> list = userService.list(new LambdaQueryWrapper<User>().in(User::getId, ids));
        return JsonUtil.<User>ObjectToList(list,
               null,User::getId,User::getName,User::getIdRbacDepartment
        );
    }


    /**
     * @desc: 获取到所有用户
     * @param:
     * @return:
     * @author: 郭振洋
     * @date: 2019/7/18 9:29
    **/
    @PostMapping("/listUser")
    public List<Map<String,Object>> listUser() {
        return userService.listUser();
    }


    /**
     * 统一密码修改
     *
     * @param dto 包含新老密码
     *            -1005 数据已存在
     *            -1011 数据不存在
     *            -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/07 09:03
     */
    @PutMapping("unityUpdatePwd")
    public void unityUpdatePwd(@RequestBody User dto) {
        log.info("===统一密码修改===《unityUpdatePwd》===入参 {}", GsonUtils.format(dto));
        if (dto == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到要修改的用户信息")
                    .build();
        }
        userService.unityUpdatePwd(dto.getOldPwd(), dto.getPwd());
    }

    /**
     * 获取指定用户信息
     *
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * -1011 用户不存在
     * @author gengjiajia
     * @since 2018/12/11 13:55
     */
    @GetMapping("getUserInfo")
    public Map<String,Object> getUserInfo() {
        return userService.getUserById(null);
    }


    /**
     * 获取指定用户信息
     *
     * @param userId 指定用户的id
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * -1011 用户不存在
     * @author gengjiajia
     * @since 2018/12/11 13:55
     */
    @GetMapping("getUser/{userId}")
    public Map<String,Object> getUserInfo(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("getUserName/{userId}")
    public String getUserName(@PathVariable("userId") Long userId) {
        return userService.getById(userId).getName();
    }


    /**
     * 通过userId批量获取用户信息
     *
     * @param userIds 用户id集
     * @return code : 0 表示成功
     * @author gengjiajia
     * @since 2018/12/05 17:03
     */
    @PostMapping("findUserInfoByIdIn")
    public Map<String, Map<String, Object>> findUserInfoByIdIn(@RequestBody List<Long> userIds) {
        log.info("===批量获取用户信息===《findUserInfoByIdIn》===入参 {}", GsonUtils.format(userIds));
        if (CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }
        return userService.findUserInfoByIdIn(userIds);
    }

    /**
    * 根据用户id返回项目账户的项目id
    *
    * @param id 主键
    * @return 项目id
    * @author JH
    * @date 2019/7/18 18:24
    */
    @PostMapping("getIdProjectById/{id}")
    public Long getIdProjectById(@PathVariable("id") Long id) {
        return userService.getById(id).getIdInfoProject();
    }


    /**
    * 根据项目id返回项目账户个数
    *
    * @param projectIds 项目id集合
    * @return 项目账户的个数
    * @author JH
    * @date 2019/7/23 14:57
    */
    @PostMapping("getCountByIdInfoProject")
    public Integer getCountByIdInfoProject(@RequestBody List<Long> projectIds){
        return userService.count(new QueryWrapper<User>().in("id_info_project",projectIds));
    }



    /**
     * 返回当前登录账号，发送时可选择的账号
     *
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author JH
     * @date 2019/8/8 9:00
     */
    @PostMapping("getUserList")
    public  List<Map<String, Object>> getUserList(){
        return userService.getUserList();
    }


    /**
     * 根据角色、单位集合获取用户id集合
     *
     * @param map 参数map
     * @return java.util.Map<Long, List<Long>>
     * @author JH
     * @date 2019/8/8 18:11
     */
    @PostMapping("getUserIdsByRoleIdAndDepartmentIds")
    public  Map<Long, List<Long>> getUserIdsByRoleIdAndDepartmentIds(@RequestBody Map<String,Object> map) {
        return userService.getUserIdsByRoleIdAndDepartmentIds(map);
    }

    /**
     * 获取某单位某角色的用户
     *
     * @param map 参数map
     * @return java.util.List<java.lang.Long>
     * @author JH
     * @date 2019/8/8 18:11
     */
    @PostMapping("getUserIdsByRoleIdAndDepartmentId")
    public  List<Long> getUserIdsByRoleIdAndDepartmentId(@RequestBody Map<String,Object> map) {
        return userService.getUserIdsByRoleIdAndDepartmentId(map);
    }

    /**
     * 根据单位id获取这个单位下的所有员工
     *
     * @param id 单位id
     * @return java.util.List<java.lang.Long>
     * @author JH
     * @date 2019/8/12 16:07
     */
    @PostMapping("getUserIdsByDepartmentId/{id}")
    List<Long> getUserIdsByDepartmentId(@PathVariable("id") Long id) {
        return userService.list(new LambdaQueryWrapper<User>().eq(User::getIdRbacDepartment,id)).stream().map(User::getId).collect(Collectors.toList());
    }

}