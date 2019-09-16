
package com.unity.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unity.common.base.BaseServiceImpl;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.dao.RoleResourceDao;
import com.unity.rbac.entity.Resource;
import com.unity.rbac.entity.RoleResource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * ClassName: RoleResourceService
 * date: 2018-12-12 20:21:07
 * 
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleResourceServiceImpl extends BaseServiceImpl<RoleResourceDao,RoleResource> implements IService<RoleResource> {


    /**
     * 通过角色id查询所关联的资源
     *
     * @param  roleId 角色id
     * @return 关联的资源
     * @author gengjiajia
     * @since 2019/07/03 09:51
     */
    public List<Resource> getRoleLinkedResourceListByRoleId(Long roleId){
        return baseMapper.getRoleLinkedResourceListByRoleId(roleId);
    }

    /**
     * 批量插入
     *
     * @param roleResourceList 角色与资源关系集
     * @author gengjiajia
     * @since 2019/05/10 13:51
     */
    public void insertBatch(List<RoleResource> roleResourceList){
        //此处判断集合大小，大于批量最大条数则进行切割
        //调用递归方法
        recursiveInsertBatchRoleResource(roleResourceList);
    }

    /**
     * 递归批量插入
     *
     * @param  sourceList 源数据集
     * @author gengjiajia
     * @since 2019/05/10 14:56
     */
    private void recursiveInsertBatchRoleResource(List<RoleResource> sourceList) {
        if(CollectionUtils.isEmpty(sourceList)){
            return;
        }
        if(sourceList.size() > UserConstants.MAX_BATCH_INSERT_NUM){
            //超过最大插入条数，获取集合前 最大数
            List<RoleResource> newList = sourceList.stream().limit(UserConstants.MAX_BATCH_INSERT_NUM).collect(Collectors.toList());
            //从源数据列表中移除要插入的数据，必须要先移除，因为插入数据后id会存在，就无法移除成功
            sourceList = sourceList.subList(UserConstants.MAX_BATCH_INSERT_NUM,sourceList.size());
            //插入前 最大条数
            baseMapper.insertBatch(newList);
            //递归调用
            recursiveInsertBatchRoleResource(sourceList);
        } else {
            //不足 最大条数，一次插入
            baseMapper.insertBatch(sourceList);
        }
    }

    /**
     * 通过用户id查询用户关联的角色来获取角色关联的资源列表
     *
     * @param  userId 用户id
     * @return 角色关联的资源列表
     * @author gengjiajia
     * @since 2019/07/03 18:59
     */
    public List<Resource> getRoleResourceCodeListByUserId(Long userId) {
        return baseMapper.getRoleResourceCodeListByUserId(userId);
    }
}
