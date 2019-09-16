
package com.unity.system.service;

import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.system.entity.Serial;
import com.unity.system.dao.SerialDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * ClassName: SerialService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2018-12-21 13:31:13
 * 
 * @author creator 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SerialServiceImpl extends BaseServiceImpl<SerialDao,Serial>{

 /**
  *  TODO 暂时不适用
  * @param paramMap
  * 前缀 serialPrefix
  * 后缀 serialSuffixes
  * 类型 serialType
  * @return
  *  校验：serialType类型不能为空
  */
// public LayUIResponse<String> callGenerateSyserialVal(HashMap paramMap)   {
//  LayUIResponse<String> response = new LayUIResponse<>();
//  if(paramMap.get("serialType")==null){
//   return response.error("类型不能为空");
//  }
//  this.baseMapper.callGenerateSyserialVal(paramMap);
//  //获取存储过程中的OUT 参数 返回值 工单号
//  String  servialNum= (String) paramMap.get("servialNum");
//  List<String> body = new ArrayList<>();
//  body.add(servialNum);
//  response.success(body, 1);
//  return response;
// }
}
