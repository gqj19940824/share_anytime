
package com.unity.system.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.system.entity.Dic;
import com.unity.system.dao.DicDao;

 /**
 * 
 * ClassName: DicService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-07-23 16:34:48
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DicServiceImpl extends BaseServiceImpl<DicDao,Dic>{

     
}
