package com.unity.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "unity-server-re", fallback = ReClient.HystrixClientFallback.class)
public interface ReClient {

    /**
     * 通过文件地址获取文件流
     *
     * @param  fileUrl 文件地址
     * @return 文件流
     * @author gengjiajia
     * @since 2019/10/11 14:47
     */
    @GetMapping("/feign/filefeign/download")
    byte[] download(@RequestParam("fileUrl") String fileUrl);


    @Component
    class HystrixClientFallback implements ReClient {

        @Override
        public byte[] download(String fileUrl) {
            return new byte[0];
        }
    }
}
