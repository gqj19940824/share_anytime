
package com.unity.rbac.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.rbac.entity.DefaultRole;
import com.unity.rbac.dao.DefaultRoleDao;

 /**
 * 
 * ClassName: DefaultRoleService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-01-11 17:13:27
 * 
 * @author creator 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultRoleServiceImpl extends BaseServiceImpl<DefaultRoleDao,DefaultRole>{

     
}
