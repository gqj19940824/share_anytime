package com.unity.innovation.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.constant.RedisConstants;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.XyDates;
import com.unity.common.utils.HashRedisUtils;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.entity.generated.IplDarbMainSnapshot;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        //System.out.println(calendar.getTimeInMillis());
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

    /**
    * 根据用户id返回用户名
    *
    * @param userId 用户id
    * @return java.lang.String
    * @author JH
    * @date 2019/9/24 15:01
    */
    public static String getUserNameById(Long userId) {
        return  innovationUtil.hashRedisUtils.getFieldValueByFieldName(RedisConstants.USER.concat(userId.toString()), RedisConstants.NAME);
    }

     /**
     * 根据单位id集合从redis中获取单位集合
     *
     * @param ids 单位集合
     * @return java.util.List<com.unity.common.client.vo.DepartmentVO>
     * @author JH
     * @date 2019/9/24 13:58
     */
    public static List<DepartmentVO> getDepartmentListByIds(List<Long> ids) {
        List<DepartmentVO> list = Lists.newArrayList();
        ids.forEach(id ->{
            DepartmentVO department = new DepartmentVO();
            department.setId(id);
            department.setName(getDeptNameById(id));
            list.add(department);
        });
        return list;
    }

    /**
     * 功能描述 邮箱校验
     *
     * @param email 邮箱字符串
     * @return boolean 是否为邮箱 true 是 false 不是
     * @author gengzhiqiang
     * @date 2019/9/24 16:30
     */
    public static boolean isEmail(String email) {
        String com = ".com";
        String cn = ".cn";
        String net = ".net";
        if (StringUtils.isBlank(email)) {
            return false;
        }
        if (!(email.endsWith(com) || email.endsWith(cn) || email.endsWith(net))) {
            return false;
        }
        String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(REGEX_EMAIL);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 两个对象中相同的属性值复制
     * @param source
     * @param dest
     * @throws Exception
     */
    public static <T> T Copy(Object source, T dest) {
        try{
            //获取属性
            BeanInfo sourceBean = Introspector.getBeanInfo(source.getClass(), java.lang.Object.class);
            PropertyDescriptor[] sourceProperty = sourceBean.getPropertyDescriptors();

            BeanInfo destBean = Introspector.getBeanInfo(dest.getClass(), java.lang.Object.class);
            PropertyDescriptor[] destProperty = destBean.getPropertyDescriptors();


            for(int i=0;i<sourceProperty.length;i++){

                for(int j=0;j<destProperty.length;j++){

                    if(sourceProperty[i].getName().equals(destProperty[j].getName())){
                        //调用source的getter方法和dest的setter方法
                        destProperty[j].getWriteMethod().invoke(dest, sourceProperty[i].getReadMethod().invoke(source));
                        break;
                    }
                }
            }
        }catch(Exception e){
            log.error("属性复制失败:"+e.getMessage());
        }
        return dest;
    }



}
