package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import com.unity.common.utils.DateUtil;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.controller.vo.CylinderVo;
import com.unity.innovation.controller.vo.MultiBarVO;
import com.unity.innovation.controller.vo.PieVo;
import com.unity.innovation.dao.StatisticsPublishWorkDao;
import com.unity.innovation.entity.*;
import com.unity.innovation.entity.POJO.Statistics;
import com.unity.innovation.entity.POJO.StatisticsChange;
import com.unity.innovation.entity.POJO.StatisticsSearch;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.BizTypeEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

/**
 * @author zhqgeng
 * 生成日期 2019-10-28 14:48
 */
@Service
public class StatisticsPublishWorkService {

    @Resource
    private IplTimeOutLogServiceImpl iplTimeOutLogService;

    @Resource
    private IpaManageMainServiceImpl ipaManageMainService;

    @Resource
    private StatisticsPublishWorkDao dao;

    @Resource
    private DicUtils dicUtils;

    /**
     * 功能描述  时间期间内 各单位工作基本情况统计 平均首次响应时间 和 平均完成时间
     *
     * @param search 查询条件
     * @return com.unity.innovation.controller.vo.MultiBarVO 实体
     * @author gengzhiqiang
     * @date 2019/10/29 11:09
     */
    public MultiBarVO baseStatistics(StatisticsSearch search) throws Exception {
        String beginStr = DateUtil.getYearMonthDay(search.getBeginTime());
        String endStr = DateUtil.getYearMonthDay(search.getEndTime());
        String title=beginStr+"-"+endStr;
        Long beginTime = InnovationUtil.getFirstTimeInDay(search.getBeginTime());
        Long endTime = InnovationUtil.getLastTimeInDay(search.getEndTime());
        //科技局
        int satbFirst = InnovationUtil.ceil(dao.satbFirst(beginTime, endTime));
        int satbFinish = InnovationUtil.ceil(dao.satbFinish(beginTime, endTime));
        //企服局
        int esbFirst = InnovationUtil.ceil(dao.esbFirst(beginTime, endTime));
        int esbFinish = InnovationUtil.ceil(dao.esbFinish(beginTime, endTime));
        //发改局
        int darbFirst = InnovationUtil.ceil(dao.darbFirst(beginTime, endTime));
        int darbFinish = InnovationUtil.ceil(dao.darbFinish(beginTime, endTime));
        //纪检组
        int sugFirst = InnovationUtil.ceil(dao.sugFirst(beginTime, endTime));
        int sugFinish = InnovationUtil.ceil(dao.sugFinish(beginTime, endTime));
        //组织部
        int odFirst = InnovationUtil.ceil(dao.odFirst(beginTime, endTime));
        int odFinish = InnovationUtil.ceil(dao.odFinish(beginTime, endTime));
        if ((satbFirst + satbFinish + esbFirst + esbFinish +
                darbFirst + darbFinish + sugFirst + sugFinish + odFirst + odFinish) == 0) {
            return null;
        }
        return MultiBarVO.newInstance().title(title)
                .legend(
                        MultiBarVO.LegendBean.newInstance().data(
                                Arrays.asList("平均首次响应时间(h)", "平均完成时间(d)")
                        ).build()
                ).xAxis(
                        Arrays.asList(MultiBarVO.XAxisBean.newInstance()
                                .type("category")
                                .axisPointer(MultiBarVO.AxisPointerBean.newInstance().type("shadow").build())
                                .data(getDeptNames()).build())
                ).series(
                        Arrays.asList(
                                MultiBarVO.SeriesBean.newInstance().type("bar").name("平均首次响应时间(h)")
                                        .data(Arrays.asList(darbFirst, esbFirst+satbFirst, odFirst, sugFirst)).build()
                                , MultiBarVO.SeriesBean.newInstance().type("bar").name("平均完成时间(d)")
                                        .data(Arrays.asList(darbFinish, esbFinish+satbFinish, odFinish, sugFinish)).build())).build();
    }

    /**
     * 功能描述 时间期间内 各单位超时次数统计（次）
     *
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/28 15:51
     */
    public MultiBarVO overDealTimes(StatisticsSearch search) throws Exception {
        String beginStr = DateUtil.getYearMonthDay(search.getBeginTime());
        String endStr = DateUtil.getYearMonthDay(search.getEndTime());
        String title=beginStr+"-"+endStr;
        //时间期间内的数据
        Long beginTime = InnovationUtil.getFirstTimeInDay(search.getBeginTime());
        Long endTime = InnovationUtil.getLastTimeInDay(search.getEndTime());
        List<IplTimeOutLog> list = iplTimeOutLogService.list(new LambdaQueryWrapper<IplTimeOutLog>()
                .gt(IplTimeOutLog::getGmtCreate, beginTime).lt(IplTimeOutLog::getGmtCreate, endTime));
        Map<String, List<IplTimeOutLog>> collect = list.stream().collect(Collectors.groupingBy(IplTimeOutLog::getBizType));
        List<Statistics> statisticsList = new ArrayList<>();
        //发改局
        statisticsList.add(Statistics.newInstance()
                .deptName(InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.CITY.getType().toString()))))
                .count(collect.get(BizTypeEnum.CITY.getType().toString()) == null ? 0 : collect.get(BizTypeEnum.CITY.getType().toString()).size())
                .build());
        //企服局和科技局 合并成一个局负责
        int a = collect.get(BizTypeEnum.ENTERPRISE.getType().toString()) == null ? 0 :
                collect.get(BizTypeEnum.ENTERPRISE.getType().toString()).size();
        int b = collect.get(BizTypeEnum.GROW.getType().toString()) == null ? 0 :
                collect.get(BizTypeEnum.GROW.getType().toString()).size();
        statisticsList.add(Statistics.newInstance()
                .deptName(InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.ENTERPRISE.getType().toString()))))
                .count(a + b)
                .build());
        //纪检组
        statisticsList.add(Statistics.newInstance()
                .deptName(InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.SUGGESTION.getType().toString()))))
                .count(collect.get(BizTypeEnum.SUGGESTION.getType().toString()) == null ? 0 : collect.get(BizTypeEnum.SUGGESTION.getType().toString()).size())
                .build());
        //组织部
        statisticsList.add(Statistics.newInstance()
                .deptName(InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.INTELLIGENCE.getType().toString()))))
                .count(collect.get(BizTypeEnum.INTELLIGENCE.getType().toString()) == null ? 0 : collect.get(BizTypeEnum.INTELLIGENCE.getType().toString()).size())
                .build());
        //按照数量排序
        statisticsList.sort(comparingInt(Statistics::getCount).reversed());
        //单位名称 数量
        List<String> name = new ArrayList<>();
        List<Integer> count = new ArrayList<>();
        statisticsList.forEach(s -> {
            name.add(s.getDeptName());
            count.add(s.getCount());
        });
        return MultiBarVO.newInstance().title(title)
                .xAxis(
                        Arrays.asList(MultiBarVO.XAxisBean.newInstance()
                                .type("value").build())
                ).yAxis(
                        Arrays.asList(MultiBarVO.YAxisBean.newInstance()
                                .type("category")
                                .data(name).build())
                ).series(
                        Arrays.asList(
                                MultiBarVO.SeriesBean.newInstance().type("bar")
                                        .data(count).build())).build();
    }

    /**
     * 功能描述  固定月份（它前六个月的数据） 六个月内某单位工作基本情况变化
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/29 14:29
     */
    public MultiBarVO changeStatistics(StatisticsSearch search) throws Exception {
        //2019-06
        String monthTime = search.getMonthTime();
        Long beginTime = null;
        Long endTime = null;
        if (StringUtils.isNotBlank(monthTime)) {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat("yyyy-MM").parse(monthTime));
            c.add(Calendar.MONTH, -6);
            beginTime = c.getTimeInMillis();
            c.add(Calendar.MONTH, 7);
            endTime = c.getTimeInMillis();
        }
        List<String> monthsList = DateUtil.getMonthsList(monthTime);
        String title = monthsList.get(0) +"-"+monthsList.get(monthsList.size() - 1);
        Map<String, Integer> first = new LinkedHashMap<>();
        Map<String, Integer> finish = new LinkedHashMap<>();
        monthsList.forEach(e -> {
            first.put(e, 0);
            finish.put(e, 0);
        });
        Integer bizType = search.getBizType();
        //定义三个数据集合
        List<String> titleData = Lists.newArrayList();
        List<Integer> firstData = Lists.newArrayList();
        List<Integer> finishData = Lists.newArrayList();
        //某个局
        if (search.getBizType() != null) {
            //20为创新科技局 原科技局和企服局两表合一
            if (BizTypeEnum.ENTERPRISE.getType().equals(bizType)) {

                List<StatisticsChange> changeFirstAll = dao.changeFirstTwo(beginTime, endTime);
                List<StatisticsChange> changeFinishAll = dao.changeFinishTwo(beginTime, endTime);
                //根据月份分组
                Map<String, List<StatisticsChange>> changeMap = changeFirstAll.stream().collect(Collectors.groupingBy(StatisticsChange::getMonth));
                Map<String, List<StatisticsChange>> finishMap = changeFinishAll.stream().collect(Collectors.groupingBy(StatisticsChange::getMonth));
                //循环月份集合
                monthsList.forEach(month -> {
                    //平均首次响应时间
                    List<StatisticsChange> list1 = changeMap.get(month);
                    if (CollectionUtils.isNotEmpty(list1)) {
                        dealFirstMonthData(list1,month,first);
                    }
                    //平均首次响应时间
                    List<StatisticsChange> list2 = finishMap.get(month);
                    if (CollectionUtils.isNotEmpty(list2)) {
                        dealFinishMonthData(list2,month,finish);
                    }
                });

            } else {
                String tableName = getTableName(bizType);
                //变化 平均首次响应时间
                List<Map<String, Object>> changeFirst = dao.changeFirst(tableName, beginTime, endTime);
                changeFirst.forEach(e -> {
                    String month = MapUtils.getString(e, "month");
                    int sum = InnovationUtil.ceil(MapUtils.getDouble(e, "sum"));
                    first.put(month, sum);
                });
                //变化 平均完成时间
                List<Map<String, Object>> changeFinish = dao.changeFinish(tableName, beginTime, endTime);
                changeFinish.forEach(e -> {
                    String month = MapUtils.getString(e, "month");
                    int sum = InnovationUtil.ceil(MapUtils.getDouble(e, "sum"));
                    finish.put(month, sum);
                });
            }
        } else {
            //全部数据
            List<StatisticsChange> changeFirstAll = dao.changeFirstAll(beginTime, endTime);
            List<StatisticsChange> changeFinishAll = dao.changeFinishAll(beginTime, endTime);
            //根据月份分组
            Map<String, List<StatisticsChange>> changeMap = changeFirstAll.stream().collect(Collectors.groupingBy(StatisticsChange::getMonth));
            Map<String, List<StatisticsChange>> finishMap = changeFinishAll.stream().collect(Collectors.groupingBy(StatisticsChange::getMonth));
            //循环月份集合
            monthsList.forEach(month -> {
                //平均首次响应时间
                List<StatisticsChange> list2 = finishMap.get(month);
                if (CollectionUtils.isNotEmpty(list2)) {
                    dealFinishMonthData(list2,month,finish);
                }
                //平均首次响应时间
                List<StatisticsChange> list1 = changeMap.get(month);
                if (CollectionUtils.isNotEmpty(list1)) {
                    dealFirstMonthData(list1,month,first);
                }
            });
        }
        //遍历集合 添加数据
        int count = 0;
        for (String month : monthsList) {
            titleData.add(month);
            firstData.add(first.get(month));
            finishData.add(finish.get(month));
            count += first.get(month);
            count += finish.get(month);
        }
        if (count == 0) {
            return null;
        }
        return MultiBarVO.newInstance().title(title)
                .legend(
                        MultiBarVO.LegendBean.newInstance().data(
                                Arrays.asList("平均首次响应时间(h)", "平均完成时间(d)")).build()
                ).xAxis(
                        Arrays.asList(MultiBarVO.XAxisBean.newInstance()
                                .type("category")
                                .axisPointer(MultiBarVO.AxisPointerBean.newInstance().type("shadow").build())
                                .data(titleData).build())
                ).series(
                        Arrays.asList(
                                MultiBarVO.SeriesBean.newInstance().type("bar").name("平均首次响应时间(h)")
                                        .data(firstData).build()
                                , MultiBarVO.SeriesBean.newInstance().type("bar").name("平均完成时间(d)")
                                        .data(finishData).build())).build();
    }

    /**
     * 功能描述 处理 首次的数据
     * @param list1 统计集合
     * @param month 月份
     * @param first 返回集合
     * @author gengzhiqiang
     * @date 2019/11/7 10:21
     */
    private void dealFirstMonthData(List<StatisticsChange> list1, String month, Map<String,Integer> first) {
        Long firsts = list1.stream().map(StatisticsChange::getFirstSum).reduce(0L, (a, b) -> a + b);
        Long creates = list1.stream().map(StatisticsChange::getCreateSum).reduce(0L, (a, b) -> a + b);
        Integer count = list1.stream().map(StatisticsChange::getCount).reduce(0, (a, b) -> a + b);
        int firstAvg = InnovationUtil.ceil((double) ((firsts - creates) / (count * InnovationConstant.HOUR)));
        first.put(month, firstAvg);
    }

    /**
     * 功能描述 处理 首次的数据
     * @param list 统计集合
     * @param month 月份
     * @param map 返回集合
     * @author gengzhiqiang
     * @date 2019/11/7 10:21
     */
    private void dealFinishMonthData(List<StatisticsChange> list, String month,Map<String, Integer> map ) {
        Long modify = list.stream().map(StatisticsChange::getModifiedSum).reduce(0L, (a, b) -> a + b);
        Long creates2 = list.stream().map(StatisticsChange::getCreateSum).reduce(0L, (a, b) -> a + b);
        Integer count2 = list.stream().map(StatisticsChange::getCount).reduce(0, (a, b) -> a + b);
        int finishAvg = InnovationUtil.ceil((double) ((modify - creates2) / (count2 * InnovationConstant.DAY)));
        map.put(month, finishAvg);
    }

    /**
     * 获取查询表名
     *
     * @author qinhuan
     * @since 2019/10/30 9:53 上午
     */
    private String getTableName(Integer bizType) {
        String tableName;
        switch (BizTypeEnum.of(bizType)) {
            case INTELLIGENCE:
                tableName = "ipl_od_main";
                break;
            case CITY:
                tableName = "ipl_darb_main";
                break;
            case SUGGESTION:
                tableName = "ipl_suggestion";
                break;
            default:
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("无效的清单类型").build();
        }
        return tableName;
    }

    /**
     * 功能描述 固定月份（它前六个月的数据） 六个月内某单位超时次数变化
     *
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/29 15:10
     */
    public CylinderVo overDealChangeTimes(StatisticsSearch search) throws Exception {
        String monthTime = search.getMonthTime();
        Long beginTime = null;
        Long endTime = null;
        if (StringUtils.isNotBlank(monthTime)) {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat("yyyy-MM").parse(monthTime));
            c.add(Calendar.MONTH, -6);
            beginTime = c.getTimeInMillis();
            c.add(Calendar.MONTH, 7);
            endTime = c.getTimeInMillis();
        }
        //定义数据集合
        List<String> titleData = Lists.newArrayList();
        List<Integer> collectData = Lists.newArrayList();
        // 根据bizType时间期间内的数据 判断某个局或者全部
        List<StatisticsChange> data;
        if (search.getBizType() != null && BizTypeEnum.ENTERPRISE.getType().equals(search.getBizType())) {
            data = dao.overDealTimesForTwo( beginTime, endTime);
        } else {
            data = dao.overDealTimes(search.getBizType(), beginTime, endTime);
        }
        Map<String, Integer> collect = data.stream().collect(Collectors.toMap(StatisticsChange::getMonth, StatisticsChange::getCount));
        //六个月
        List<String> monthsList = DateUtil.getMonthsList(monthTime);
        String title = monthsList.get(0) + "-"+monthsList.get(monthsList.size() - 1);
        //遍历集合 添加数据
        monthsList.forEach(month -> {
            titleData.add(month);
            collectData.add(collect.get(month)==null?0:collect.get(month));
        });
        return CylinderVo.newInstance()
                .title(title)
                .seriesData(collectData)
                .xAxisData(titleData)
                .build();
    }

    /**
     * 功能描述 创新发布活动贡献情况统计
     *
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/29 15:10
     */
    public MultiBarVO contribution(StatisticsSearch search) throws Exception {
        String beginStr = DateUtil.getYearMonth(search.getBeginTime());
        String endStr = DateUtil.getYearMonth(search.getEndTime());
        String title=beginStr+"-"+endStr;
        //时间期间内的数据
        Long beginTime = InnovationUtil.getFirstTimeInMonth(search.getBeginTime(), true);
        Long endTime = InnovationUtil.getFirstTimeInMonth(search.getEndTime(), false);
        List<DailyWorkStatus> dailyWorkStatusList = dao.workContribution(beginTime, endTime);
        //整理数据
        List<Statistics> workList = dealWorkContribution(dailyWorkStatusList);
        List<IplManageMain> iplManageMainList = dao.publicContribution(beginTime, endTime);
        //整理数据
        List<Statistics> publicList = dealPublicContribution(iplManageMainList);
        //两个集合加在一起 做好去重排序
        List<Long> orderIds = getOrderIds(workList, publicList);
        Map<Long, Integer> workMap = workList.stream().collect(Collectors.toMap(Statistics::getDeptId, Statistics::getCount));
        Map<Long, Integer> publicMap = publicList.stream().collect(Collectors.toMap(Statistics::getDeptId, Statistics::getCount));
        List<String> names = Lists.newArrayList();
        List<Integer> workData = Lists.newArrayList();
        List<Integer> publicData = Lists.newArrayList();
        //组装数据
        orderIds.forEach(id -> {
            names.add(InnovationUtil.getDeptNameById(id));
            workData.add(workMap.get(id) == null ? 0 : workMap.get(id));
            publicData.add(publicMap.get(id) == null ? 0 : publicMap.get(id));
        });
        int count = 0;
        for (Integer workDatum : workData) {
            count += workDatum;
        }
        for (Integer pd : publicData) {
            count += pd;
        }
        if (count == 0) {
            return null;
        }
        return MultiBarVO.newInstance().title(title)
                .legend(
                        MultiBarVO.LegendBean.newInstance().data(
                                Arrays.asList("工作动态", "创新发布清单")
                        ).build()
                ).xAxis(
                        Arrays.asList(MultiBarVO.XAxisBean.newInstance()
                                .type("category")
                                .axisPointer(MultiBarVO.AxisPointerBean.newInstance().type("shadow").build())
                                .data(names).build())
                ).series(
                        Arrays.asList(
                                MultiBarVO.SeriesBean.newInstance().type("bar").name("工作动态")
                                        .data(workData).build()
                                , MultiBarVO.SeriesBean.newInstance().type("bar").name("创新发布清单")
                                        .data(publicData).build()))
                .build();
    }

    /**
     * 功能描述  有序单位集合
     *
     * @param workList   工作动态数据
     * @param publicList 清单数据
     * @return java.util.List<java.lang.Long> 有序单位集合
     * @author gengzhiqiang
     * @date 2019/10/29 19:17
     */
    private List<Long> getOrderIds(List<Statistics> workList, List<Statistics> publicList) {
        Set<Long> set = new HashSet<>();
        //set去重
        workList.forEach(w -> set.add(w.getDeptId()));
        publicList.forEach(w -> set.add(w.getDeptId()));
        Map<Long, Integer> workMap = workList.stream().collect(Collectors.toMap(Statistics::getDeptId, Statistics::getCount));
        Map<Long, Integer> publicMap = publicList.stream().collect(Collectors.toMap(Statistics::getDeptId, Statistics::getCount));
        List<Statistics> total = Lists.newArrayList();
        //工作动态加上清单
        set.forEach((id) -> {
            int count = (workMap.get(id) == null ? 0 : workMap.get(id)) + (publicMap.get(id) == null ? 0 : publicMap.get(id));
            Statistics statistics = Statistics.newInstance()
                    .deptId(id)
                    .count(count).build();
            total.add(statistics);
        });
        //排序
        total.sort(comparingInt(Statistics::getCount).reversed());
        //取id
        return total.stream().map(Statistics::getDeptId).collect(Collectors.toList());
    }

    /**
     * 功能描述 处理工作动态集合
     *
     * @param dailyWorkStatusList 工作动态集合
     * @return java.util.List<com.unity.innovation.entity.POJO.Statistics> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/29 19:17
     */
    private List<Statistics> dealWorkContribution(List<DailyWorkStatus> dailyWorkStatusList) {
        Map<Long, List<DailyWorkStatus>> collect = dailyWorkStatusList.stream().collect(Collectors.groupingBy(DailyWorkStatus::getIdRbacDepartment));
        List<Statistics> statisticsList = new ArrayList<>();
        collect.forEach((k, v) -> {
            Statistics statistics = Statistics.newInstance().deptName(InnovationUtil.getDeptNameById(k))
                    .deptId(k)
                    .count(v.size()).build();
            statisticsList.add(statistics);
        });
        return statisticsList;
    }

    /**
     * 功能描述 处理清单集合
     *
     * @param iplManageMainList 清单集合
     * @return java.util.List<com.unity.innovation.entity.POJO.Statistics> 数据集合
     * @author gengzhiqiang
     * @date 2019/10/29 19:17
     */
    private List<Statistics> dealPublicContribution(List<IplManageMain> iplManageMainList) {
        Map<Integer, List<IplManageMain>> collect = iplManageMainList.stream().collect(Collectors.groupingBy(IplManageMain::getBizType));
        List<Statistics> statisticsList = new ArrayList<>();
        //发改局
        List<IplManageMain> city = collect.get(BizTypeEnum.CITY.getType());
        int countCity = 0;
        if (CollectionUtils.isNotEmpty(city)) {
            countCity = city.stream().map(c -> GsonUtils.parse(c.getSnapshot(), new TypeToken<List<IplDarbMain>>() {
            })).mapToInt(List::size).sum();
        }
        statisticsList.add(Statistics.newInstance()
                .deptName(InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.CITY.getType().toString()))))
                .deptId(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.CITY.getType().toString())))
                .count(countCity)
                .build());
        //企服局
        List<IplManageMain> enterprise = collect.get(BizTypeEnum.ENTERPRISE.getType());
        int countEnterprise = 0;
        if (CollectionUtils.isNotEmpty(enterprise)) {
            countEnterprise = enterprise.stream().map(c -> GsonUtils.parse(c.getSnapshot(), new TypeToken<List<IplEsbMain>>() {
            })).mapToInt(List::size).sum();
        }
        //原来科技局与企服局合并 加在一起
        int countGrow = 0;
        List<IplManageMain> grow = collect.get(BizTypeEnum.GROW.getType());
        if (CollectionUtils.isNotEmpty(grow)) {
            countGrow = grow.stream().map(c -> GsonUtils.parse(c.getSnapshot(), new TypeToken<List<IplSatbMain>>() {
            })).mapToInt(List::size).sum();
        }
        countEnterprise+=countGrow;
        statisticsList.add(Statistics.newInstance()
                .deptName(InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.ENTERPRISE.getType().toString()))))
                .deptId(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.ENTERPRISE.getType().toString())))
                .count(countEnterprise)
                .build());
        //纪检组
        List<IplManageMain> political = collect.get(BizTypeEnum.POLITICAL.getType());
        int countPolitical = 0;
        if (CollectionUtils.isNotEmpty(political)) {
            countPolitical = political.stream().map(c -> GsonUtils.parse(c.getSnapshot(), new TypeToken<List<IplSuggestion>>() {
            })).mapToInt(List::size).sum();
        }
        statisticsList.add(Statistics.newInstance()
                .deptName(InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.POLITICAL.getType().toString()))))
                .deptId(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.POLITICAL.getType().toString())))
                .count(countPolitical)
                .build());
        //组织部
        List<IplManageMain> intelligence = collect.get(BizTypeEnum.INTELLIGENCE.getType());
        int countIntelligence = 0;
        if (CollectionUtils.isNotEmpty(intelligence)) {
            countIntelligence = intelligence.stream().map(c -> GsonUtils.parse(c.getSnapshot(), new TypeToken<List<IplOdMain>>() {
            })).mapToInt(List::size).sum();
        }
        statisticsList.add(Statistics.newInstance()
                .deptName(InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.INTELLIGENCE.getType().toString()))))
                .deptId(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.INTELLIGENCE.getType().toString())))
                .count(countIntelligence)
                .build());
        statisticsList = statisticsList.stream().filter(s -> s.getCount() != YesOrNoEnum.NO.getType()).collect(Collectors.toList());
        return statisticsList;
    }

    /**
     * 功能描述 发言人发声情况统计（次）
     *
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/29 15:10
     */
    public PieVo voice(StatisticsSearch search) throws Exception {
        String beginStr = DateUtil.getYearMonth(search.getBeginTime());
        String endStr = DateUtil.getYearMonth(search.getEndTime());
        String title=beginStr+"-"+endStr;
        //时间期间内的数据
        Long beginTime = InnovationUtil.getFirstTimeInMonth(search.getBeginTime(), true);
        Long endTime = InnovationUtil.getFirstTimeInMonth(search.getEndTime(), false);
        List<IpaManageMain> list = ipaManageMainService.list(new LambdaQueryWrapper<IpaManageMain>()
                .gt(IpaManageMain::getGmtCreate, beginTime).lt(IpaManageMain::getGmtCreate, endTime).isNotNull(IpaManageMain::getLevel));
        Integer count = list.size();
        Map<Integer, Long> collect = list.stream().collect(Collectors.groupingBy(IpaManageMain::getLevel, Collectors.counting()));
        List<PieVo.SeriesBean.DataBean> dataBeanList = Lists.newArrayList();
        List<String> names = Lists.newArrayList();
        collect.forEach((k, v) -> {
            PieVo.SeriesBean.DataBean s = PieVo.SeriesBean.DataBean.newInstance()
                    .name(dicUtils.getDicValueByCode(DicConstants.IPA_LEVEL, k.toString()))
                    .value(v)
                    .build();
            names.add(dicUtils.getDicValueByCode(DicConstants.IPA_LEVEL, k.toString()));
            dataBeanList.add(s);
        });
        return PieVo.newInstance().title(title).count(count)
                .legend(
                        PieVo.LegendBean.newInstance().data(names).build()
                )
                .series(
                        Arrays.asList(PieVo.SeriesBean.newInstance().data(dataBeanList).build())
                )
                .build();
    }

    /**
     * 功能描述 获取五大局子
     *
     * @return java.util.List 集合
     * @author gengzhiqiang
     * @date 2019/10/28 19:07
     */
    private List getDeptNames() {
        //四大局子
        List<String> list = Lists.newArrayList();
        String one = InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.CITY.getType().toString())));
        String two = InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.ENTERPRISE.getType().toString())));
        String three = InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.INTELLIGENCE.getType().toString())));
        String four = InnovationUtil.getDeptNameById(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.SUGGESTION.getType().toString())));
        list.add(one);
        list.add(two);
        list.add(three);
        list.add(four);
        return list;
    }

}
