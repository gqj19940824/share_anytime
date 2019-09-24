
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import com.unity.innovation.dao.SysMessageReadLogDao;
import com.unity.innovation.entity.SysMessageReadLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: SysMessageReadLogService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-23 09:39:17
 *
 * @author G
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysMessageReadLogServiceImpl extends BaseServiceImpl<SysMessageReadLogDao, SysMessageReadLog> {


}
