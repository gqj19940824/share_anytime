
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.InfoDeptSatb;
import com.unity.innovation.dao.InfoDeptSatbDao;

 /**
 *
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InfoDeptSatbServiceImpl extends BaseServiceImpl<InfoDeptSatbDao,InfoDeptSatb>{

     
}
