package com.unity.system.controller.api;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.system.enums.SystemTypeEnum;
import com.unity.system.service.AppVersionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * <p>
 * createby:zhaozesheng at 2019-02-27 15:04
 */
@RestController
@RequestMapping("/api/appversion")
public class AppVersionApiController extends BaseWebController {

    @Autowired
    AppVersionServiceImpl appVersionService;


    /**
     * App查询版本信息是否需要更新
     *
     * @param systemType 查询类型
     * @return 查询版本信息
     * code 0 success
     * code -1013 您查询类型不匹配
     * @author zhaozesheng
     * @since 2019/2/27 15:05
     */
    @GetMapping("getAppVersion")
    public Mono<ResponseEntity<SystemResponse<Object>>> getAppVersion(Integer systemType) {
        if (systemType == null || SystemTypeEnum.of(systemType) == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "您查询类型不匹配");
        }
        return success(appVersionService.findAppVersionBySystemType(systemType));
    }
}
