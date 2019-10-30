package com.unity.innovation.controller;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.entity.POJO.StatisticsSearch;
import com.unity.innovation.service.StatisticsInfoServiceImpl;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @ClassName StatisticsInfoController
 * @Description 企业信息统计Controller
 * @Author JH
 * @Date 2019/10/28 15:17
 */
@RestController
@RequestMapping("statisticsInfo")
public class StatisticsInfoController extends BaseWebController {

    @Resource
    private StatisticsInfoServiceImpl service;

    /**
    * 与会企业基本情况数据统计
    *
    * @param entity 查询条件
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/30 9:58
    */
    @PostMapping("/getParticipateInfo")
    public Mono<ResponseEntity<SystemResponse<Object>>> getParticipateDeptInfo(@RequestBody StatisticsSearch  entity) {
        if(entity == null || StringUtils.isBlank(entity.getBeginTime()) || StringUtils.isBlank(entity.getEndTime()) || entity.getType() == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("缺少必要参数").build();
        }
        Map<String, Long> timeMap = InnovationUtil.getTime(entity.getBeginTime(), entity.getEndTime());
        String[] begin = entity.getBeginTime().split("-");
        String[] end = entity.getEndTime().split("-");
        String title = begin[0]+"年"+begin[1]+"月-"+end[0]+"年"+end[1]+"月";
        Map<String, Object> res ;
        if(entity.getType() == 1) {
            res = service.getParticipateDeptInfo(timeMap,title);
        } else if(entity.getType() == 2) {
            res = service.getParticipateInvestInfo(timeMap,title);
        } else {
            res = service.getParticipateMediaInfo(timeMap,title);
        }
        return success(res);
    }



}
