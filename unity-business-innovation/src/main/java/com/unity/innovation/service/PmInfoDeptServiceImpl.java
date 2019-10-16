
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.enums.ListCategoryEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.PmInfoDept;
import com.unity.innovation.dao.PmInfoDeptDao;

import javax.annotation.Resource;
import java.util.List;

/**
 * 
 * ClassName: PmInfoDeptService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-10-15 15:33:01
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PmInfoDeptServiceImpl extends BaseServiceImpl<PmInfoDeptDao,PmInfoDept>{

 @Resource
 private DicUtils dicUtils;

 public LambdaQueryWrapper<PmInfoDept> wrapper(PmInfoDept entity) {
  LambdaQueryWrapper<PmInfoDept> ew = new LambdaQueryWrapper<>();
  Customer customer = LoginContextHolder.getRequestAttributes();
  List<Long> roleList = customer.getRoleList();
  if (entity != null) {
   //提交时间
   if (StringUtils.isNotBlank(entity.getSubmitTime())) {
    long end = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), false);
    ew.lt(PmInfoDept::getGmtSubmit, end);
    long begin = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), true);
    ew.gt(PmInfoDept::getGmtSubmit, begin);
   }
   //标识模块
   if (StringUtils.isNotBlank(entity.getCategory())) {
    ew.eq(PmInfoDept::getIdRbacDepartment, getDepartmentId(entity.getCategory()));
   } else {
    //非宣传部审批角色必传category
    if (!roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE)))) {
     throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
             .message("提交单位不能为空").build();
    }
   }
   //状态
   if (entity.getStatus() != null) {
    ew.eq(PmInfoDept::getStatus, entity.getStatus());
   }

   //宣传部审批角色不查看 待提交、已驳回
   if (roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE)))) {
    ew.notIn(PmInfoDept::getStatus, Lists.newArrayList(WorkStatusAuditingStatusEnum.TEN.getId(), WorkStatusAuditingStatusEnum.FORTY.getId()));
   }
   //排序
   ew.orderByDesc(PmInfoDept::getGmtSubmit, PmInfoDept::getGmtModified);
  } else {
   //只有宣传部角色可以查询所有单位数据
   if (!roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE)))) {
    throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
            .message("提交单位不能为空").build();
   }
  }
  return ew;
 }

 private Long getDepartmentId(String category){
  if(StringUtils.isBlank(category)) {
   throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
           .message("提交单位不能为空").build();
  }
  ListCategoryEnum listCategoryEnum = ListCategoryEnum.valueOfName(category);
  if(listCategoryEnum != null) {
   return listCategoryEnum.getId();
  }else {
   throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
           .message("提交单位错误").build();
  }
 }

}
