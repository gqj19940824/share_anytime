package com.unity.common.client;

import com.unity.common.client.vo.UcsUser;
import com.unity.common.pojos.SystemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author gengjiajia
 */
//@FeignClient(name = "unity-server-ucs",url = "http://192.168.2.56:10008", fallback = UcsClient.HystrixClientFallback.class)
@FeignClient(name = "unity-server-ucs",url = "http://ucs.test.jingcaiwang.cn:21020", fallback = UcsClient.HystrixClientFallback.class)
public interface UcsClient {

    /**
     * 各个系统向用户中心推送用户接口
     *
     * @param  ucsUser 用户信息
     * @return 用户完整信息
     * @author gengjiajia
     * @since 2019/07/27 10:45  
     */
    @PostMapping("/ucs/user/pushUserToUcs")
    SystemResponse pushUserToUcs(@RequestBody UcsUser ucsUser);


    /**
     * 用户中心账号信息验证
     *
     * @param  ucsUser 用户信息
     * @return 验证码
     * @author gengjiajia
     * @since 2019/07/27 10:45
     */
    @PostMapping("/ucs/user/checkLoginInfo")
    SystemResponse checkLoginInfo(@RequestBody UcsUser ucsUser);

    /**
     * 用户中心重置用户密码
     *
     * @param  ucsUser 用户信息
     * @return 接口调用状态
     * @author gengjiajia
     * @since 2019/07/27 10:45
     */
    @PostMapping("/ucs/user/resetPassword")
    SystemResponse resetPassword(@RequestBody UcsUser ucsUser);

    /**
     * 用户中心修改用户密码
     *
     * @param  ucsUser 用户信息
     * @return 接口调用状态
     * @author gengjiajia
     * @since 2019/07/27 10:45
     */
    @PostMapping("/ucs/user/updatePassword")
    SystemResponse updatePassword(@RequestBody UcsUser ucsUser);

    /**
     * 异常托底数据
     *
     * @author gengjiajia
     * @since 2019/07/27 10:40
     */
    @Component
    class HystrixClientFallback implements UcsClient {

        @Override
        public SystemResponse pushUserToUcs(UcsUser ucsUser) {
            return null;
        }

        @Override
        public SystemResponse checkLoginInfo(UcsUser ucsUser) {
            return null;
        }

        @Override
        public SystemResponse resetPassword(UcsUser ucsUser) {
            return null;
        }

        @Override
        public SystemResponse updatePassword(UcsUser ucsUser) {
            return null;
        }
    }
}
