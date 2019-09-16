package com.unity.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "unity-server-me", fallback = MessageClient.HystrixClientFallback.class)
public interface MessageClient {

    @PostMapping("/feign/sms/sendVerificationCode")
    Map<String, Object> sendVerificationCode(@RequestBody Map<String, Object> params);

    /**
     * 获取系统通知信息数量
     *
     * @return 系统通知数量
     * @author zhangxiaogang
     * @since 2019/4/25 16:30
     */
    @GetMapping("/feign/message/getSystemMessageInfos")
    Map<String, Object> getSystemMessageInfos();

    @PostMapping("/feign/sms/checkVerificationCode")
    Map<String, Object> checkVerificationCode(@RequestBody Map<String, Object> params);

    /**
     * 发送哨声结束通知
     *
     * @param params 哨声通知参数 phone 电话 content[] 内容
     * @author zhangxiaogang
     * @since 2019/4/29 14:59
     */
    @PostMapping("/feign/sms/sendVerificationNotice")
    void sendVerificationNotice(@RequestBody Map<String, Object> params);

    /**
     * 发送哨声结束通知多个对象
     *
     * @param paramList 哨声通知参数 phone 电话 content[] 内容
     * @author zhangxiaogang
     * @since 2019/4/29 14:59
     */
    @PostMapping("/feign/sms/sendVerificationNotices")
    void sendVerificationNotices(@RequestBody List<Map<String, Object>> paramList);

    @Component
    class HystrixClientFallback implements MessageClient {

        @Override
        public Map<String, Object> sendVerificationCode(Map<String, Object> params) {
            return null;
        }

        @Override
        public Map<String, Object> getSystemMessageInfos() {
            return null;
        }

        @Override
        public Map<String, Object> checkVerificationCode(Map<String, Object> params) {
            return null;
        }

        @Override
        public void sendVerificationNotice(Map<String, Object> params) {

        }

        @Override
        public void sendVerificationNotices(List<Map<String, Object>> paramList) {

        }
    }
}
