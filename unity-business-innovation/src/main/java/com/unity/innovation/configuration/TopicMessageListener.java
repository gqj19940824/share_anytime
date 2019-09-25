package com.unity.innovation.configuration;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监听redis发布事件
 *<p>
 *create by zhangxiaogang at 2019/9/23 11:01
 */
@Component
public class TopicMessageListener implements MessageListener {

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();// 请使用valueSerializer
        byte[] channel = message.getChannel();
        String topic = new String(channel);
        String itemValue = new String(body);
        // 请参考配置文件，本例中key，value的序列化方式均为string。
        System.out.println("topic:"+topic);
        System.out.println("itemValue:"+itemValue);
    }
}
