package com.unity.rbac.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 生成编号的util
 */
@Component
@Order
public class NoUtil implements CommandLineRunner {

    private static final AtomicLong FINAL_NUMBER = new AtomicLong(1000);
    private static final DecimalFormat TEXT_FORMAT = new DecimalFormat("0000");
    private static final String PREFIX = "";
    private static Date CURRENT_DATE;

    /**
     * {@link CommandLineRunner#run(String...)}
     *
     * @param args 启动参数
     * @throws Exception 如果失败则抛出异常
     * @author Jung
     * @since 2018年08月26日17:02:19
     */
    @Override
    public void run(String... args) throws Exception {
        /*EnterpriseWhistleDao bean = SpringUtils.getBean(EnterpriseWhistleDao.class);
        EnterpriseWhistle lastNewEnterpriseWhistleInfo = bean.getLastNewEnterpriseWhistleInfo();
        if (lastNewEnterpriseWhistleInfo == null || StringUtils.isEmpty(lastNewEnterpriseWhistleInfo.getSNo())) {
            CURRENT_DATE = new Date();
            FINAL_NUMBER.set(0);
        } else {
            String currentNo = lastNewEnterpriseWhistleInfo.getSNo();
            FINAL_NUMBER.set(Long.valueOf(currentNo.substring(8)));
            CURRENT_DATE = DateUtils.parseDate(currentNo.substring(0, 8), "yyyyMMdd");
        }*/
    }

    /**
     * 生成编号的方法
     *
     * @return 生成的编号
     * @author Jung
     * @since 2018年08月26日17:00:37
     */
    public static synchronized String addAndGetNext() {
        //判断是否到下一天了
        long finalNumber;
        Date date = new Date();
        if (!DateUtils.isSameDay(CURRENT_DATE, date)) {
            finalNumber = FINAL_NUMBER.updateAndGet((e) -> 1L);
            CURRENT_DATE = date;
        } else {
            finalNumber = FINAL_NUMBER.addAndGet(1);
        }
        return PREFIX + DateFormatUtils.format(CURRENT_DATE, "yyyyMMdd") + TEXT_FORMAT.format(finalNumber);
    }

    /*public static void main(String[] args) {
        long startTimes = System.currentTimeMillis();
        Task task = new Task();
		FutureTask<Integer> futureTask = new FutureTask<>(task);
		Thread thread = new Thread(futureTask);
		thread.start();
        Task2 task2 = new Task2();
        FutureTask<Integer> futureTask2 = new FutureTask<>(task2);
        Thread thread2 = new Thread(futureTask2);
        thread2.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("主线程在执行任务");

        try {
            System.out.println("task运行结果"+futureTask.get());
            System.out.println("task2运行结果"+futureTask2.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        long endTimes = System.currentTimeMillis();
        System.out.println("所有任务执行完毕 耗时 "+(endTimes-startTimes));
    }*/

    /*public static void main(String[] args) {
        long startTimes = System.currentTimeMillis();
        System.out.println("子线程在进行计算");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        int sum = 0;
        for(int i=0;i<100;i++)
            sum += i;

        System.out.println("子线程2在进行计算");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        int sum2 = 0;
        for(int i=0;i<200;i++)
            sum2 += i;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println("主线程在执行任务");
        System.out.println("task运行结果"+sum);
        System.out.println("task2运行结果"+sum2);
        long endTimes = System.currentTimeMillis();
        System.out.println("所有任务执行完毕 耗时 "+(endTimes-startTimes));
    }*/

}