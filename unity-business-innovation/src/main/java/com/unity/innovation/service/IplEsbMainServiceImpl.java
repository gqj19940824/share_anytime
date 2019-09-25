
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.IplEsbMain;
import com.unity.innovation.dao.IplEsbMainDao;

 /**
 * 
 * ClassName: IplEsbMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-25 14:51:39
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IplEsbMainServiceImpl extends BaseServiceImpl<IplEsbMainDao,IplEsbMain>{

     
}
