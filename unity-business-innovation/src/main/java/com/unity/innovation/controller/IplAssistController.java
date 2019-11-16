package com.unity.innovation.controller;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.enums.BizTypeEnum;
import com.unity.innovation.service.IplAssistServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 创新发布清单-协同事项
 * @author zhang
 * 生成时间 2019-09-21 15:45:35
 */
@RestController
@RequestMapping("/iplassist")
public class IplAssistController extends BaseWebController {

    @Resource
    private IplAssistServiceImpl iplAssistService;

     /**
     * 获取一页数据
     * @param pageEntity 统一查询条件
     * @return
     */
    @PostMapping("/listAssistByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<Map<String, Object>> pageEntity) {
        return success(iplAssistService.listAssistByPage(pageEntity));
    }


    /**
     * 获取清单类型列表（排除自身所属清单）
     * @return
     */
    @GetMapping("/listBizType")
    public Mono<ResponseEntity<SystemResponse<Object>>> listBizType() {
        List<BizTypeEnum> bizTypeEnums = Arrays.asList(BizTypeEnum.CITY, BizTypeEnum.ENTERPRISE, BizTypeEnum.GROW, BizTypeEnum.INTELLIGENCE);
        List<Map<String, Object>> collect = bizTypeEnums.stream().map(BizTypeEnum::toMap).collect(Collectors.toList());
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (customer.getIdRbacDepartment() != null){
            List<Integer> typeRangeList = customer.getTypeRangeList();
            collect.removeIf(e->typeRangeList.contains(MapUtils.getInteger(e,"type")));
            return success(collect);
        }else {
            return success(new ArrayList<>());
        }
    }
}

