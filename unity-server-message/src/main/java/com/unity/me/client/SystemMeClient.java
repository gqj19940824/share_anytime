package com.unity.me.client;

import com.unity.common.pojos.Dic;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "unity-server-system", fallback = SystemMeClient.HystrixClientFallback.class)
public interface SystemMeClient {

    @PostMapping("/feign/Dictionary/getDic")
    public Dic getDic(List<String> names);

    @Component
    static class HystrixClientFallback implements SystemMeClient {
        @Override
        public Dic getDic(List<String> names) {
            return null;
        }
    }
}
