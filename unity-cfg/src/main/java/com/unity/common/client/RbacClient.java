package com.unity.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * rbac feign 接口调用客户端
 *
 * @author gengjiajia
 * @since 2019/07/12 10:33
 */
@FeignClient(name = "unity-server-rbac", fallback = RbacClient.HystrixClientFallback.class)
public interface RbacClient {

    @Component
    class HystrixClientFallback implements RbacClient {


    }


}
