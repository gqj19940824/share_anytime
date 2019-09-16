package com.unity.rbac.controller.api;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.client.vo.UcsUser;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import com.unity.rbac.enums.UcsSourceEnum;
import com.unity.rbac.service.AccountConflictRecordServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 用户API控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 *
 * @author gengjiajia
 */
@Slf4j
@RestController
@RequestMapping("api/user")
public class UserApiController extends BaseWebController {

    private final AccountConflictRecordServiceImpl accountConflictRecordService;

    public UserApiController(AccountConflictRecordServiceImpl accountConflictRecordService) {
        this.accountConflictRecordService = accountConflictRecordService;
    }

    /**
     * 用户中心推送账号信息
     *
     * @param user 包含用户账号信息
     * @return code:0表示成功
     * -1013 缺少必要参数
     * -9999 系统异常
     * @author gengjiajia
     * @since 2019/07/25 19:38
     */
    @PostMapping("/pushUcsUserToSecurity")
    public Mono<ResponseEntity<SystemResponse<Object>>> pushUcsUserToSecurity(@RequestBody UcsUser user) {
        log.info("======《pushUcsUserToSecurity》 用户中心推送账号信息===入参 {}", GsonUtils.format(user));
        if (user == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "缺少必要参数");
        } else if (StringUtils.isEmpty(user.getLoginName())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到账号");
        } else if(user.getSource() == null || !UcsSourceEnum.exist(user.getSource())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到用户信息来源");
        } else if (user.getIdUcsUser() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到用户标识");
        }
        accountConflictRecordService.pushUcsUserToSecurity(user);
        log.info("======《pushUcsUserToSecurity》 ===出参 SUCCESS");
        return success("推送成功");
    }


}