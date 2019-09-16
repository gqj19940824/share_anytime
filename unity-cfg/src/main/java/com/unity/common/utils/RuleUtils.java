package com.unity.common.utils;

import com.google.common.collect.Lists;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.Operate;
import com.unity.common.ui.Rule;
import com.unity.common.ui.SearchCondition;
import java.util.Calendar;


/**
 * @ClassName RuleUtils
 * @Description Rule工具类
 * @Author JH
 * @Date 2019/7/1 19:45
 */
public class RuleUtils {

    /**
     * @Desciption 判段rule是否为空,以及是否有值
     * @param rule
     * @return  rule的值 或者""
     */
    public static String getData(Rule rule) {
         if(rule != null && rule.getData()!=null ) {
            return rule.getData().toString();
         }else {
             return "";
         }
    }

    /**
     * @Description  通过rule名称 移除某个条件
     * @param cond  分页条件
     * @param ruleName  rule名称
     *
     */
    public static void removeRule(SearchCondition cond ,String ruleName) {
        cond.excludeSpecialState(Lists.newArrayList(ruleName));
    }


    /***
     * @Description 传入需要查找的年份,返回相应的时间戳条件
     * @param cond
     * @param paramName
     */
    public static void addGmtModifiedRules(SearchCondition cond,String paramName) {
        if(cond == null){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("cond 不能为空").build();
        }
        String gmtModifiedRuleData = getData(cond.findRuleOne(paramName));
        removeRule(cond,paramName);
        if(!"".equals(gmtModifiedRuleData)) {
            int year = Integer.parseInt(gmtModifiedRuleData);
            Calendar c = Calendar.getInstance();
            c.set(year,0,1,0,0,0);
            cond.addRule(paramName, Operate.ge, c.getTimeInMillis());
            c.set(year+1,0,1,0,0,0);
            cond.addRule(paramName, Operate.lt, c.getTimeInMillis());

        }

    }

    public static void main(String[] args) {
        int year = Integer.parseInt("2019");
        Calendar c = Calendar.getInstance();
        c.set(year,0,1,0,0,0);
        System.out.println(c.getTime());
        c.set(year+1,0,1,0,0,0);
        System.out.println(c.getTime());
    }

}
