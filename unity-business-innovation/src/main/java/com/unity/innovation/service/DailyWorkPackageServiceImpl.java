
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.dao.DailyWorkPackageDao;
import com.unity.innovation.entity.DailyWorkPackage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

 /**
 * 
 * ClassName: DailyWorkPackageService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 11:17:02
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DailyWorkPackageServiceImpl extends BaseServiceImpl<DailyWorkPackageDao,DailyWorkPackage>{

 }
