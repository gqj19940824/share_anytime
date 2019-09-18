package com.unity.rbac.configuration;


import com.unity.common.constant.RedisConstants;
import com.unity.common.util.GsonUtils;
import com.unity.common.utils.HashRedisUtils;
import com.unity.rbac.entity.Department;
import com.unity.rbac.entity.User;
import com.unity.rbac.service.DepartmentServiceImpl;
import com.unity.rbac.service.ResourceServiceImpl;
import com.unity.rbac.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 项目启动后执行redis缓存操作类
 * <p>
 * create by  at 2019/07/12 17:58
 *
 * @author gengjiajia
 */
@Slf4j
//@Component
public class RedisRunner implements ApplicationRunner {

    @Resource
    private UserServiceImpl userService;
    @Resource
    private DepartmentServiceImpl departmentService;
    @Resource
    private HashRedisUtils hashRedisUtils;



    /**
     * 项目启动后调用该方法
     *
     * @param args 系统参数
     * @author gengjiajia
     * @since 2019/07/12 18:03
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("====== 《RedisRunner》 RBAC 项目启动执行缓存操作======");
        //1、清除hash表中用户及单位信息   2、将用户及单位信息存到redis
        List<User> userList = userService.findUserAndDepartmentList();
        if (CollectionUtils.isNotEmpty(userList)) {
            //先删后增
            Set<String> userKeys = userList.parallelStream()
                    .map(user -> RedisConstants.USER.concat(user.getId().toString()))
                    .collect(Collectors.toSet());
            hashRedisUtils.delete(userKeys);
            userList.parallelStream().forEach(user ->
                    hashRedisUtils.putValueByKey(RedisConstants.USER.concat(user.getId().toString()), GsonUtils.map(GsonUtils.format(user)))
            );
            log.info("====== 《RedisRunner》 用户信息同步到缓存数量 {}",userList.size());
        }
        List<Department> departmentList = departmentService.list();
        if (CollectionUtils.isNotEmpty(departmentList)) {
            //先删后增
            Set<String> depKeys = departmentList.parallelStream()
                    .map(department -> RedisConstants.DEPARTMENT.concat(department.getId().toString()))
                    .collect(Collectors.toSet());
            hashRedisUtils.delete(depKeys);
            departmentList.parallelStream().forEach(department ->
                    hashRedisUtils.putValueByKey(RedisConstants.DEPARTMENT.concat(department.getId().toString()), GsonUtils.map(GsonUtils.format(department)))
            );
            log.info("====== 《RedisRunner》 单位信息同步到缓存数量 {}",departmentList.size());
        }
        //将公司排序信息放入redis
        departmentService.reOrderDepartment();
        //清除资源树
        hashRedisUtils.removeValueByKey(RedisConstants.RESOURCE_TREE);
    }
}
