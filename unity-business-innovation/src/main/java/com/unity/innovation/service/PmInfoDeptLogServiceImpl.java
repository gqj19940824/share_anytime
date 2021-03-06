
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unity.innovation.entity.PmInfoDeptLog;
import com.unity.innovation.dao.PmInfoDeptLogDao;

/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PmInfoDeptLogServiceImpl extends BaseServiceImpl<PmInfoDeptLogDao, PmInfoDeptLog> {


}
