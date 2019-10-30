package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.DicConstants;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.controller.vo.MultiBarVO;
import com.unity.innovation.entity.MediaManager;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.service.InfoDeptSatbServiceImpl;
import com.unity.innovation.service.IpaManageMainServiceImpl;
import com.unity.innovation.service.MediaManagerServiceImpl;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * create by qinhuan at 2019/10/28 4:40 下午
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
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

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
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取时间范围");
        }
        Long startTime = InnovationUtil.getFirstTimeInMonth(map.get(START_DATE), true);
        Long endTime = InnovationUtil.getFirstTimeInMonth(map.get(END_DATE), false);
        List<IpaManageMain> manageMainList = ipaManageMainService.list(new LambdaQueryWrapper<IpaManageMain>()
                .eq(IpaManageMain::getStatus, WorkStatusAuditingStatusEnum.SIXTY.getId())
                .between(IpaManageMain::getGmtCreate, startTime, endTime));
        List<Long> allMediaIdList = Lists.newArrayList();
        for (IpaManageMain main : manageMainList) {
            String[] mediaIdArr = main.getPublishMedia().split(",");
            allMediaIdList.addAll(Arrays.stream(mediaIdArr).map(Long::parseLong).collect(Collectors.toList()));
        }
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
                .xAxis(
                        Collections.singletonList(MultiBarVO.XAxisBean.newInstance()
                                .type("category")
                                .data(xData)
                                .build())
                ).series(
                        Arrays.asList(
                                MultiBarVO.SeriesBean.newInstance()
                                        .type("bar")
                                        .name("媒体发稿情况")
                                        .data(yData)
                                        .build()
                        )).build();
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
    @PostMapping("/infoDeptSatb/avgStatistics")
    public Mono<ResponseEntity<SystemResponse<Object>>> avgStatistics(@RequestBody Map<String, String> map) {
        if (MapUtils.isEmpty(map) || StringUtils.isEmpty(map.get(START_DATE)) || StringUtils.isEmpty(map.get(END_DATE))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取时间范围");
        }
        Long endTime = InnovationUtil.getFirstTimeInMonth(map.get(END_DATE), false);
        Long startTime = InnovationUtil.getFirstTimeInMonth(map.get(START_DATE), true);
        return success(infoDeptSatbService.avgStatistics(startTime, endTime));
    }

    /**
     * 与会路演企业成果首次对外发布情况统计
     *
     * @param map 包含开始时间及结束时间
     * @return 统计数据
     * @author gengjiajia
     * @since 2019/10/29 19:55
     */
    @PostMapping("/infoDeptSatb/firstExternalRelease")
    public Mono<ResponseEntity<SystemResponse<Object>>> firstExternalRelease(@RequestBody Map<String, String> map) {
        if (MapUtils.isEmpty(map)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取时间范围");
        }
        if(StringUtils.isEmpty(map.get(START_DATE)) || StringUtils.isEmpty(map.get(END_DATE))){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取时间范围");
        }
        Long startTime = InnovationUtil.getFirstTimeInMonth(map.get(START_DATE), true);
        Long endTime = InnovationUtil.getFirstTimeInMonth(map.get(END_DATE), false);
        return success(infoDeptSatbService.firstExternalRelease(startTime, endTime));
    }
}