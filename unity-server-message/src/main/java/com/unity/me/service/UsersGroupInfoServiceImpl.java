package com.unity.me.service;

import com.unity.common.base.BaseServiceImpl;
import com.unity.me.dao.UsersGroupInfoDao;
import com.unity.me.entity.UsersGroupInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: UsersGroupInfoService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-02-12 12:47:36
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UsersGroupInfoServiceImpl extends BaseServiceImpl<UsersGroupInfoDao, UsersGroupInfo> {

    /**
     * 获取当前登录人的数据资源
     *
     * @return 数据资源
     * @author gengjiajia
     * @since 2019/02/14 17:33
     */
    public List<UsersGroupInfo> getUsersGroupInfo(Long groupInfoId) {
        //查询组织下所有的用户
        return baseMapper.selectMeUserByGroupInfo(groupInfoId);
    }
}