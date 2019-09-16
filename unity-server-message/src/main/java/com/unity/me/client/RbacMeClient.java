package com.unity.me.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "unity-server-rbac", fallback = RbacMeClient.HystrixClientFallback.class)
public interface RbacMeClient {

    @GetMapping("/feign/resource/getMenuButton/{code}")
    public List<Integer> getMenuButton(@PathVariable("code") String code);

    @GetMapping("/feign/department/getDataResource")
    public String getDataResource();

    @GetMapping("/feign/department/getDataResourceByStatus/{status}")
    public String getDataResourceByStatus(@PathVariable("status") Integer ststus);

    @GetMapping("/feign/user/findUserInfoByIdIn")
    public Map<String, Object> findUserInfoByIdIn(List<Long> userIds);



    @Component
    static class HystrixClientFallback implements RbacMeClient {
        @Override
        public List<Integer> getMenuButton(String code) {
            return null;
        }

        @Override
        public String getDataResource() {
            return null;
        }

        @Override
        public String getDataResourceByStatus(Integer ststus) {
            return null;
        }

        @Override
        public Map<String, Object> findUserInfoByIdIn(List<Long> userIds) {
            return null;
        }

    }
}
