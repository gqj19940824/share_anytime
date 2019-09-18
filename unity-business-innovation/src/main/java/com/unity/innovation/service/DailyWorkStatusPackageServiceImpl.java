
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.dao.DailyWorkStatusPackageDao;
import com.unity.innovation.entity.DailyWorkStatusPackage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: DailyWorkStatusPackageService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 11:17:02
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DailyWorkStatusPackageServiceImpl extends BaseServiceImpl<DailyWorkStatusPackageDao, DailyWorkStatusPackage> {


    public IPage<DailyWorkStatusPackage> listByPage(PageEntity<DailyWorkStatusPackage> search) {
        return null;
    }
}
