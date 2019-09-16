
package com.unity.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unity.common.base.BaseServiceImpl;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.dao.ResourceIdentityDao;
import com.unity.rbac.entity.Resource;
import com.unity.rbac.entity.ResourceIdentity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * ClassName: ResourceIdentityService
 * date: 2018-12-12 20:21:07
 * 
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceIdentityServiceImpl extends BaseServiceImpl<ResourceIdentityDao,ResourceIdentity> implements IService<ResourceIdentity> {

    /**
     * 查询指定身份关联的资源
     *
     * @param  identityId 指定身份
     * @return 指定身份关联的资源
     * @author gengjiajia
     * @since 2018/12/15 13:45
     */
    public List<Map<String,Object>> selectResourceByIdentity(Long identityId){
        return this.baseMapper.selectResourceByIdentity(identityId);
    }

    /**
     * 批量插入
     *
     * @param  bindResourceIdentityList 身份与资源关系
     * @author gengjiajia
     * @since 2019/07/03 10:41
     */
    public void insertBatch(List<ResourceIdentity> bindResourceIdentityList) {
        //此处判断集合大小，大于批量最大条数则进行切割
        //调用递归方法
        recursiveInsertBatchResourceIdentity(bindResourceIdentityList);
    }

    /**
     * 递归批量插入
     *
     * @param  sourceList 源数据集
     * @author gengjiajia
     * @since 2019/05/10 14:56
     */
    private void recursiveInsertBatchResourceIdentity(List<ResourceIdentity> sourceList) {
        if(CollectionUtils.isEmpty(sourceList)){
            return;
        }
        if(sourceList.size() > UserConstants.MAX_BATCH_INSERT_NUM){
            //超过最大插入条数，获取集合前 最大数
            List<ResourceIdentity> newList = sourceList.stream().limit(UserConstants.MAX_BATCH_INSERT_NUM).collect(Collectors.toList());
            //从源数据列表中移除要插入的数据，必须要先移除，因为插入数据后id会存在，就无法移除成功
            sourceList = sourceList.subList(UserConstants.MAX_BATCH_INSERT_NUM,sourceList.size());
            //插入前 最大条数
            baseMapper.insertBatch(newList);
            //递归调用
            recursiveInsertBatchResourceIdentity(sourceList);
        } else {
            //不足 最大条数，一次插入
            baseMapper.insertBatch(sourceList);
        }
    }

    /**
     * 通过身份id获取关联的资源
     *
     * @param  identityId 身份id
     * @return 关联的资源级次编码
     * @author gengjiajia
     * @since 2019/07/03 11:15
     */
    public List<Resource> getIdentityLinkedResourceListByIdentityId(Long identityId) {
        return baseMapper.getIdentityLinkedResourceListByIdentityId(identityId);
    }

}
