package com.unity.common.utils;

import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by ${Jiaww} on 2017/11/10.
 */
public class DateUtil
{

    private static String pattern="yyyy-MM-dd";
    private static SimpleDateFormat formatter = new SimpleDateFormat(pattern);
    private static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
    public static DateTimeFormatter getDateTimeFormatter(){
        return dateFormatter;
    }

    /**
     * 功能描述 返回年月日
     * @param date yyyy-MM-dd
     * @return java.lang.String yyyy年MM月dd日
     * @author gengzhiqiang
     * @date 2019/10/31 10:19
     */
    public static String getYearMonthDay(String date) throws Exception{
        Date parse;
        if (date == null){
            parse = new Date();
        }else {
             parse = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);
        String str = new SimpleDateFormat(("yyyy年MM月dd日")).format(calendar.getTime());
        return str;
    }

    /**
     * 功能描述 返回年月
     * @param date yyyy-MM
     * @return java.lang.String yyyy年MM月
     * @author gengzhiqiang
     * @date 2019/10/31 10:19
     */
    public static String getYearMonth(String date) throws Exception{
        Date parse;
        if (date == null){
            parse = new Date();
        }else {
            parse = new SimpleDateFormat("yyyy-MM").parse(date);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);
        String str = new SimpleDateFormat(("yyyy年MM日")).format(calendar.getTime());
        return str;
    }

    /**
     * 获得最近6个月的名称列表
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 2:37 下午
     */
    public static List<String> getMonthsList(String date) throws Exception{
        Date parse;
        if (date == null){
            parse = new Date();
        }else {
           parse = new SimpleDateFormat("yyyy-MM").parse(date);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);
        List<String> dataList = new ArrayList<>();
        dataList.add(new SimpleDateFormat(("yyyy年MM月")).format(calendar.getTime()));
        for (int i=0; i<5; i++){
            calendar.add(Calendar.MONTH, -1);
            System.out.println();
            dataList.add(new SimpleDateFormat(("yyyy年MM月")).format(calendar.getTime()));
        }
        Collections.reverse(dataList);
        return dataList;
    }

    /**
     * 获取指定截止时间前指定个数的指定格式时间列表
     *
     * @param  date 指定截止时间
     * @param  pattern 指定格式
     * @param  num 指定个数
     * @return 指定格式时间列表
     * @author gengjiajia
     * @since 2019/10/30 14:28
     */
    public static List<String> getMonthsList(String date,String pattern,int num){
        Date parse;
        if (StringUtils.isEmpty(date)){
            parse = new Date();
        }else {
            try {
                parse = new SimpleDateFormat("yyyy-MM").parse(date);
            } catch (ParseException e) {
                parse = new Date();
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);
        List<String> dataList = new ArrayList<>();
        dataList.add(new SimpleDateFormat((pattern)).format(calendar.getTime()));
        for (int i = 0; i < num; i++){
            calendar.add(Calendar.MONTH, -1);
            dataList.add(new SimpleDateFormat((pattern)).format(calendar.getTime()));
        }
        Collections.reverse(dataList);
        return dataList;
    }

    /**
     * 获取指定月份前第某个月
     * @param date 指定月份
     * @param pattern 要获取的月份格式
     * @param num 要获取指定月份的第几个月
     * @return 前第某个月
     * @author gengjiajia
     * @since 2019/10/30 14:28
     */
    public static String getMonthsBySpecifiedMonthFirstFew(String date,String pattern,int num){
        Date parse;
        if (StringUtils.isNotEmpty(date)){
            try {
                parse = new SimpleDateFormat("yyyy-MM").parse(date);
            } catch (ParseException e) {
                parse = new Date();
            }
        }else {
            parse = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);
        calendar.add(Calendar.MONTH, -num);
        return new SimpleDateFormat((pattern)).format(calendar.getTime());
    }

    /**
     * 获得两个日期相差的天数（绝对值）
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-07-30 21:19
     */
    public static Long getBetweenDays(Date date1, Date date2){
        return Math.abs((date1.getTime() - date2.getTime())/86400000);
    }

    /**
     * 获得两个日期相差的天数（绝对值）
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-07-30 21:19
     */
    public static Long getBetweenDays(Long date1, Long date2){
        return Math.abs((date1 - date2)/86400000);
    }

    /**
     * 将string时间型数据转为long
     * @param time 传入的时间
     * @return java.lang.Long 返回long型数据
     * @author lifeihong
     * @date 2019/7/4 11:17
     */
    public static Long parseTimeToLong(String time) {
        try {
            return formatter2.parse(time).getTime();
        } catch (ParseException e) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("时间格式不正确").build();
        }
    }
    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date());
        Date currentTime_2 = null;
        try {
            currentTime_2 = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentTime_2;
    }

    /**
     * 获取现在日期
     *
     * @return返回短时间格式 yyyy-MM-dd
     */
    public static Date getNowDateShort(){
        String dateString = formatter.format(new Date());
        Date currentTime_2 = null;
        try {
            currentTime_2 = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentTime_2;
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(new Date());
        return dateString;
    }
    /**
     * 获取现在时间
     * @return返回字符串格式 yyyyMMddHHmmss
     */
    public static String getStringAllDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateString = formatter.format(new Date());
        return dateString;
    }
    /**
     * 获取现在日期
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort() {
        String dateString = formatter.format( new Date());
        return dateString;
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getTimeShort() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateString = formatter.format(new Date());
        return dateString;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @param
     * @return
     */
    public static String dateToStr(Date dateDate) {
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String dateToStr(java.time.LocalDate dateDate) {
        String dateString = dateFormatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Timestamp strToDateSql(String strDate) {
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter2.parse(strDate, pos);
        return new Timestamp(strtodate.getTime());
    }

    //生成时间戳 8
    public static String getDateStamp(){
        return getTimestamp().substring(0, 8);
    }

    //生成时间戳
    public  static String getTimestamp(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss" );
        String timestamp = formatter.format( date );
        return timestamp;
    }


    public  static String formationDate(Date date) {
        String dateString = "";
        // 获取系统当前时间
        Date now = new Date();
        try {
            long endTime = now.getTime();
            long currentTime= date.getTime();
            // 计算两个时间点相差的秒数
            long seconds = (endTime - currentTime);
            if (seconds<10*1000) {
                dateString ="刚刚";
            }else if (seconds<60*1000) {
                dateString = seconds/1000+"秒前";
            }else if (seconds<60*60*1000) {
                dateString = seconds/1000/60+"分钟前";
            }else if (seconds<60*60*24*1000) {
                dateString = seconds/1000/60/60+"小时前";
            }else if (seconds<60*60*24*1000*30L) {
                dateString =seconds/1000/60/60/24+ "天前";
            }else if (date.getYear()==now.getYear()) {//今年并且大于30天显示具体月日
                dateString = new SimpleDateFormat("MM-dd").format(date.getTime());
            }else if (date.getYear()!=now.getYear()) {//大于今年显示年月日
                dateString =  new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
            }else{
                dateString =  new SimpleDateFormat("yyyy-MM-dd").format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateString;

    }


     public static String transferLongToDate(Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /**
     * 时间戳（毫秒）转星期
     *
     * @param  timestamp 时间戳
     * @return 星期
     * @author gengjiajia
     * @since 2019/05/22 16:47
     */
    public static int timestampToWeek(Long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        return c.get(Calendar.DAY_OF_WEEK)-1;
    }

    public static String timestampToWeekStr(Long timestamp) {
        int i = timestampToWeek(timestamp);
        switch (i) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 0:
                return "周日";
            default:
                return "";
        }
    }


    //日期转星期
    public static String dayForWeek(Date tmpDate) {
        Calendar cal = Calendar.getInstance();
        String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        try {
            cal.setTime(tmpDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long s,String strDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDate);
        Date date = new Date(s);
        return simpleDateFormat.format(date);
    }

    // 根据时间戳获取下一天的时间戳
    public static Map<String,Long> getNextDayByStamp(long s){
        Map<String,Long> nextDay = new HashMap<>();
        try {
            String nextDayBegin = "";
            String nextDayEnd = "";
            String ymd = getLastDay(stampToDate(s, "yyyy-MM-dd"));
            nextDayBegin = ymd +  " 00:00:00";
            nextDayEnd = ymd  +  " 23:59:59";
            nextDay.put("nextDayBegin",strToDateLong(nextDayBegin).getTime());
            nextDay.put("nextDayEnd",strToDateLong(nextDayEnd).getTime());
            return nextDay;
        }catch (Exception e){
            return null;
        }
    }


    //获取前一天
    public static String getLastDay(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        //                      此处修改为+1则是获取后一天
        calendar.set(Calendar.DATE, day + 1);

        String lastDay = sdf.format(calendar.getTime());
        return lastDay;
    }
    //通过年份获取本年至下一年区间
    public static Map<String,Long> getYearInterval(String year) {
        Map<String,Long> nextDay = new HashMap<>();
        Pattern pattern = Pattern.compile("[0-9]*");
        if( StringUtils.isEmpty(year) || !(pattern.matcher(year).matches())){
            Calendar date = Calendar.getInstance();
            year = String.valueOf(date.get(Calendar.YEAR));
        }
        nextDay.put("beginYear",strToDateLong(year +  "-01-01 00:00:00").getTime());
        nextDay.put("endYear",strToDateLong((Integer.valueOf(year) + 1)  +  "-01-01 00:00:00").getTime());
        return nextDay;
    }
    /**
     * 功能描述 将时间转换为时间戳
     * @param str 2018-09-22
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/7/7 11:26
     */
    public static Long dateToStamp(String str) {
        //设置时间格式，将该时间格式的时间转换为时间戳
        Long time = 0L;
          String pattern="yyyy-MM";
          SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(str);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 功能描述 将时间转换为时间戳
     * @param str 2018-09-22
     * @return java.lang.Long
     * @author gengzhiqiang
     * @date 2019/7/7 11:26
     */
    public static Long dateToLongStamp(String str) {
        //设置时间格式，将该时间格式的时间转换为时间戳
        Long time = 0L;
        String pattern="yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(str);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
