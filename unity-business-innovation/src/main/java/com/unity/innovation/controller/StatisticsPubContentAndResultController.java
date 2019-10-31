package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.DicConstants;
import com.unity.common.constants.ConstString;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.controller.vo.MultiBarVO;
import com.unity.innovation.entity.MediaManager;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.service.InfoDeptSatbServiceImpl;
import com.unity.innovation.service.IpaManageMainServiceImpl;
import com.unity.innovation.service.IplOdMainServiceImpl;
import com.unity.innovation.service.MediaManagerServiceImpl;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * create by qinhuan at 2019/10/28 4:40 下午
 * @author gengjiajia
 */
@RestController
@RequestMapping("/statisticsPubContentAndResult")
public class StatisticsPubContentAndResultController extends BaseWebController {
    @Resource
    private IpaManageMainServiceImpl ipaManageMainService;
    @Resource
    private MediaManagerServiceImpl mediaManagerService;
    @Resource
    private DicUtils dicUtils;
    @Resource
    private InfoDeptSatbServiceImpl infoDeptSatbService;
    @Resource
    private IplOdMainServiceImpl iplOdMainService;
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String YEAR_MONTH = "yearMonth";
    private static final String TYPE = "type";

    /**
     * 媒体发稿情况
     *
     * @param map 包含开始时间及结束时间
     * @return 统计数据
     * @author gengjiajia
     * @since 2019/10/29 19:55
     */
    @PostMapping("/mediaReleaseSituation")
    public Mono<ResponseEntity<SystemResponse<Object>>> mediaReleaseSituation(@RequestBody Map<String, String> map) {
        if (MapUtils.isEmpty(map) || StringUtils.isEmpty(map.get(START_DATE)) || StringUtils.isEmpty(map.get(END_DATE))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到时间范围");
        }
        Long startTime = InnovationUtil.getFirstTimeInMonth(map.get(START_DATE), true);
        Long endTime = InnovationUtil.getFirstTimeInMonth(map.get(END_DATE), false);
        List<IpaManageMain> manageMainList = ipaManageMainService.list(new LambdaQueryWrapper<IpaManageMain>()
                .eq(IpaManageMain::getStatus, WorkStatusAuditingStatusEnum.SIXTY.getId())
                .between(IpaManageMain::getGmtCreate, startTime, endTime));
        List<Long> allMediaIdList = manageMainList.stream().map(main ->
                Arrays.stream(main.getPublishMedia().split(ConstString.SPLIT_COMMA))
                        .map(Long::parseLong)
                        .collect(Collectors.toList())
        ).flatMap(List::stream).collect(Collectors.toList());
        List<MediaManager> mediaManagerList = mediaManagerService.list(new LambdaQueryWrapper<MediaManager>().in(MediaManager::getId, allMediaIdList));
        Map<Long, Long> data = mediaManagerList.stream()
                .collect(Collectors.groupingBy(MediaManager::getMediaType, Collectors.counting()));
        List<String> xData = Lists.newArrayList();
        List<Integer> yData = Lists.newArrayList();
        for (Map.Entry<Long, Long> entry : data.entrySet()) {
            if (!entry.getValue().equals(0L)) {
                yData.add(entry.getValue().intValue());
                Dic dic = dicUtils.getDicByCode(DicConstants.MEDIA_TYPE, entry.getKey().toString());
                xData.add(dic.getDicValue());
            }
        }
        MultiBarVO multiBarVO = MultiBarVO.newInstance()
                .xAxis(Collections.singletonList(MultiBarVO.XAxisBean.newInstance()
                                .type("category")
                                .data(xData)
                                .build())
                ).series(Lists.newArrayList(MultiBarVO.SeriesBean.newInstance()
                        .type("bar")
                        .name("媒体发稿情况")
                        .data(yData)
                        .build())).build();
        return success(multiBarVO);
    }

    /**
     * 路演企业成果创新水平统计
     *
     * @param map 包含开始时间及结束时间
     * @return 统计数据
     * @author gengjiajia
     * @since 2019/10/29 19:55
     */
    @PostMapping("/roadshowEnterprise/achievementInnovationLevel")
    public Mono<ResponseEntity<SystemResponse<Object>>> achievementInnovationLevel(@RequestBody Map<String, String> map) {
        if (MapUtils.isEmpty(map) || StringUtils.isEmpty(map.get(START_DATE)) || StringUtils.isEmpty(map.get(END_DATE))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到时间范围");
        }
        Long endTime = InnovationUtil.getFirstTimeInMonth(map.get(END_DATE), false);
        Long startTime = InnovationUtil.getFirstTimeInMonth(map.get(START_DATE), true);
        return success(infoDeptSatbService.roadshowEnterpriseInnovationLevel(startTime, endTime));
    }

    /**
     * 与会路演企业成果首次对外发布情况统计
     *
     * @param map 包含开始时间及结束时间
     * @return 统计数据
     * @author gengjiajia
     * @since 2019/10/29 19:55
     */
    @PostMapping("/roadshowEnterprise/resultsFirstReleased")
    public Mono<ResponseEntity<SystemResponse<Object>>> resultsFirstReleased(@RequestBody Map<String, String> map) {
        if (MapUtils.isEmpty(map)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到时间范围");
        }
        if(StringUtils.isEmpty(map.get(START_DATE)) || StringUtils.isEmpty(map.get(END_DATE))){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到时间范围");
        }
        Long startTime = InnovationUtil.getFirstTimeInMonth(map.get(START_DATE), true);
        Long endTime = InnovationUtil.getFirstTimeInMonth(map.get(END_DATE), false);
        return success(infoDeptSatbService.firstExternalRelease(startTime, endTime));
    }

    /**
     * 企业高端才智需求变化统计
     *
     * @param  map 包含时间
     * @return 高端才智需求变化统计
     * @author gengjiajia
     * @since 2019/10/30 16:23  
     */
    @PostMapping("/highendTalentDemand/changesStatistics")
    public Mono<ResponseEntity<SystemResponse<Object>>> changesStatistics(@RequestBody Map<String, String> map) {
        if (MapUtils.isEmpty(map) || StringUtils.isEmpty(map.get(YEAR_MONTH))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到时间范围");
        }
        return success(iplOdMainService.changeInPersonnelNeeds(map.get(YEAR_MONTH)));
    }

    /**
     * 企业高端才智需求行业分布及变化饼图统计
     *
     * @param map 包含指定年月
     * @return 新增人才需求统计
     * @author gengjiajia
     * @since 2019/10/30 18:48
     */
    @PostMapping("/highendTalentDemand/statisticsIndustryDemand")
    public Mono<ResponseEntity<SystemResponse<Object>>> statisticsIndustryDemand(@RequestBody Map<String, String> map) {
        if (MapUtils.isEmpty(map) || StringUtils.isEmpty(map.get(YEAR_MONTH))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到时间范围");
        } else if(StringUtils.isEmpty(map.get(TYPE))){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到要查询的类型");
        }
        return success(iplOdMainService.statisticsIndustryDemand(map.get(YEAR_MONTH),Integer.parseInt(map.get(TYPE))));
    }
}