package com.unity.common.client;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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


    @PostMapping("/feign/filefeign/deleteFileBatch")
    void deleteFileBatch(@RequestBody List<String> filePaths);

    @PostMapping("/feign/filefeign/deleteFile")
    void deleteFile(@RequestBody String filePath);

    @PostMapping(value = "/feign/filefeign/fileUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String fileUpload(@RequestPart("file") MultipartFile file);


    @Component
    class HystrixClientFallback implements ReClient {

        @Override
        public byte[] download(String fileUrl) {
            return new byte[0];
        }

        @Override
        public void deleteFileBatch(List<String> filePaths) {

        }

        @Override
        public void deleteFile(String filePath) {

        }

        @Override
        public String fileUpload(MultipartFile file) {
            return null;
        }
    }
    @Scope("prototype")
    @Primary
    @Configuration
    class MultipartSupportConfig {
        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;
        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder(new SpringEncoder(messageConverters));
        }
    }
}
