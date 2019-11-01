package com.unity.innovation.util;

import com.google.common.collect.Lists;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.RedisConstants;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.XyDates;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.HashRedisUtils;
import com.unity.common.constant.ParamConstants;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Calendar;
import java.util.HashMap;
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
    @Resource
    private DicUtils dicUtils2;

    private static DicUtils dicUtils;
    private static InnovationUtil innovationUtil;

    @PostConstruct
    public void init() {
        innovationUtil = this;
        innovationUtil.hashRedisUtils = this.hashRedisUtils;
        dicUtils = dicUtils2;
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
     * 功能描述 获取一天最开始的时间
     * @param beginTime 2019-05-01
     * @return long 时间戳
     * @author gengzhiqiang
     * @date 2019/10/28 20:13
     */
    public static long getFirstTimeInDay(String beginTime) {
        if (StringUtils.isBlank(beginTime)){
            return 0L;
        }
        String[] sp = beginTime.split("-");
        int year = Integer.parseInt(sp[0]);
        int month = Integer.parseInt(sp[1]);
        int day = Integer.parseInt(sp[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, day);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 功能描述 获取一天最后的时间
     * @param endTime 2019-05-01
     * @return long 时间戳
     * @author gengzhiqiang
     * @date 2019/10/28 20:13
     */
    public static long getLastTimeInDay(String endTime) {
        if (StringUtils.isBlank(endTime)){
            return ParamConstants.GMT_SUBMIT;
        }
        String[] sp = endTime.split("-");
        int year = Integer.parseInt(sp[0]);
        int month = Integer.parseInt(sp[1]);
        int day = Integer.parseInt(sp[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, day+1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    /**
    * 返回 2019-05-01：00：00：00  、2019-10-31： 23：59：59：999 两个时间戳
    *
    * @param beginTime 2019-05
     * @param endTime 2019-10
    * @return java.util.Map<java.lang.String,java.lang.Long>
    * @author JH
    * @date 2019/10/28 16:16
    */
    public static Map<String,Long> getTime(String beginTime, String endTime) {
        Map<String,Long> res = new HashMap<>();
        String[] begin = beginTime.split("-");
        int beginYear = Integer.parseInt(begin[0]);
        int beginmonth = Integer.parseInt(begin[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,beginYear);
        calendar.set(Calendar.MONTH,beginmonth-1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        res.put("beginTime",calendar.getTimeInMillis());

        String[] end = endTime.split("-");
        int endYear = Integer.parseInt(end[0]);
        int endMonth = Integer.parseInt(end[1]);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,endYear);
        calendar.set(Calendar.MONTH,endMonth-1);
        int maxDay = XyDates.getMaxDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1);
        calendar.set(Calendar.DAY_OF_MONTH, maxDay);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        res.put("endTime",calendar.getTimeInMillis());
        return res;
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
        if (deptId == null){
            return null;
        }
        return  innovationUtil.hashRedisUtils.getFieldValueByFieldName(RedisConstants.DEPARTMENT + deptId, RedisConstants.NAME);
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

    /**
     * 校验当前用户是否有操作传入业务的权限
     *
     * @param  bizType  BizEnum中的枚举
     * @return
     * @author qinhuan
     * @since 2019/10/25 9:20 上午
     */
    public static void check(Integer bizType){
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (!customer.getTypeRangeList().contains(bizType)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("当前账号的单位不可操作数据").build();
        }
    }

    /**
     * 根据业务类型查找主责单位
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/28 10:17 上午
     */
    public static Long getIdRbacDepartmentDuty(Integer bizType){
        String dicValueByCode = dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, bizType + "");
        if (StringUtils.isBlank(dicValueByCode)){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR).message("无法获得业务对应的处理单位").build();
        }else {
            return Long.parseLong(dicValueByCode);
        }
    }

    /**
     * 功能描述 double取整
     *
     * @param number doubles数据
     * @return int 取整数据
     * @author gengzhiqiang
     * @date 2019/9/24 16:30
     */
    public static int ceil(Double number) {
        if (number == null || number == 0) {
            return 0;
        } else {
            return (int) Math.ceil(number);
        }
    }


}
