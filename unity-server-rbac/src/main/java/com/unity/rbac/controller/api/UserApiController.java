package com.unity.rbac.controller.api;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.entity.User;
import com.unity.rbac.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 用户信息后台控制类
 * <p>
 *
 * @author gengjiajia
 * @since 2018/12/21 13:45
 */
@Slf4j
@RestController
@RequestMapping("api/user")
public class UserApiController extends BaseWebController {

    private final UserServiceImpl userService;

    public UserApiController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * 身份认证
     *
     * @param user 用户信息
     * @return code 0 表示调用成功
     * @author gengjiajia
     * @since 2019/10/23 15:33
     */
    @PostMapping("/authentication")
    public Mono<ResponseEntity<SystemResponse<Object>>> authentication(@RequestBody Map<String,String> user) {
        log.info("=====《身份认证》login-body {}", GsonUtils.format(user));
        if (StringUtils.isEmpty(user.get(UserConstants.PHONE))) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到登录账号");
        } else if(StringUtils.isEmpty(user.get(UserConstants.SECRET))){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到认证标识");
        }
        //身份认证
        Map<String,Object> map = userService.authentication(user.get(UserConstants.PHONE), user.get(UserConstants.SECRET));
        return success(map);
    }
}