package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.DateUtil;
import com.unity.innovation.controller.vo.MultiBarVO;
import com.unity.innovation.controller.vo.PieVoByDoc;
import com.unity.innovation.entity.IplEsbMain;
import com.unity.innovation.entity.IplOdMain;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.enums.BizTypeEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.*;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * create by qinhuan at 2019/10/28 4:40 下午
 */
@RestController
@RequestMapping("/statisticsPubContentAndResults")
public class StatisticsPubContentAndResultsController extends BaseWebController {

    @Resource
    private IplDarbMainServiceImpl iplDarbMainService;
    @Resource
    private IplSatbMainServiceImpl iplSatbMainService;
    @Resource
    private IplOdMainServiceImpl iplOdMainService;
    @Resource
    private IplEsbMainServiceImpl iplEsbMainService;
    @Resource
    private IplAssistServiceImpl assistService;
    @Resource
    private IpaManageMainServiceImpl ipaManageMainService;
    @Resource
    private IplLogServiceImpl iplLogService;

    /**
     * 企业成长目标投资需求行业分布及变化-工作动态的关键字统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/dwsKewWordStatistics")
    public Mono<ResponseEntity<SystemResponse<Object>>> dwsKewWordStatistics(@RequestBody Map<String, String> map) {
        Long start = getStart(map);
        Long end = getEnd(map);

        List<PieVoByDoc.DataBean> dataBeans = ipaManageMainService.dwsKewWordStatistics(start, end, MapUtils.getLong(map, "idRbacDepartment"));

        if (CollectionUtils.isNotEmpty(dataBeans)){
            if (dataBeans.size() > 10){
                List<PieVoByDoc.DataBean> otherDateBeans = dataBeans.subList(10, dataBeans.size());
                int sum = otherDateBeans.stream().mapToInt(e -> (Integer) e.getValue()).sum();
                dataBeans = dataBeans.subList(0, 10);
                dataBeans.add(PieVoByDoc.DataBean.newInstance().name("其他").value(sum).build());
            }
            List<String> legend = dataBeans.stream().map(PieVoByDoc.DataBean::getName).collect(Collectors.toList());

            PieVoByDoc pieVoByDoc = PieVoByDoc.newInstance()
                    .legend(PieVoByDoc.LegendBean.newInstance().data(legend).build())
                    .data(dataBeans)
                    .build();

            return success(pieVoByDoc);
        }else {
            return success(null);
        }
    }

    /**
     * 企业成长目标投资需求行业分布及变化-工作动态的工作类别统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/dwsTypeStatistics")
    public Mono<ResponseEntity<SystemResponse<Object>>> dwsTypeStatistics(@RequestBody Map<String, String> map) {
        Long start = getStart(map);
        Long end = getEnd(map);

        List<PieVoByDoc.DataBean> dataBeans = ipaManageMainService.dwsTypeStatistics(start, end, MapUtils.getLong(map, "idRbacDepartment"));

        if (CollectionUtils.isNotEmpty(dataBeans)){
            if (dataBeans.size() > 10){
                List<PieVoByDoc.DataBean> otherDateBeans = dataBeans.subList(10, dataBeans.size());
                int sum = otherDateBeans.stream().mapToInt(e -> (Integer) e.getValue()).sum();
                dataBeans = dataBeans.subList(0, 10);
                dataBeans.add(PieVoByDoc.DataBean.newInstance().name("其他").value(sum).build());
            }
            List<String> legend = dataBeans.stream().map(PieVoByDoc.DataBean::getName).collect(Collectors.toList());

            PieVoByDoc pieVoByDoc = PieVoByDoc.newInstance()
                    .legend(PieVoByDoc.LegendBean.newInstance().data(legend).build())
                    .data(dataBeans)
                    .build();

            return success(pieVoByDoc);
        } else {
            return success(null);
        }
    }

    /**
     * 企业成长目标投资需求行业分布及变化-投资需求变化统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/satbDemandTrend")
    public Mono<ResponseEntity<SystemResponse<Object>>> satbDemandTrend(@RequestBody Map<String, String> map) throws Exception {
        String date = MapUtils.getString(map, "date");
        if (StringUtils.isBlank(date) || !date.matches("\\d{4}-\\d{2}")) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "请求参数缺失或者错误");
        }
        Long start = getStart(map);
        Long end = getEnd(map);
        List<String> monthsList = DateUtil.getMonthsList(date);

        List<Map<String, Object>> newAddMaps = iplSatbMainService.satbDemandTrend(start, end);
        Map<String, Double> newAddMap = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(newAddMaps)) {
            Map<String, Double> collect = newAddMaps.stream().collect(Collectors.toMap(e -> MapUtils.getString(e, "month"), e -> MapUtils.getDouble(e, "sum")));
            monthsList.forEach(e -> {
                Double o = collect.get(e);
                newAddMap.put(e, o == null ? 0 : o);
            });
        }

        List<Map<String, Object>> doneMaps = iplLogService.statisticsMonthlyDemandCompletionNum(start, end, BizTypeEnum.CITY.getType());
        Map<String, Double> doneMap = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(doneMaps)) {
            Map<String, Double> collect = doneMaps.stream().collect(Collectors.toMap(e -> MapUtils.getString(e, "month"), e -> MapUtils.getDouble(e, "num")));
            monthsList.forEach(e -> {
                Double o = collect.get(e);
                doneMap.put(e, o == null ? 0 : o);
            });
        }

        Collection<Double> newAddValues = newAddMap.values();
        Collection<Double> doneMapValues = doneMap.values();
        Double newAddSum = newAddValues.stream().mapToDouble(Double::valueOf).sum();
        Double doneSum = doneMapValues.stream().mapToDouble(Double::valueOf).sum();
        if (newAddSum + doneSum>0){
            MultiBarVO multiBarVO = MultiBarVO.newInstance()
                    .legend(MultiBarVO.LegendBean.newInstance().data(Arrays.asList("月度新增融资需求额", "月度融资完成额")).build())
                    .xAxis(Arrays.asList(MultiBarVO.XAxisBean.newInstance().type("category").data(monthsList).build()))
                    .series(
                            Arrays.asList(
                                    MultiBarVO.SeriesBean.newInstance().name("月度新增融资需求额").type("line").data(new ArrayList<>(newAddValues)).build(),
                                    MultiBarVO.SeriesBean.newInstance().name("月度融资完成额").type("line").data(new ArrayList<>(doneMapValues)).build()
                            )
                    )
                    .build();

            return success(multiBarVO);
        }else {
            return success(null);
        }
    }

    /**
     * 企业成长目标投资需求行业分布及变化-完成额度统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/satbDemandDone")
    public Mono<ResponseEntity<SystemResponse<Object>>> satbDemandDone(@RequestBody Map<String, String> map) {
        String date = MapUtils.getString(map, "date");
        if (StringUtils.isBlank(date) || !date.matches("\\d{4}-\\d{2}")) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "请求参数缺失或者错误");
        }
        Long start = getStart(map);
        Long end = getEnd(map);
        List<PieVoByDoc.DataBean> dataBeans = iplLogService.satbDemandDone(start, end, BizTypeEnum.GROW.getType());

        if (CollectionUtils.isNotEmpty(dataBeans)) {
            List<String> legend = dataBeans.stream().map(PieVoByDoc.DataBean::getName).collect(Collectors.toList());
            PieVoByDoc pieVoByDoc = PieVoByDoc.newInstance()
                    .legend(PieVoByDoc.LegendBean.newInstance().data(legend).build())
                    .data(dataBeans).build();
            return success(pieVoByDoc);
        } else {
            return success(null);
        }
    }

    /**
     * 企业成长目标投资需求行业分布及变化-累计资金缺口统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/satbCashFlowGapToDate")
    public Mono<ResponseEntity<SystemResponse<Object>>> satbCashFlowGapToDate(@RequestBody Map<String, String> map) {
        String date = MapUtils.getString(map, "date");
        if (StringUtils.isBlank(date) || !date.matches("\\d{4}-\\d{2}")) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "请求参数缺失或者错误");
        }
        Long start = 0L;
        Long end = getEnd(map);

        List<PieVoByDoc.DataBean> dataBeansDone = iplLogService.satbDemandDone(start, end, BizTypeEnum.GROW.getType());
        List<PieVoByDoc.DataBean> dataBeansNew = iplSatbMainService.demandNew(start, end);
        Map<String, BigDecimal> collectDone = dataBeansDone.stream().collect(Collectors.toMap(PieVoByDoc.DataBean::getName, e->(BigDecimal)e.getValue()));
        List<String> legend = new ArrayList<>();
        List<PieVoByDoc.DataBean> dataBeans = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataBeansNew)){
            dataBeansNew.stream().forEach(e->{
                BigDecimal done = collectDone.get(e.getName());
                BigDecimal newAdd = (BigDecimal) e.getValue();
                legend.add(e.getName());
                dataBeans.add(PieVoByDoc.DataBean.newInstance().name(e.getName()).value(done == null? newAdd : newAdd.subtract(done)).build());
            });
        }

        PieVoByDoc build = PieVoByDoc.newInstance().legend(PieVoByDoc.LegendBean.newInstance().data(legend).build()).data(dataBeans).build();
        return success(build);
    }

    /**
     * 企业成长目标投资需求行业分布及变化-累计完成额度统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/satbDemandDoneToDate")
    public Mono<ResponseEntity<SystemResponse<Object>>> satbDemandDoneToDate(@RequestBody Map<String, String> map) {
        String date = MapUtils.getString(map, "date");
        if (StringUtils.isBlank(date) || !date.matches("\\d{4}-\\d{2}")) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "请求参数缺失或者错误");
        }
        Long start = 0L;
        Long end = getEnd(map);
        List<PieVoByDoc.DataBean> dataBeans = iplLogService.satbDemandDone(start, end, BizTypeEnum.GROW.getType());

        List<String> legend = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataBeans)) {
            legend = dataBeans.stream().map(PieVoByDoc.DataBean::getName).collect(Collectors.toList());
        }

        PieVoByDoc pieVoByDoc = PieVoByDoc.newInstance()
                .legend(PieVoByDoc.LegendBean.newInstance().data(legend).build())
                .data(dataBeans).build();
        return success(pieVoByDoc);
    }

    /**
     * 企业成长目标投资需求行业分布及变化-新增需求分类统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/satbDemandNewCatagory")
    public Mono<ResponseEntity<SystemResponse<Object>>> satbDemandNewCatagory(@RequestBody Map<String, String> map) {
        String date = MapUtils.getString(map, "date");
        if (StringUtils.isBlank(date) || !date.matches("\\d{4}-\\d{2}")) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "请求参数缺失或者错误");
        }
        Long start = getStart(map);
        Long end = getEnd(map);
        Map<String, Double> dataBeans = iplSatbMainService.demandNewCatagory(start, end);
        List<String> legend = Arrays.asList("银行", "债券", "自筹");
        PieVoByDoc pieVoByDoc = PieVoByDoc.newInstance()
                .legend(PieVoByDoc.LegendBean.newInstance().data(legend).build())
                .data(
                        Arrays.asList(
                                PieVoByDoc.DataBean.newInstance().name("银行").value(dataBeans == null?0:dataBeans.get("bank")).build(),
                                PieVoByDoc.DataBean.newInstance().name("债券").value(dataBeans == null?0:dataBeans.get("bond")).build(),
                                PieVoByDoc.DataBean.newInstance().name("自筹").value(dataBeans == null?0:dataBeans.get("raise")).build()
                        )
                ).build();
        return success(pieVoByDoc);
    }

    /**
     * 企业成长目标投资需求行业分布及变化-新增需求统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/satbDemandNew")
    public Mono<ResponseEntity<SystemResponse<Object>>> satbDemandNew(@RequestBody Map<String, String> map) {
        String date = MapUtils.getString(map, "date");
        if (StringUtils.isBlank(date) || !date.matches("\\d{4}-\\d{2}")) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "请求参数缺失或者错误");
        }
        Long start = getStart(map);
        Long end = getEnd(map);

        List<PieVoByDoc.DataBean> dataBeans = iplSatbMainService.demandNew(start, end);
        List<String> legend = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataBeans)) {
            legend = dataBeans.stream().map(PieVoByDoc.DataBean::getName).collect(Collectors.toList());
        }

        PieVoByDoc pieVoByDoc = PieVoByDoc.newInstance()
                .legend(PieVoByDoc.LegendBean.newInstance().data(legend).build())
                .data(dataBeans).build();
        return success(pieVoByDoc);
    }

    /**
     * 企业成长目标投资需求行业分布及变化-累计新增需求统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/satbDemandNewToDate")
    public Mono<ResponseEntity<SystemResponse<Object>>> satbDemandNewToDate(@RequestBody Map<String, String> map) {
        String date = MapUtils.getString(map, "date");
        if (StringUtils.isBlank(date) || !date.matches("\\d{4}-\\d{2}")) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "请求参数缺失或者错误");
        }
        Long start = 0L;
        Long end = getEnd(map);

        List<PieVoByDoc.DataBean> dataBeans = iplSatbMainService.demandNew(start, end);
        List<String> legend = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataBeans)) {
            legend = dataBeans.stream().map(PieVoByDoc.DataBean::getName).collect(Collectors.toList());
        }

        PieVoByDoc pieVoByDoc = PieVoByDoc.newInstance()
                .legend(PieVoByDoc.LegendBean.newInstance().data(legend).build())
                .data(dataBeans).build();
        return success(pieVoByDoc);
    }

    /**
     * 北京亦庄创新发布清单情况-需求数量变化统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/innovationPublishInfo/demandTrendStatistics")
    public Mono<ResponseEntity<SystemResponse<Object>>> demandTrendStatistics(@RequestBody Map<String, String> map) throws Exception {
        String date = MapUtils.getString(map, "date");
        Long startLong = null;
        Long endLong = null;
        if (StringUtils.isNotBlank(date)) {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat("yyyy-MM").parse(date));
            c.add(Calendar.MONTH, -5);
            startLong = c.getTimeInMillis();
            c.add(Calendar.MONTH, 6);
            endLong = c.getTimeInMillis();
        }

        Integer bizType = MapUtils.getInteger(map, "bizType");
        List<String> monthsList = DateUtil.getMonthsList(date);
        Map<String, Integer> enMap = new LinkedHashMap<>();
        Map<String, Integer> sfMap = new LinkedHashMap<>();
        monthsList.forEach(e -> {
            enMap.put(e, 0);
            sfMap.put(e, 0);
        });
        if (bizType == null) {
            List<String> tables = Arrays.asList("ipl_satb_main", "ipl_od_main", "ipl_darb_main", "ipl_esb_main");
            for (String tableName : tables) {
                getNum(startLong, endLong, enMap, sfMap, tableName);
            }
        } else {
            getNum(startLong, endLong, enMap, sfMap, getTableName(bizType));
        }

        Collection<Integer> enValues = enMap.values();
        Collection<Integer> sfValues = sfMap.values();
        int enSum = enValues.stream().mapToInt(Integer::intValue).sum();
        int sfSum = sfValues.stream().mapToInt(Integer::intValue).sum();
        MultiBarVO multiBarVO;
        if (enSum + sfSum > 0){
            multiBarVO = MultiBarVO.newInstance()
                    .legend(
                            MultiBarVO.LegendBean.newInstance().data(
                                    Arrays.asList("职能局代企业上报的需求", "企业自主上报的需求")
                            ).build()
                    ).xAxis(
                            Collections.singletonList(MultiBarVO.XAxisBean.newInstance()
                                    .type("category")
                                    .data(monthsList).build())
                    ).series(
                            Arrays.asList(
                                    MultiBarVO.SeriesBean.newInstance().type("bar").stack("name").name("职能局代企业上报的需求")
                                            .data(new ArrayList<>(enValues)).build()
                                    , MultiBarVO.SeriesBean.newInstance().type("bar").stack("name").name("企业自主上报的需求")
                                            .data(new ArrayList<>(sfValues)).build())
                    ).build();
            return success(multiBarVO);
        }else {
            return success(null);
        }
    }

    /**
     * 获取查询表名
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/30 9:53 上午
     */
    private String getTableName(Integer bizType) {
        String tableName;
        switch (BizTypeEnum.of(bizType)) {
            case GROW: // 科技局
                tableName = "ipl_satb_main";
                break;
            case INTELLIGENCE:
                tableName = "ipl_od_main";
                break;
            case CITY:
                tableName = "ipl_darb_main";
                break;
            case ENTERPRISE:
                tableName = "ipl_esb_main";
                break;
            default:
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("无效的清单类型").build();
        }
        return tableName;
    }

    /**
     * 获得对应月份的数量
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/30 9:52 上午
     */
    private void getNum(Long startLong, Long endLong, Map<String, Integer> enMap, Map<String, Integer> sfMap, String tableName) {
        List<Map<String, Object>> list = assistService.demandTrendStatistics(tableName, startLong, endLong);
        Map<Object, List<Map<String, Object>>> source = list.stream().collect(Collectors.groupingBy(e -> e.get("source")));
        List<Map<String, Object>> enNum = source.get(SourceEnum.ENTERPRISE.getId());
        if (CollectionUtils.isNotEmpty(enNum)){
            enNum.forEach(e -> {
                String month = MapUtils.getString(e, "month");
                Integer sum = MapUtils.getInteger(e, "sum");
                enMap.put(month, enMap.get(month) + sum);
            });
        }
        List<Map<String, Object>> sfNum = source.get(SourceEnum.SELF.getId());
        if (CollectionUtils.isNotEmpty(sfNum)){
            sfNum.forEach(e -> {
                String month = MapUtils.getString(e, "month");
                Integer sum = MapUtils.getInteger(e, "sum");
                sfMap.put(month, sfMap.get(month) + sum);
            });
        }
    }

    /**
     * 北京亦庄创新发布清单情况-需求数量统计
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/29 10:11 上午
     */
    @PostMapping("/innovationPublishInfo/demandStatistics")
    public Mono<ResponseEntity<SystemResponse<Object>>> demandStatistics(@RequestBody Map<String, String> map) {
        Long start = getStart(map);
        Long end = getEnd(map);

        // 图例数据
        List<String> data = new ArrayList<>();
        // 职能局数据
        List<Integer> selfData = new ArrayList<>();
        // 企业数据
        List<Integer> enterpriseData = new ArrayList<>();

        // 30成长目标投资（科技局）
        int satbSelfCount = iplSatbMainService.count(getIplSatbQw(SourceEnum.SELF.getId(), start, end));
        int satbEnterpriseCount = iplSatbMainService.count(getIplSatbQw(SourceEnum.ENTERPRISE.getId(), start, end));
        if (satbSelfCount + satbEnterpriseCount > 0){
            data.add(BizTypeEnum.GROW.getName());
            selfData.add(satbSelfCount);
            enterpriseData.add(satbEnterpriseCount);
        }

        // 40高端才智需求（组织部）
        int odSelfCount = iplOdMainService.count(getIplOdQw(SourceEnum.SELF.getId(), start, end));
        int odEnterpriseCount = iplOdMainService.count(getIplOdQw(SourceEnum.ENTERPRISE.getId(), start, end));
        if (odSelfCount + odEnterpriseCount > 0){
            data.add(BizTypeEnum.INTELLIGENCE.getName());
            selfData.add(odSelfCount);
            enterpriseData.add(odEnterpriseCount);
        }

        // 10城市创新合作(发改局)
        int darbSelfCount = iplDarbMainService.count(getIplDarbQw(SourceEnum.SELF.getId(), start, end));
        int darbEnterpriseCount = iplDarbMainService.count(getIplDarbQw(SourceEnum.ENTERPRISE.getId(), start, end));
        if (darbSelfCount + darbEnterpriseCount > 0){
            data.add(BizTypeEnum.CITY.getName());
            selfData.add(darbSelfCount);
            enterpriseData.add(darbEnterpriseCount);
        }

        // 20企业创新发展（企服局）
        int esbSelfCount = iplEsbMainService.count(getIplEsbQw(SourceEnum.SELF.getId(), start, end));
        int esbEnterpriseCount = iplEsbMainService.count(getIplEsbQw(SourceEnum.ENTERPRISE.getId(), start, end));
        if (esbSelfCount + esbEnterpriseCount > 0){
            data.add(BizTypeEnum.ENTERPRISE.getName());
            selfData.add(esbSelfCount);
            enterpriseData.add(esbEnterpriseCount);
        }
        
        int total = satbSelfCount + satbEnterpriseCount + odSelfCount + odEnterpriseCount + darbSelfCount + darbEnterpriseCount + esbSelfCount + esbEnterpriseCount;
        if (total == 0){
            return success(null);
        }else {
            MultiBarVO multiBarVO = MultiBarVO.newInstance()
                    .legend(
                            MultiBarVO.LegendBean.newInstance().data(
                                    Arrays.asList("职能局代企业上报的需求", "企业自主上报的需求")
                            ).build()
                    ).xAxis(
                            Collections.singletonList(MultiBarVO.XAxisBean.newInstance()
                                    .type("category")
                                    .data(data).build())
                    ).series(
                            Arrays.asList(
                                    MultiBarVO.SeriesBean.newInstance().type("bar").stack("name").name("职能局代企业上报的需求")
                                            .data(selfData).build()
                                    , MultiBarVO.SeriesBean.newInstance().type("bar").stack("name").name("企业自主上报的需求")
                                            .data(enterpriseData).build())
                    ).build();

            return success(multiBarVO);
        }
    }

    private Long getStart(@RequestBody Map<String, String> map) {
        String startDate = MapUtils.getString(map, "startDate");
        if (StringUtils.isNotBlank(startDate)) {
            return InnovationUtil.getFirstTimeInMonth(startDate, true);
        }
        return null;
    }

    private Long getEnd(@RequestBody Map<String, String> map) {
        String endDate = MapUtils.getString(map, "endDate");
        if (StringUtils.isNotBlank(endDate)) {
            return InnovationUtil.getFirstTimeInMonth(endDate, false);
        }
        return null;
    }

    /**
     * 如下四个分别是获取对应查询条件
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/30 9:51 上午
     */
    private LambdaQueryWrapper<IplDarbMain> getIplDarbQw(Integer source, Long start, Long end) {

        LambdaQueryWrapper<IplDarbMain> qw = new LambdaQueryWrapper<>();
        qw.eq(IplDarbMain::getSource, source);
        if (start != null) {
            qw.ge(IplDarbMain::getGmtCreate, start);
        }
        if (end != null) {
            qw.le(IplDarbMain::getGmtCreate, end);
        }
        return qw;
    }

    private LambdaQueryWrapper<IplEsbMain> getIplEsbQw(Integer source, Long start, Long end) {

        LambdaQueryWrapper<IplEsbMain> iplDarbQw = new LambdaQueryWrapper<>();
        iplDarbQw.eq(IplEsbMain::getSource, source);
        if (start != null) {
            iplDarbQw.ge(IplEsbMain::getGmtCreate, start);
        }
        if (end != null) {
            iplDarbQw.le(IplEsbMain::getGmtCreate, end);
        }
        return iplDarbQw;
    }

    private LambdaQueryWrapper<IplOdMain> getIplOdQw(Integer source, Long start, Long end) {

        LambdaQueryWrapper<IplOdMain> iplDarbQw = new LambdaQueryWrapper<>();
        iplDarbQw.eq(IplOdMain::getSource, source);
        if (start != null) {
            iplDarbQw.ge(IplOdMain::getGmtCreate, start);
        }
        if (end != null) {
            iplDarbQw.le(IplOdMain::getGmtCreate, end);
        }
        return iplDarbQw;
    }

    private LambdaQueryWrapper<IplSatbMain> getIplSatbQw(Integer source, Long start, Long end) {

        LambdaQueryWrapper<IplSatbMain> qw = new LambdaQueryWrapper<>();
        qw.eq(IplSatbMain::getSource, source);
        if (start != null) {
            qw.ge(IplSatbMain::getGmtCreate, start);
        }
        if (end != null) {
            qw.le(IplSatbMain::getGmtCreate, end);
        }
        return qw;
    }
}