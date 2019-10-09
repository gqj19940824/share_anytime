package com.unity.innovation.controller;

import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.service.RedisSubscribeServiceImpl;
import com.unity.innovation.util.RedisPoolUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 测试 redis订阅与发布功能
 *<p>
 *create by zhangxiaogang at 2019/9/23 10:06
 */
@RestController
@RequestMapping("redisSubscribe")
public class RedisSubscribeController {

    @Resource
    private RedisSubscribeServiceImpl redisSubscribeService;

    @GetMapping("/pushRedisSubscribeInfo")
    public void pushRedisSubscribeInfo(@RequestParam("hours")String hours, @RequestParam("id") Long id ){

        String key = ListTypeConstants.LIST_CONTROL.concat(ListTypeConstants.CITY_CONTROL).concat(hours).concat(":").concat(id.toString());
        System.out.println(key);
        String mytest = RedisPoolUtil.setEx(key, key, 30);

        System.out.println(mytest);

        redisSubscribeService.saveSubscribeInfo("12-0",ListTypeConstants.HOURS,ListTypeConstants.CITY_CONTROL);
    }


}
