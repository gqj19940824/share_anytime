package com.unity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * server
 * <p>
 * Create by Jung at 2018年05月14日17:48:25
 */

@EnableFeignClients
@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableAsync
@MapperScan("com.unity.rbac.dao")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}