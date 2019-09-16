package com.unity.common.client;

import com.unity.common.client.vo.SysReminder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
* safety feign接口调用客户端
*
* @author JH
* @date 2019/7/23 15:30
*/
@FeignClient(name = "unity-business-safety",fallback = SafetyClient.HystrixClientFallback.class )
public interface SafetyClient {

    /**
    * 根据项目id 返回项目信息
    *
    * @param id 项目id
    * @return java.util.Map<java.lang.String,java.lang.Object>
    * @author JH
    * @date 2019/7/23 15:40
    */
    @GetMapping("/feign/project/getProjectById/{id}")
    Map<String, Object> getProjectById(@PathVariable("id") Long id);

    /**
     * 保存系统提醒
     *
     * @param  sysReminder 包含提醒信息
     * @author gengjiajia
     * @since 2019/07/31 16:07
     */
    @PostMapping("/feign/sysreminder/save")
    void saveSysReminder(@RequestBody SysReminder sysReminder);

    class HystrixClientFallback implements SafetyClient{

        @Override
        public Map<String, Object> getProjectById(Long id) {
            return null;
        }

        @Override
        public void saveSysReminder(SysReminder sysReminder) {

        }
    }

}
