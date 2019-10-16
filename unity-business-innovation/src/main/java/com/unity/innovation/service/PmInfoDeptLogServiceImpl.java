
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.PmInfoDeptLog;
import com.unity.innovation.dao.PmInfoDeptLogDao;

 /**
 * 
 * ClassName: PmInfoDeptLogService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-10-15 15:33:01
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PmInfoDeptLogServiceImpl extends BaseServiceImpl<PmInfoDeptLogDao,PmInfoDeptLog>{

     
}
