package com.unity.common.utils;

import com.alibaba.fastjson.JSON;
import com.unity.common.client.SystemClient;
import com.unity.common.constant.RedisConstants;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.DicGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典表工具类
 * create at 2019年07月11日13:41:57
 *
 * @author qinhuan
 */
@Component
@Slf4j
public class DicUtils {

    @Resource
    private SystemClient systemClient;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据字典组编码和字典项编码查询字典值
     *
     * @param groupCode 字典组编码
     * @param dicCode 字典项编码
     * @author qinhuan
     * @since 2019年07月11日13:56:45
     */
    public String getDicValueByCode(String groupCode, String dicCode) {
        String value = "";
        Dic dicByCode = getDicByCode(groupCode, dicCode);
        if (dicByCode != null){
            value = dicByCode.getDicValue();
        }
        return value;
    }

    /**
     * 根据字典组编码和字典项编码查询字典
     *
     * @param groupCode 字典组编码
     * @param dicCode 字典项编码
     * @author qinhuan
     * @since 2019年07月11日13:56:45
     */
    public Dic getDicByCode(String groupCode, String dicCode) {
        if (StringUtils.isEmpty(groupCode) || StringUtils.isEmpty(dicCode)){
            return null;
        }

        String key = RedisConstants.DIC_PREFIX + groupCode;
        String dicString = (String) redisTemplate.opsForHash().get(key, dicCode);
        if (StringUtils.isEmpty(dicString)){
            synchronized (this){
                dicString = (String) redisTemplate.opsForHash().get(key, dicCode);
                if (StringUtils.isEmpty(dicString)){
                    // load from db
                    Dic dicByCode = systemClient.getDicByCode(groupCode, dicCode);
                    // set to redis
                    if (dicByCode != null){
                        redisTemplate.opsForHash().put(key, dicCode, JSON.toJSONString(dicByCode));
                    }
                    return dicByCode;
                }
            }
        }

        return JSON.parseObject(dicString, Dic.class);
    }

    /**
     * 根据字典组编码查询字典组
     *
     * @param groupCode 字典组编码
     * @author qinhuan
     * @since 2019年07月11日13:56:45
     */
    public DicGroup getDicGroupByGroupCode(String groupCode) {
        if(StringUtils.isEmpty(groupCode)){
            return null;
        }
        String key = RedisConstants.DICGROUP;
        String dicGroupString = (String) redisTemplate.opsForHash().get(key, groupCode);
        if (StringUtils.isEmpty(dicGroupString)){
            synchronized (this){
                dicGroupString = (String) redisTemplate.opsForHash().get(key, groupCode);
                if (StringUtils.isEmpty(dicGroupString)){
                    // load from db
                    DicGroup dicGroupByGroupCode = systemClient.getDicGroupByGroupCode(groupCode);
                    // set to redis
                    if(dicGroupByGroupCode != null){
                        redisTemplate.opsForHash().put(key, groupCode, JSON.toJSONString(dicGroupByGroupCode));
                    }
                    return dicGroupByGroupCode;
                }
            }
        }
        return JSON.parseObject(dicGroupString, DicGroup.class);
    }

    /**
     * 根据字典组编码查询字典项列表
     *
     * @param groupCode 字典组编码
     * @author qinhuan
     * @since 2019年07月11日13:56:45
     */
    public List<Dic> getDicsByGroupCode(String groupCode){
        if (StringUtils.isEmpty(groupCode)){
            return new ArrayList<>();
        }
        return systemClient.getDicsByGroupCode(groupCode);
    }
}
