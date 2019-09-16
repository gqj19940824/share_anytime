package com.unity.rbac.controller;

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
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 统一登录
 *
 * <p>
 * create by gengjiajia at 2018/12/17 09:31
 * @author gengjiajia
 */
@Slf4j
@RestController
public class LoginController extends BaseWebController{

    private final UserServiceImpl userService;

    public LoginController(UserServiceImpl userService){
        this.userService = userService;
    }
    /**
     * 全局统一登录
     *
     * @param user 包含用户登录条件
     * @return code : 0 表示成功
     * -1001 登录名或密码有误
     * -1010 登录信息有误
     * -1011 用户不存在
     * -1013 缺少必要参数
     *
     * @author gengjiajia
     * @since 2018/12/11 10:04
     */
    @PostMapping("sys/login")
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
    }


    /**
     * 统一退出登录
     *
     * @return code 0 成功
     * @author gengjiajia
     * @since 2019/04/02 16:53
     */
    @PostMapping("sys/logout")
    public Mono<ResponseEntity<SystemResponse<Object>>> unityLogout(@RequestBody Map<String, String> body) {
        log.info("=====《全局统一退出登录》login-body:" + GsonUtils.format(body));
        userService.unityLogout(body.get("os"));
        return success("退出成功");
    }
}
