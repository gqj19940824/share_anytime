package com.unity.me.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "unity-server-cms",
        fallback = CmsClient.HystrixClientFallback.class)
public interface CmsClient {


    @PostMapping("feign/doc/getDocTitle")
    Map<String,Object> getDocTitle(@RequestParam("docId") Long docId);
    @PostMapping("feign/doc/getColumnName")
    String getColumnName(@RequestParam("columnId") Long columnId);
    @PostMapping("feign/doc/getDocList")
    List<Map<String, Object>> getDocList(Map<String, Object> map);
    @PostMapping("feign/doc/getDetail/{docId}")
    Map<String,String> findDocTextById(@PathVariable("docId") String docId);
    @PostMapping("feign/doc/lockDoc")
    boolean lockDoc(Map<String, Object> comment);
    @PostMapping("feign/doc/releaseDoc")
    boolean releaseDoc(@RequestParam("docId") Long docId,@RequestParam("type") Integer type);
    @Component
    class HystrixClientFallback implements CmsClient {

        @Override
        public Map<String,Object> getDocTitle(Long docId) {
            return null;
        }

        @Override
        public String getColumnName(Long columnId) {
            return null;
        }

        @Override
        public List<Map<String, Object>> getDocList(Map<String, Object> map) {
            return null;
        }

        @Override
        public Map<String,String> findDocTextById(String docId) {
            return null;
        }

        @Override
        public boolean lockDoc(Map<String, Object> comment) {
            return false;
        }

        @Override
        public boolean releaseDoc(Long docId, Integer type) {
            return false;
        }


    }

}