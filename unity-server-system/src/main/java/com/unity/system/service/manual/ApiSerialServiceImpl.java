
package com.unity.system.service.manual;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.StringUtil;
import com.unity.system.dao.SerialDao;
import com.unity.system.entity.Serial;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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
public class ApiSerialServiceImpl extends BaseServiceImpl<SerialDao,Serial>{
 private static Logger logger = LoggerFactory.getLogger(ApiSerialServiceImpl.class);

 public String callComGenerateSyserial(String code)   {
     SystemResponse<String> response = new SystemResponse<>();
     if(StringUtils.isEmpty(code)){
         throw UnityRuntimeException.newInstance()
                 .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                 .message("标识信息不能未空")
                 .build();
     }
     QueryWrapper<Serial> wrapper = new QueryWrapper<>();
     wrapper.lambda().eq(Serial::getSerialCode,code);
     Serial  nowSerial=this.baseMapper.selectOne(wrapper);
      if(nowSerial==null){
          throw UnityRuntimeException.newInstance()
                  .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                  .message("未获取到流水号信息")
                  .build();
      }
     String servialNum = generateSyserialCore(nowSerial);
//     response.success(servialNum);
     return servialNum;
 }

 /**
  * 根据类型生存流水号
  * @param nowSerial
  * {id:'1',name:'年月日+5位数字'},'2','年月日时+5位数字'},'3',name:'8位数字'}
  * @return 流水号
  */
  public  String generateSyserialCore(Serial  nowSerial){
      String sequence=null;
      String dateTimeStr="";
      StringBuilder  stringBuilder= new StringBuilder();
        switch (nowSerial.getSerialType()){
         case 1:
             dateTimeStr=dateTimeStr("yyyyMMdd");
             sequence= compareSequence(dateTimeStr,nowSerial.getSerialVal(),5);
           break;
         case 2:
             dateTimeStr= dateTimeStr("yyyyMMddHH");
             sequence= compareSequence(dateTimeStr,nowSerial.getSerialVal(),4);
             break;
         case 3:
             if(StringUtils.isEmpty(nowSerial.getSerialVal())){
                 sequence="0";
             }else{
                 sequence=nowSerial.getSerialVal();
             }
             sequence = getSequence(Long.parseLong(sequence)+1,8);
             break;
        }
      //更新流水号值
      nowSerial.setSerialVal(sequence);
      this.baseMapper.updateById(nowSerial);
      stringBuilder.append(nowSerial.getSerialPrefix())
                    .append(sequence)
                    .append(nowSerial.getSerialSuffixes());
    return stringBuilder.toString();
  }


 /**
  *
  * @param seq 序列号
  *            00001
  * @return
  */
 public  static String getSequence(long seq,Integer length) {
  String str = String.valueOf(seq);
  int len = str.length();
  if (len >= length) {// 取决于业务规模,应该不会到达
   return str;
  }
  int rest = length - len;
  StringBuilder sb = new StringBuilder();
  for (int i = 0; i < rest; i++) {
   sb.append('0');
  }
  sb.append(str);
  return sb.toString();
 }

    /**
     * @param pattern
     * yyyyMMddHHmmss
     * @return 格式化日期
     */
 public static String dateTimeStr(String pattern){
     DateFormat df = new SimpleDateFormat(pattern);
     Calendar calendar = Calendar.getInstance();
     String dateName = df.format(calendar.getTime());
     return  dateName;
 }

    /**
     *根据日期类型和当前时间的比较，序列号递增1或者初始化
     * @param dateTimeStr
     * @param serialVal
     * @param length 长度
     * @return
     */
    public  static  String compareSequence(String dateTimeStr,String  serialVal,int length){
    String sequence="";
    int len = dateTimeStr.length();
    //流水号中的日期和今天日期相同
    if(!StringUtils.isEmpty(serialVal)&&serialVal.substring(0,len).equals(dateTimeStr)){
        sequence =String.valueOf(Long.parseLong(serialVal)+1) ;
    }else{
        //从0开始
         sequence =dateTimeStr+getSequence(1,length);
        logger.debug("今日更新初始为1");
    }
    return  sequence;
}


}