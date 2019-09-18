package com.unity.innovation.util;

import com.unity.common.constant.RedisConstants;
import com.unity.common.util.XyDates;
import com.unity.common.utils.HashRedisUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Year;
import java.util.Calendar;

/**
 * @author zhqgeng
 * @create 2019-09-17 11:08
 */
@Component
@Slf4j
@Data
public class InnovationUtil {

    @Resource
    private  HashRedisUtils hashRedisUtils;

    private static InnovationUtil innovationUtil;

    @PostConstruct
    public void init() {
        innovationUtil = this;
        innovationUtil.hashRedisUtils = this.hashRedisUtils;
    }

    /**
     * 功能描述
     * @param time 月份查询条件  2019-6
     * @param flag   月初：true  月末：false
     * @return long 返回时间戳
     * @author gengzhiqiang
     * @date 2019/9/17 13:54
     */
    public static long getFirstTimeInMonth(String time,boolean flag) {
        String[] sp = time.split("-");
        int year = Integer.parseInt(sp[0]);
        int month = Integer.parseInt(sp[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        if (flag) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        } else {
            int maxDay = XyDates.getMaxDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1);
            calendar.set(Calendar.DAY_OF_MONTH, maxDay);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        }
        System.out.println(calendar.getTimeInMillis());
        return calendar.getTimeInMillis();
    }

    /**
    * 根据单位id返回单位名称
    *
    * @param deptId 单位id
    * @return 单位名称
    * @author JH
    * @date 2019/9/18 14:13
    */
    public static String getDeptNameById(Long deptId) {
        return  innovationUtil.hashRedisUtils.getFieldValueByFieldName(RedisConstants.DEPARTMENT.concat(deptId.toString()), RedisConstants.NAME);
    }

}
