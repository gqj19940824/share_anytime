package com.unity.innovation.service;

import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 测试redis的订阅与发布
 *<p>
 *create by zhangxiaogang at 2019/9/23 10:07
 */
public class RedisSubscribeServiceImpl {

    @Resource
    private RedisTemplate<String, Map<String, Object>> redisTemplate;


    public void saveSubscribeInfo(){
        //redisTemplate.
    }
}
