
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.dao.SysCfgDao;

 /**
 * 
 * ClassName: SysCfgService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 14:53:55
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysCfgServiceImpl extends BaseServiceImpl<SysCfgDao,SysCfg>{

     
}
