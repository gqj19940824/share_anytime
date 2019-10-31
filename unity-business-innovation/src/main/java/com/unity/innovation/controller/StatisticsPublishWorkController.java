package com.unity.innovation.controller;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.entity.POJO.StatisticsSearch;
import com.unity.innovation.service.StatisticsPublishWorkService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author zhqgeng
 * 生成时间 2019-10-28 14:46
 */
@RestController
@RequestMapping("/publishWorkStatistics")
public class StatisticsPublishWorkController extends BaseWebController {

    @Resource
    StatisticsPublishWorkService service;

    /**
     * 功能描述 时间期间内 各单位工作基本情况统计 平均首次响应时间 和 平均完成时间
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/28 14:57
     */
    @PostMapping("/baseStatistics")
    public Mono<ResponseEntity<SystemResponse<Object>>> baseStatistics(@RequestBody StatisticsSearch search) throws Exception {
        if (StringUtils.isBlank(search.getBeginTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：开始时间不可为空");
        }
        if (StringUtils.isBlank(search.getEndTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：截止时间不可为空");
        }
        return success(service.baseStatistics(search));
    }

    /**
     * 功能描述 时间期间内 各单位超时未更新次数统计（次）
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/28 15:51
     */
    @PostMapping("/overDealTimes")
    public Mono<ResponseEntity<SystemResponse<Object>>> overDealTimes(@RequestBody StatisticsSearch search) throws Exception {
        if (StringUtils.isBlank(search.getBeginTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：开始时间不可为空");
        }
        if (StringUtils.isBlank(search.getEndTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：截止时间不可为空");
        }
        return success(service.overDealTimes(search));
    }

    /**
     * 功能描述  固定月份（它前六个月的数据） 六个月内某单位工作基本情况变化
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/29 14:29
     */
    @PostMapping("/changeStatistics")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeStatistics(@RequestBody StatisticsSearch search) throws Exception {
        if (StringUtils.isBlank(search.getMonthTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：时间不可为空");
        }
        return success(service.changeStatistics(search));
    }

    /**
     * 功能描述 固定月份（它前六个月的数据） 六个月内某单位超时次数变化
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/29 15:10
     */
    @PostMapping("/overDealChangeTimes")
    public Mono<ResponseEntity<SystemResponse<Object>>> overDealChangeTimes(@RequestBody StatisticsSearch search) throws Exception {
        if (StringUtils.isBlank(search.getMonthTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：时间不可为空");
        }
        return success(service.overDealChangeTimes(search));
    }

    /**
     * 功能描述 创新发布活动贡献情况统计
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/29 15:10
     */
    @PostMapping("/contribution")
    public Mono<ResponseEntity<SystemResponse<Object>>> contribution(@RequestBody StatisticsSearch search) throws Exception {
        if (StringUtils.isBlank(search.getBeginTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：开始时间不可为空");
        }
        if (StringUtils.isBlank(search.getEndTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：截止时间不可为空");
        }
        return success(service.contribution(search));
    }

    /**
     * 功能描述 发言人发声情况统计（次）
     * @param search 查询条件
     * @return 返回集合
     * @author gengzhiqiang
     * @date 2019/10/29 15:10
     */
    @PostMapping("/voice")
    public Mono<ResponseEntity<SystemResponse<Object>>> voice(@RequestBody StatisticsSearch search) throws Exception{
        if (StringUtils.isBlank(search.getBeginTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：开始时间不可为空");
        }
        if (StringUtils.isBlank(search.getEndTime())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询条件：截止时间不可为空");
        }
        return success(service.voice(search));
    }

}
