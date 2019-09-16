
package com.unity.system.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.system.entity.Dictionary;
import com.unity.system.dao.DictionaryDao;

 /**
 * 
 * ClassName: DictionaryService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2018-12-21 13:31:12
 * 
 * @author creator 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DictionaryServiceImpl extends BaseServiceImpl<DictionaryDao,Dictionary>{


}
