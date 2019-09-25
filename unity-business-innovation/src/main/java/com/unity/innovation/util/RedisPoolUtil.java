package com.unity.innovation.util;

import com.unity.innovation.configuration.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * <p>
 * create by zhangxiaogang at 2019/9/23 16:42
 */
@Slf4j
public class RedisPoolUtil {


    /**
     * 设置key的有效期，单位是秒
     *
     * @param key    设置key
     * @param exTime 超时时间
     * @return 设置结果
     * @author zhangxiaogang
     * since 2019/9/23 19:43
     */
    public static Long expire(String key, int exTime) {
        Jedis jedis = null;
        Long result = null;
        try {
            //从Redis连接池中获得Jedis对象
            jedis = RedisPool.getJedis();
            //设置成功则返回Jedis对象
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key:{} error", key, e);
        }
        RedisPool.closeResource(jedis);
        return result;
    }

    /**
     * exTime的单位是秒
     * 设置key-value并且设置超时时间
     *
     * @param key    键
     * @param value  key对应value
     * @param exTime 超时时间
     * @return 设置结果
     * @author zhangxiaogang
     * @since 2019/9/23 19:46
     */
    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} error", key, value, e);
        }
        RedisPool.closeResource(jedis);
        return result;
    }

    /**
     * 向redis 设置值
     *
     * @param key   设置key
     * @param value 对于的值
     * @return 设置结果
     * @author zhangxiaogang
     * @since 2019/9/23 19:43
     */
    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
        }
        RedisPool.closeResource(jedis);
        return result;
    }

    /**
     * @param key 根据key获取值
     * @return redis存储的内容
     * @author zhangxiaogang
     * @since 2019/9/23 19:27
     */
    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
        }
        RedisPool.closeResource(jedis);
        return result;
    }

    /**
     * 删除redis中的内容
     *
     * @param key redis中的key
     * @return 删除结果
     * @author zhangxiaogang
     * @since 2019/9/23 19:25
     */
    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
        }
        RedisPool.closeResource(jedis);
        return result;
    }
}
