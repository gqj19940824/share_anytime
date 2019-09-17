
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.entity.DailyWorkStatusLog;
import com.unity.dao.DailyWorkStatusLogDao;

 /**
 * 
 * ClassName: DailyWorkStatusLogService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 11:17:01
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DailyWorkStatusLogServiceImpl extends BaseServiceImpl<DailyWorkStatusLogDao,DailyWorkStatusLog>{

     
}