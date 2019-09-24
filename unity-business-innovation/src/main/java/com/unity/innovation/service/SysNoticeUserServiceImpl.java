
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.SysNoticeUser;
import com.unity.innovation.dao.SysNoticeUserDao;

 /**
 * 
 * ClassName: SysNoticeUserService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-23 15:00:35
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysNoticeUserServiceImpl extends BaseServiceImpl<SysNoticeUserDao,SysNoticeUser>{

     
}
