package com.unity;

import com.unity.common.base.SessionHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * server
 * <p>
 * Create by Jung at 2018年05月14日17:48:25
 */

@EnableFeignClients
@EnableTransactionManagement
//@EnableAutoConfiguration
@SpringBootApplication
//@EnableEurekaClient
@EnableScheduling
@EnableDiscoveryClient
@EnableAsync
@MapperScan("com.unity.system.dao")
@RestController
@RequestMapping(value = "/demo1")
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

    @RequestMapping(value = "/first", method = RequestMethod.GET)
    public Object firstResp (){
        HttpServletRequest request = SessionHolder.getRequest();
        Map<String, Object> map = (Map)request.getSession(). getAttribute("map");
        if(map==null)  map =   new HashMap<>();

        map.put("request Url demo2", request.getRequestURL());
        request.getSession().setAttribute("map", map);
        return map;
    }

    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public Object sessions (){
        HttpServletRequest request = SessionHolder.getRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", request.getSession().getId());
        map.put("message", request.getSession().getAttribute("map"));
        return map;
    }
}