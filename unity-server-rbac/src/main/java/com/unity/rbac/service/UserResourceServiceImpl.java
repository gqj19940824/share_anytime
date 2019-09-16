
package com.unity.rbac.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.unity.common.base.BaseServiceImpl;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.dao.UserResourceDao;
import com.unity.rbac.entity.UserResource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * ClassName: UserResourceService
 * date: 2018-12-12 20:21:09
 * 
 * @author creator
 * @since JDK 1.8
 */
@Service
public class UserResourceServiceImpl extends BaseServiceImpl<UserResourceDao,UserResource> implements IService<UserResource> {

    /**
     * 批量插入
     *
     * @param userResourceList 用户与资源关系集
     * @author gengjiajia
     * @since 2019/05/10 13:51
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertBatch(List<UserResource> userResourceList){
        //此处判断集合大小，大于批量最大条数则进行切割
        //调用递归方法
        recursiveInsertBatch(userResourceList);
    }

    /**
     * 递归批量插入
     *
     * @param  sourceList 源数据集
     * @author gengjiajia
     * @since 2019/05/10 14:56
     */
    private void recursiveInsertBatch(List<UserResource> sourceList){
        if(CollectionUtils.isNotEmpty(sourceList)){
            if(sourceList.size() > UserConstants.MAX_BATCH_INSERT_NUM){
                //超过最大插入条数，获取集合前 最大数
                List<UserResource> newList = sourceList.stream().limit(UserConstants.MAX_BATCH_INSERT_NUM).collect(Collectors.toList());
                //从源数据列表中移除要插入的数据，必须要先移除，因为插入数据后id会存在，就无法移除成功
                sourceList = sourceList.subList(UserConstants.MAX_BATCH_INSERT_NUM,sourceList.size());
                //插入前 最大条数
                baseMapper.insertBatch(newList);
                //递归调用
                recursiveInsertBatch(sourceList);
            } else {
                //不足 最大条数，一次插入
                baseMapper.insertBatch(sourceList);
            }
        }
    }
}
