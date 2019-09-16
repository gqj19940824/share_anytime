package com.unity.system.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.RedisConstants;
import com.unity.system.dao.DicGroupDao;
import com.unity.system.entity.DicGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 
 * ClassName: DicGroupService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-07-23 16:34:48
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DicGroupServiceImpl extends BaseServiceImpl<DicGroupDao, DicGroup> {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据groupCode查询DicGroup
     *
     * @param  groupCode
     * @return DicGroup
     * @author qinhuan
     * @since 2019-07-25 19:41
     */
    public DicGroup getDicGroupByGroupCode(String groupCode){
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
                    DicGroup dicGroup = getOne(new LambdaQueryWrapper<DicGroup>().eq(DicGroup::getGroupCode, groupCode));
                    // set to redis
                    if (dicGroup != null){
                        redisTemplate.opsForHash().put(key, groupCode, JSON.toJSONString(dicGroup));
                    }

                    return dicGroup;
                }
            }
        }
        return JSON.parseObject(dicGroupString, DicGroup.class);
    }

}
