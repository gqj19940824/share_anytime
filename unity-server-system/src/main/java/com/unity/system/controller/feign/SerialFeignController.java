package com.unity.system.controller.feign;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.system.service.manual.ApiSerialServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * 流水号
 * @author creator
 * 生成时间 2018-12-20 17:12:38
 */
@Controller
@RequestMapping("/feign/serial")
public class SerialFeignController extends BaseWebController  {

    @Autowired
    ApiSerialServiceImpl service;

    /**
     * 根据code，查询生成规则类型，生成流水号
     * @param code
     * @return
     */
    @PostMapping("callComGenerateSyserialVal/{code}")
    public Object callComGenerateSyserialVal(  @PathVariable("code") String code) {
        return  service.callComGenerateSyserial(code);
    }


}
