package com.unity.common.utils;

import com.unity.common.client.RbacClient;
import com.unity.common.client.SafetyClient;
import com.unity.common.constant.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * redis工具类
 * create at 2019年07月11日13:41:57
 *
 * @author gengjiajia
 */
@Component
@Slf4j
public class HashRedisUtils {
    private static final Logger logger = LoggerFactory.getLogger(HashRedisUtils.class);

    @Resource
    private RedisTemplate<String, Map<String, Object>> redisTemplate;
    @Resource
    private RbacClient rbacClient;
    @Resource
    private SafetyClient safetyClient;

    /**
     * 给valueOperation设置值
     *
     * @param key   键
     * @param value 值
     * @author gengjiajia
     * @since 2019年07月11日13:56:45
     */
    private synchronized void valueOperationSet(String key, Map<String, Object> value) {
        redisTemplate.opsForHash().putAll(key, value);
    }

    /**
     * 存入指定对象
     *
     * @param key   redis 中的key
     * @param value 当前要存储的对象
     * @author gengjiajia
     * @since 2019年07月11日13:56:45
     */
    @Async
    public void putValueByKey(String key, Map<String, Object> value) {
        valueOperationSet(key, value);
    }


    /**
     * 移除当前的对象
     *
     * @param key 指定的redis中的key
     * @author gengjiajia
     * @since 2019-07-11 13:45:38
     */
    public void removeValueByKey(String key) {
        redisTemplate.delete(key);
    }

    public void delete(Set<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 获得指定的实体对象
     *
     * @param key 需要获得的key
     * @return 得到的实体 如类转换异常，则返回null
     * @author gengjiajia
     * @since 2019-07-11 13:45:38
     */
    public <T> T getObj(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return commonGetObjValue(key);
    }

    /**
     * 获得指定的实体对象属性值
     *
     * @param key     需要获得的组key 例如 USER:212
     * @param hashKey 对象的属性名 例如 name
     * @return 实体对象属性值 如类转换异常，则返回null
     * @author gengjiajia
     * @since 2019-07-11 13:45:38
     */
    public <T> T getFieldValueByFieldName(String key, String hashKey) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(hashKey)) {
            return null;
        }
        return commonGetValue(key, hashKey);
    }

    /**
     * 公共-获取实体属性值的工具类
     *
     * @param key 需要拿的key
     * @return 得到的对象的指定属性值
     * @author gengjiajia
     * @since 2019-07-11 13:45:38
     */
    @SuppressWarnings("unchecked")
    private synchronized <T> T commonGetValue(String key, String hashKey) {
        try {
            Object value = redisTemplate.opsForHash().get(key, hashKey);
            if (value == null) {
                Map<String, Object> map = getDataByKeyId(key);
                if(MapUtils.isEmpty(map)){
                    return null;
                }
                putValueByKey(key,map);
                return (T) map.get(hashKey);
            }
            return (T) value;
        } catch (ClassCastException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Attention! Redis current user is NOT A DISTANCE of AdminUser, Please Check, Current key:{}", key);
            }
            return null;
        }
    }

    /**
     * 公共-获取实体的工具类
     *
     * @param key 需要拿的key
     * @return 得到的对象
     * @author gengjiajia
     * @since 2019-07-11 13:45:38
     */
    @SuppressWarnings("unchecked")
    private <T> T commonGetObjValue(String key) {
        try {
            Map value = redisTemplate.opsForHash().entries(key);
            log.info("====== 《commonGetObjValue》 --- "+key+"：Redis {}",value);
            if (MapUtils.isEmpty(value)) {
                synchronized (this){
                    value = redisTemplate.opsForHash().entries(key);
                    if (MapUtils.isEmpty(value)){
                        value = getDataByKeyId(key);
                        log.info("====== 《commonGetObjValue》 --- "+key+"：DB {}",value);
                        putValueByKey(key,value);
                    }
                }
            }
            return (T) value;
        } catch (Exception e) {
            logger.error("Attention! Redis current user is NOT A DISTANCE of AdminUser, Please Check, Current key:{}" + key, e);
            return null;
        }
    }

    /**
     * 解析key获取对应数据
     *
     * @param  key 建
     * @return 对应数据
     * @author gengjiajia
     * @since 2019/07/12 15:38
     */
    private Map<String,Object> getDataByKeyId(String key){
        Long id;
        if (key.contains(RedisConstants.USER)) {
            id = Long.parseLong(key.replace(RedisConstants.USER, ""));
            return rbacClient.getUserById(id);
        } else if (key.contains(RedisConstants.DEPARTMENT)) {
            id = Long.parseLong(key.replace(RedisConstants.DEPARTMENT, ""));
            return rbacClient.getDepartmentById(id);
        } else if(key.contains(RedisConstants.PROJECT))  {
            id = Long.parseLong(key.replace(RedisConstants.PROJECT, ""));
            return safetyClient.getProjectById(id);
        }
        return null;
    }
}
