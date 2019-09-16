
package com.unity.resource.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.resource.entity.ResourceType;
import com.unity.resource.dao.ResourceTypeDao;

 /**
 * 
 * ClassName: ResourceTypeService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-01-25 13:46:29
 * 
 * @author creator 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceTypeServiceImpl extends BaseServiceImpl<ResourceTypeDao,ResourceType>{

     
}
