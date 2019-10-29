
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.MediaManager;
import com.unity.innovation.dao.MediaManagerDao;

 /**
 * 
 * ClassName: MediaManagerService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-10-28 13:41:56
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MediaManagerServiceImpl extends BaseServiceImpl<MediaManagerDao,MediaManager>{

     
}
