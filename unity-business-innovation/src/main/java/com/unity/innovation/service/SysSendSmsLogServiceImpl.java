
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import com.unity.innovation.dao.SysSendSmsLogDao;
import com.unity.innovation.entity.SysSendSmsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: SysSendSmsLogService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-10-17 21:24:33
 *
 * @author G
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysSendSmsLogServiceImpl extends BaseServiceImpl<SysSendSmsLogDao, SysSendSmsLog> {


}
