
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.UserToken;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户令牌
 * @author creator
 * 生成时间 2018-12-12 20:14:55
 */
@Transactional(rollbackFor = Exception.class)
public interface UserTokenDao  extends BaseDao<UserToken> {
	
}

