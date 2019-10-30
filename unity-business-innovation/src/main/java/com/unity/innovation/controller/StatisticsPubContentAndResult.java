package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.DateUtil;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.controller.vo.MultiBarVO;
import com.unity.innovation.entity.IplEsbMain;
import com.unity.innovation.entity.IplOdMain;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.enums.BizTypeEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.*;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * create by qinhuan at 2019/10/28 4:40 下午
 */
@RestController
@RequestMapping("/statisticsPubContentAndResult")
public class StatisticsPubContentAndResult extends BaseWebController {

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
    private MediaManagerServiceImpl mediaManagerService;
    @Resource
    private DicUtils dicUtils;
    @Resource
    private InfoDeptSatbServiceImpl infoDeptSatbService;
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    /**
     * 北京亦庄创新发布清单情况-需求趋势统计
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
            c.add(Calendar.MONTH, -6);
            startLong = c.getTimeInMillis();
            c.add(Calendar.MONTH, 7);
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

        MultiBarVO multiBarVO = MultiBarVO.newInstance()
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
                                        .data(new ArrayList<>(enMap.values())).build()
                                , MultiBarVO.SeriesBean.newInstance().type("bar").stack("name").name("企业自主上报的需求")
                                        .data(new ArrayList<>(sfMap.values())).build())).build();

        return success(multiBarVO);
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
        String tableName = "";
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
        List<Map<String, Object>> enNum = assistService.demandTrendStatistics(tableName, SourceEnum.ENTERPRISE.getId(), startLong, endLong);
        List<Map<String, Object>> sfNum = assistService.demandTrendStatistics(tableName, SourceEnum.SELF.getId(), startLong, endLong);

        enNum.forEach(e -> {
            String month = MapUtils.getString(e, "month");
            Integer sum = MapUtils.getInteger(e, "sum");
            enMap.put(month, enMap.get(month) + sum);
        });

        sfNum.forEach(e -> {
            String month = MapUtils.getString(e, "month");
            Integer sum = MapUtils.getInteger(e, "sum");
            sfMap.put(month, enMap.get(month) + sum);
        });
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
        Long start = null;
        String startDate = MapUtils.getString(map, "startDate");
        if (StringUtils.isNotBlank(startDate)) {
            start = InnovationUtil.getFirstTimeInMonth(startDate, true);
        }
        Long end = null;
        String endDate = MapUtils.getString(map, "endDate");
        if (StringUtils.isNotBlank(endDate)) {
            end = InnovationUtil.getFirstTimeInMonth(endDate, false);
        }

        // 30成长目标投资（科技局）
        int satbSelfCount = iplSatbMainService.count(getIplSatbQw(SourceEnum.SELF.getId(), start, end));
        int satbEnterpriseCount = iplSatbMainService.count(getIplSatbQw(SourceEnum.ENTERPRISE.getId(), start, end));

        // 40高端才智需求（组织部）
        int odSelfCount = iplOdMainService.count(getIplOdQw(SourceEnum.SELF.getId(), start, end));
        int odEnterpriseCount = iplOdMainService.count(getIplOdQw(SourceEnum.ENTERPRISE.getId(), start, end));

        // 10城市创新合作(发改局)
        int darbSelfCount = iplDarbMainService.count(getIplDarbQw(SourceEnum.SELF.getId(), start, end));
        int darbEnterpriseCount = iplDarbMainService.count(getIplDarbQw(SourceEnum.ENTERPRISE.getId(), start, end));

        // 20企业创新发展（企服局）
        int esbSelfCount = iplEsbMainService.count(getIplEsbQw(SourceEnum.SELF.getId(), start, end));
        int esbEnterpriseCount = iplEsbMainService.count(getIplEsbQw(SourceEnum.ENTERPRISE.getId(), start, end));

        MultiBarVO multiBarVO = MultiBarVO.newInstance()
                .legend(
                        MultiBarVO.LegendBean.newInstance().data(
                                Arrays.asList("职能局代企业上报的需求", "企业自主上报的需求")
                        ).build()
                ).xAxis(
                        Collections.singletonList(MultiBarVO.XAxisBean.newInstance()
                                .type("category")
                                .data(Arrays.asList("成长目标投资", "高端才智需求", "城市创新合作", "企业创新发展")).build())
                ).series(
                        Arrays.asList(
                                MultiBarVO.SeriesBean.newInstance().type("bar").stack("name").name("职能局代企业上报的需求")
                                        .data(Arrays.asList(satbSelfCount, odSelfCount, darbSelfCount, esbSelfCount)).build()
                                , MultiBarVO.SeriesBean.newInstance().type("bar").stack("name").name("企业自主上报的需求")
                                        .data(Arrays.asList(satbEnterpriseCount, odEnterpriseCount, darbEnterpriseCount, esbEnterpriseCount)).build())).build();

        return success(multiBarVO);
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