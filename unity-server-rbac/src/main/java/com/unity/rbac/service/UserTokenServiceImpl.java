
package com.unity.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.rbac.dao.UserTokenDao;
import com.unity.rbac.entity.UserToken;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ClassName: UserTokenService
 * date: 2018-12-12 20:21:11
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserTokenServiceImpl extends BaseServiceImpl<UserTokenDao, UserToken> implements IService<UserToken> {

    /**
     * 根据身份获取对应的token
     *
     * @param  
     * @return 
     * @author gengjiajia
     * @since 2019/03/18 16:01  
     */
    public String findUserTokenByUserIdAndIdentity(Long userId, Long identity) {
        LambdaQueryWrapper<UserToken> wrapper = new QueryWrapper<UserToken>().lambda();
        wrapper.eq(UserToken::getIdRbacUser,userId);
        //后台登录 获取后台前一次的token
        if(identity.intValue() == PlatformTypeEnum.SYSTEM.getType() ){
            wrapper.eq(UserToken::getIdRbacIdentity, identity).orderByDesc(UserToken::getGmtCreate);
            List<UserToken> tokenList = super.list(wrapper);
            if(CollectionUtils.isNotEmpty(tokenList)){
                Optional<String> first = tokenList.stream().map(UserToken::getToken).findFirst();
                return first.get();
            }
        } else if(identity.intValue() == PlatformTypeEnum.WEB.getType()){
            //PC登录 获取PC前一次的token
            wrapper.eq(UserToken::getIdRbacIdentity,(long)PlatformTypeEnum.WEB.getType())
                    .orderByDesc(UserToken::getGmtCreate);
            List<UserToken> tokenList = super.list(wrapper);
            if(CollectionUtils.isNotEmpty(tokenList)){
                Optional<String> first = tokenList.stream().map(UserToken::getToken).findFirst();
                return first.get();
            }
        } else {
            //
            wrapper.notIn(UserToken::getIdRbacIdentity,new Long[]{(long)PlatformTypeEnum.SYSTEM.getType(),(long)PlatformTypeEnum.WEB.getType()})
                    .orderByDesc(UserToken::getGmtCreate);
            List<UserToken> tokenList = super.list(wrapper);
            if(CollectionUtils.isNotEmpty(tokenList)){
                Optional<String> first = tokenList.stream().map(UserToken::getToken).findFirst();
                return first.get();
            }
        }
        return null;
    }
}
