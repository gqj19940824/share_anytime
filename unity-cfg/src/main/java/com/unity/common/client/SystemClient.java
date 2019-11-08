package com.unity.common.client;

import com.unity.common.pojos.Dic;
import com.unity.common.pojos.DicGroup;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "unity-server-system", fallback = SystemClient.HystrixClientFallback.class)
public interface SystemClient {

    @PostMapping("/feign/dic/getDicByCode")
    Dic getDicByCode(@RequestParam("groupCode") Object groupCode, @RequestParam("dicCode") Object dicCode);

    @PostMapping("/feign/dic/getDicGroupByGroupCode")
    DicGroup getDicGroupByGroupCode(@RequestParam("groupCode") String groupCode);

    @PostMapping("/feign/dic/getDicsByGroupCode")
    List<Dic> getDicsByGroupCode(@RequestParam("groupCode") String groupCode);

    @PostMapping("/feign/dic/putDicByCode")
    void putDicByCode(@RequestParam("groupCode") String groupCode, @RequestParam("dicCode") String dicCode,
                      @RequestParam("dicValue") String dicValue);

    @Component
    class HystrixClientFallback implements SystemClient {
        @Override
        public Dic getDicByCode(Object groupCode, Object dicCode) {
            return null;
        }

        @Override
        public DicGroup getDicGroupByGroupCode(String groupCode) {
            return null;
        }

        @Override
        public List<Dic> getDicsByGroupCode(String groupCode) {
            return null;
        }

        @Override
        public void putDicByCode(String groupCode, String dicCode, String dicValue) {

        }
    }
}
