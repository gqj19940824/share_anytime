
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.IplSupervisionMain;
import com.unity.innovation.dao.IplSupervisionMainDao;

 /**
 * 
 * ClassName: IplSupervisionMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-23 15:34:08
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IplSupervisionMainServiceImpl extends BaseServiceImpl<IplSupervisionMainDao,IplSupervisionMain>{

     
}
