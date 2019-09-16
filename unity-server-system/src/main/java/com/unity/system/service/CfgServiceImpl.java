
package com.unity.system.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.system.entity.Cfg;
import com.unity.system.dao.CfgDao;

 /**
 * 
 * ClassName: CfgService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2018-12-21 13:31:11
 * 
 * @author creator 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CfgServiceImpl extends BaseServiceImpl<CfgDao,Cfg>{

     
}
