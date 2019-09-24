package com.unity.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * rbac feign 接口调用客户端
 *
 * @author gengjiajia
 * @since 2019/07/12 10:33
 */
@FeignClient(name = "unity-server-rbac", fallback = RbacClient.HystrixClientFallback.class)
public interface RbacClient {

    /**
     * 根据角色id集获取对应关联的用户id集
     *
     * @param  roleIdList 角色id集
     * @return 用户id集
     * @author gengjiajia
     * @since 2019/09/23 16:15
     */
    @PostMapping("/feign/role/getUserIdListByRoleIdList")
    List<Long> getUserIdListByRoleIdList(@RequestBody List<Long> roleIdList);

    /**
     * 根据单位id列表获取关联的用户id集
     *
     * @param  departmentIdList 单位id列表
     * @return 用户id集
     * @author gengjiajia
     * @since 2019/09/23 20:01
     */
    @PostMapping("/feign/user/getUserIdListByDepIdList")
    List<Long> getUserIdListByDepIdList(@RequestBody List<Long> departmentIdList);


    /**
    * 查询属于某几个单位的用户，得到{用户id，单位id}集合
    *
    * @param ids  单位id的集合
    * @return java.util.Map<java.lang.Long,java.lang.Long>
    * @author JH
    * @date 2019/9/23 16:16
    */
    @PostMapping("/feign/user/listUserInDepartment")
    Map<Long,List<Long>> listUserInDepartment(@RequestBody List<Long> ids);

    @Component
    class HystrixClientFallback implements RbacClient {

        @Override
        public List<Long> getUserIdListByRoleIdList(List<Long> roleIdList) {
            return null;
        }

        @Override
        public Map<Long,List<Long>> listUserInDepartment(List<Long> ids) {
            return null;
        }
        @Override
        public List<Long> getUserIdListByDepIdList(List<Long> departmentIdList) {
            return null;
        }
    }

}
