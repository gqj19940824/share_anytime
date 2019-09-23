
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.SysCfgScope;
import com.unity.innovation.dao.SysCfgScopeDao;

 /**
 * 
 * ClassName: SysCfgScopeService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:22:23
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysCfgScopeServiceImpl extends BaseServiceImpl<SysCfgScopeDao,SysCfgScope>{

     
}
