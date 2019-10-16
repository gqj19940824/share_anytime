
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.entity.PmInfoDept;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.ListCategoryEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.PmInfoDeptLog;
import com.unity.innovation.dao.PmInfoDeptLogDao;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PmInfoDeptLogServiceImpl extends BaseServiceImpl<PmInfoDeptLogDao, PmInfoDeptLog> {


}
