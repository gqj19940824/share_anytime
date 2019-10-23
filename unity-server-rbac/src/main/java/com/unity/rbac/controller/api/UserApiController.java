package com.unity.rbac.controller.api;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
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

    /*@PostMapping("/login")
    public Mono<ResponseEntity<SystemResponse<Object>>> sysLogin(@RequestBody User user) {
        log.info("=====《后台登录》login-body:" + GsonUtils.format(user));
        if (StringUtils.isEmpty(user.getLoginName()) || StringUtils.isEmpty(user.getPwd())) {
            return error(SystemResponse.FormalErrorCode.USERNAME_OR_PASSWORD_EMPTY, "请输入正确格式的用户名和密码");
        }
        if(user.getOs() == null || user.getOs() < 0 || user.getOs() > PlatformTypeEnum.SYSTEM.getType()){
            return error(SystemResponse.FormalErrorCode.LOGIN_DATA_ERR, "未获取到当前操作终端类型");
        }
        Map map = userService.unityLogin(user.getLoginName(), user.getPwd(), user.getOs());
        return success(map);
    }*/
}