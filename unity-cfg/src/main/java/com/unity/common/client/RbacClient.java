package com.unity.common.client;

import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.client.vo.UserVO;
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
    @PostMapping("/feign/role/getUserListByRoleIdList")
    List<UserVO> getUserListByRoleIdList(@RequestBody List<Long> roleIdList);

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
    
    /**
     * 功能描述 获取全部单位数据
     * @return 单位集合
     * @author gengzhiqiang
     * @date 2019/9/26 15:56 
     */
    @PostMapping("/feign/dept/getAllDepartment")
    List<DepartmentVO> getAllDepartment();

    /**
     * 根据指定的单位id获取对应单位下的用户信息列表
     *
     * @param  ids 单位id集
     * @return 用户信息列表
     * @author gengjiajia
     * @since 2019/10/18 16:41
     */
    @PostMapping("/feign/user/getUserListByDepIdList")
    List<UserVO> getUserListByDepIdList(@RequestBody List<Long> ids);

    @Component
    class HystrixClientFallback implements RbacClient {

        @Override
        public List<UserVO> getUserListByRoleIdList(List<Long> roleIdList) {
            return null;
        }

        @Override
        public Map<Long,List<Long>> listUserInDepartment(List<Long> ids) {
            return null;
        }

        @Override
        public List<DepartmentVO> getAllDepartment() {
            return null;
        }

        @Override
        public List<UserVO> getUserListByDepIdList(List<Long> ids) {
            return null;
        }

        @Override
        public List<Long> getUserIdListByDepIdList(List<Long> departmentIdList) {
            return null;
        }
    }

}
