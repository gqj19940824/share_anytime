
package com.unity.me.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.me.entity.GroupInfo;
import com.unity.me.dao.GroupInfoDao;

 /**
 * 
 * ClassName: GroupInfoService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-02-12 12:47:34
 * 
 * @author creator 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupInfoServiceImpl extends BaseServiceImpl<GroupInfoDao,GroupInfo>{

     
}
