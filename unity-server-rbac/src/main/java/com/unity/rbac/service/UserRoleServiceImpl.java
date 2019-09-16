
package com.unity.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unity.common.base.BaseServiceImpl;
import com.unity.rbac.dao.UserRoleDao;
import com.unity.rbac.entity.User;
import com.unity.rbac.entity.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 
 * ClassName: UserRoleService
 * date: 2018-12-12 20:21:09
 * 
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserRoleServiceImpl extends BaseServiceImpl<UserRoleDao,UserRole> implements IService<UserRole> {


    /**
     * 根据用户查询关联的角色id
     *
     * @param userId 用户id
     * @return 用户查询关联的角色信息列表
     * @author gengjiajia
     * @since 2018/12/14 17:23
     */
    public List<Long> selectRoleIdsByUserId(Long userId){
        return this.baseMapper.selectRoleIdsByUserId(userId);
    }

    /**
     * 批量获取用户所有的角色名称列表
     *
     * @param  userIdList 用户id集
     * @return 角色名称列表
     * @author gengjiajia
     * @since 2019/08/28 20:27
     */
    public List<User> getGroupConcatRoleNameListByUserIdIn(List<Long> userIdList) {
        return baseMapper.getGroupConcatRoleNameListByUserIdIn(userIdList);
    }
}
