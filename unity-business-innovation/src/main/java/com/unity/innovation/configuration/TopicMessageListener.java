package com.unity.innovation.configuration;

import com.unity.common.constant.RedisConstants;
import com.unity.innovation.entity.IplTimeOutLog;
import com.unity.innovation.enums.ListCategoryEnum;
import com.unity.innovation.enums.UnitCategoryEnum;
import com.unity.innovation.service.IplTimeOutLogServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监听redis发布事件
 *<p>
 *create by zhangxiaogang at 2019/9/23 11:01
 */
@Component
@Slf4j
public class TopicMessageListener implements MessageListener {

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;
    @Resource
    private IplTimeOutLogServiceImpl iplTimeOutLogService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();// 请使用valueSerializer
        String itemValue = new String(body);
        // 请参考配置文件，本例中key，value的序列化方式均为string。
        log.info("itemValue:"+itemValue);
        //itemValue:listControl:DEPARTMENT_DARB_CONTROL:DEAL_OVER_TIME:12-0
        String[] itemValueArrays = itemValue.split(RedisConstants.KEY_JOINER);
        if(itemValueArrays.length == 4) {
            String departmentType = itemValueArrays[1];
            ListCategoryEnum listCategoryEnum = ListCategoryEnum.valueOfName(departmentType);
            if (listCategoryEnum != null) {
                //日志记录
                recordTimeOutLog(listCategoryEnum.getId(), itemValueArrays[2], itemValueArrays[3].split("-"));
                //todo 更新清单
            }
        }

    }

    /**
     * 记录超时日志
     *
     * @param overTimeType 超时类别
     * @param idArrays     id数组
     * @param departmentId 主责部门id
     * @author zhangxiaogang
     * @since 2019/10/8 18:37
     */
    private void recordTimeOutLog(Long departmentId, String overTimeType, String... idArrays) {
        IplTimeOutLog iplTimeOutLog = new IplTimeOutLog();
        iplTimeOutLog.setMainId(Long.valueOf(idArrays[0]));
        Long aLong = Long.valueOf(idArrays[1]);
        //主责
        if (aLong.intValue() == 0) {
            iplTimeOutLog.setUnitCategory(UnitCategoryEnum.MAIN.getId());
            iplTimeOutLog.setDepartmentId(departmentId);
            //协同
        } else {
            iplTimeOutLog.setUnitCategory(UnitCategoryEnum.COORDINATION.getId());
            iplTimeOutLog.setDepartmentId(aLong);
        }
        iplTimeOutLog.setTimeType(overTimeType);
        iplTimeOutLogService.save(iplTimeOutLog);
    }
}
