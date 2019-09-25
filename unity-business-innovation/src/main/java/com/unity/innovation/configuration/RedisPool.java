package com.unity.innovation.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *Jedis对象不是线程安全的，在多线程下使用同一个Jedis对象会出现并发问题，为了避免每次使用Jedis对象时都需要重新创建，Jedis提供了JedisPool。Jedis是线程安全的连接池
 *
 *<p>
 *create by zhangxiaogang at 2019/9/23 16:25
 */
@Configuration
public class RedisPool {

    private static JedisPool pool;//jedis连接池
    /*@Value("${spring.redis.jedis.pool.max-active}")
    private static Integer maxActive; //最大连接数
    @Value("${spring.redis.jedis.pool.max-idle}")
    private static Integer maxIdle;//在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    @Value("${spring.redis.jedis.pool.min-idle}")
    private static Integer minIdle;//在jedispool中最小的idle状态(空闲的)的jedis实例的个数jedis:
    @Value("${spring.redis.borrow}")
    private static Boolean testOnBorrow;//在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    @Value("${spring.redis.return}")
    private static Boolean testOnReturn;//在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。
    @Value("${spring.redis.host}")
    private static String redisIp;
    @Value("${spring.redis.timeout}")
    private static int timeout;//超时时间
    @Value("${spring.redis.port}")
    private static Integer redisPort;*/


    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(200);
        config.setMaxIdle(50);
        config.setMinIdle(20);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。
        pool = new JedisPool(config,"172.19.1.97",6379,3000,"Jingkai@jingcaiwang.cn");
    }

    //静态代码块，初始化Redis池
    static{
        initPool();
    }

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


}
