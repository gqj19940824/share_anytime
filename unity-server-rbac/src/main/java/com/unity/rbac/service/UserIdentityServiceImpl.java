
package com.unity.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unity.common.base.BaseServiceImpl;
import com.unity.rbac.dao.UserIdentityDao;
import com.unity.rbac.entity.UserIdentity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: UserIdentityService
 * date: 2018-12-12 20:21:08
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserIdentityServiceImpl extends BaseServiceImpl<UserIdentityDao, UserIdentity> implements IService<UserIdentity> {

    /**
     * 查询用户关联的身份信息
     *
     * @param userId 用户id
     * @return 用户关联的身份信息
     * @author gengjiajia
     * @since 2018/12/14 19:42
     */
    public List<Long> selectIdentityIdsByUserId(Long userId) {
        return this.baseMapper.selectIdentityIdsByUserId(userId);
    }
}
