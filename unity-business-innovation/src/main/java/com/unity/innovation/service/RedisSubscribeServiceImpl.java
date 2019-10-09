package com.unity.innovation.service;

import com.unity.common.pojos.Dic;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.entity.IplTimeOutLog;
import com.unity.innovation.enums.ListCategoryEnum;
import com.unity.innovation.enums.TimeTypeEnum;
import com.unity.innovation.enums.UnitCategoryEnum;
import com.unity.innovation.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 测试redis的订阅与发布
 * <p>
 * create by zhangxiaogang at 2019/9/23 10:07
 */
@Service
@Slf4j
public class RedisSubscribeServiceImpl {

    @Resource
    private DicUtils dicUtils;
    @Resource
    private IplTimeOutLogServiceImpl iplTimeOutLogService;



    /**
     * 保存清单超时处理信息
     *
     * @param id   清单id
     * @param time 选择超时hours/days
     * @param type 清单类别
     * @author zhangxiaogang
     * @since 2019/10/8 14:05
     */
    public String saveSubscribeInfo(String id, String time, String type) {
        Dic dicByCode = dicUtils.getDicByCode(ListTypeConstants.LIST_TIMEOUT, time);
        String key = ListTypeConstants.LIST_CONTROL.concat(type).concat(time).concat(":").concat(id);
        log.info("【记录redis存储的key:】" + key);
        String result = "error";
        if (dicByCode != null) {
            String[] idArrays = id.split("-");
            //超时未处理
            if (ListTypeConstants.HOURS.equals(time)) {
                result = RedisPoolUtil.setEx(key, key, (Integer.valueOf(dicByCode.getDicValue()) * 3600));
                //超时未更新
            } else if (ListTypeConstants.DAYS.equals(time)) {
                result = RedisPoolUtil.setEx(key, key, (Integer.valueOf(dicByCode.getDicValue()) * 3600 * 24));
            } else {
                log.info("请确认参数");
            }
            if("OK".equals(result)){
                recordTimeOutLog(time,idArrays);
            }
        }
        return result;
    }

    /**
     * 记录超时日志
     *
     * @param time 超时类别
     * @param idArrays id数组
     *@author zhangxiaogang
     *@since 2019/10/8 18:37
     */
    private void recordTimeOutLog(String time, String... idArrays){
        IplTimeOutLog iplTimeOutLog = new IplTimeOutLog();
        iplTimeOutLog.setMainId(Long.valueOf(idArrays[0]));
        Long aLong = Long.valueOf(idArrays[1]);
        Long departmentId = ListCategoryEnum.valueOfName(ListTypeConstants.CITY_CONTROL.substring(0,ListTypeConstants.CITY_CONTROL.length()-1)).getId();
        iplTimeOutLog.setListCategory(departmentId);
        //主责
        if(aLong.intValue() == 0){
            iplTimeOutLog.setUnitCategory(UnitCategoryEnum.MAIN.getId());
            iplTimeOutLog.setDepartmentId(departmentId);
            //协同
        }else {
            iplTimeOutLog.setUnitCategory(UnitCategoryEnum.COORDINATION.getId());
            iplTimeOutLog.setDepartmentId(aLong);
        }
        iplTimeOutLog.setTimeType(time);
        iplTimeOutLogService.save(iplTimeOutLog);

    }
}
