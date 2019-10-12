package com.unity.innovation.configuration;

import com.unity.common.util.GsonUtils;
import com.unity.common.util.SpringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 *Jedis对象不是线程安全的，在多线程下使用同一个Jedis对象会出现并发问题，为了避免每次使用Jedis对象时都需要重新创建，Jedis提供了JedisPool。Jedis是线程安全的连接池
 *
 *<p>
 *create by zhangxiaogang at 2019/9/23 16:25
 */
@Component
public class RedisPool implements ApplicationRunner {

    private static JedisPool pool;//jedis连接池


    /**
     * 获取链接资源
     *
     *@author zhangxiaogang
     *@since 2019/9/23 19:22
     */
    public static Jedis getJedis(){
        return pool.getResource();
    }
    /**
     * 关闭链接资源
     *
     * @param jedis redis链接
     *@author zhangxiaogang
     *@since 2019/9/23 19:22
     */
    public static void closeResource(Jedis jedis){
        if(jedis!=null){
            jedis.close();
        }
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        RedisProperties properties = SpringUtils.getBean(RedisProperties.class);
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(properties.getJedis().getPool().getMaxActive());
        config.setMaxIdle(properties.getJedis().getPool().getMaxIdle());
        config.setMinIdle(properties.getJedis().getPool().getMaxIdle());
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。
        pool = new JedisPool(config,properties.getHost(),properties.getPort(),3000,properties.getPassword());
    }
}
