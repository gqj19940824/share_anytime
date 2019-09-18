package com.unity.rbac.controller.api;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import com.unity.rbac.service.SMSServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 短信API控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 */
@Slf4j
@RestController
@RequestMapping("api/sms")
public class SMSApiController extends BaseWebController {

    private final SMSServiceImpl smsService;

    public SMSApiController(SMSServiceImpl smsService){
        this.smsService = smsService;
    }

    /**
     * 发送短信验证码
     *
     * @param  body:{"phone":"18500632163","messageType":10}
     * @return code:0表示成功
     * -1013 缺少必要参数
     * -9999 发送失败
     * @author gengjiajia
     * @since 2019/01/23 15:49
     */
    @PostMapping("/sendVerificationCode")
    public Mono<ResponseEntity<SystemResponse<Object>>> sendVerificationCode(@RequestBody Map<String,Object> body) {
        log.info("======《sendVerificationCode》 ===入参 {}", GsonUtils.format(body));
        smsService.sendVerificationCode(body);
        log.info("======《sendVerificationCode》 ===出参 SUCCESS");
        return success("验证码已发送成功");
    }

    /**
     *  生成4位校验码
     *
     * @param: req
     * @return:  body 校验码
     * @auther: jiaww
     * @since: 2018/10/6 14:34
     */
    @GetMapping("createCode")
    public Mono<ResponseEntity<SystemResponse<Object>>> createCode(String oldCode,HttpServletRequest req) {
        log.info("======《createCode》 ===入参 {}", "HttpServletRequest");
        String verifyCode = smsService.createVerifyCode(oldCode,req);
        log.info("======《createCode》 ===出参 SUCCESS");
        return success("获取成功",verifyCode);
    }

    /**
     * 校验后发送短信验证码
     *
     * @param  body:{"phone":"18500632163","messageType":10，"verifyCode":24gh}
     * @return code:0表示成功
     * -1013 缺少必要参数
     * -9999 发送失败
     * @author gengjiajia
     * @since 2019/01/23 15:49
     */
    @PostMapping("/checkAndSendVerificationCode")
    public Mono<ResponseEntity<SystemResponse<Object>>> checkAndSendVerificationCode(@RequestBody Map<String,Object> body,HttpServletRequest req) {
        log.info("======《checkAndSendVerificationCode》 ===入参 {}", GsonUtils.format(body));
        smsService.checkAndSendVerificationCode(body,req);
        log.info("======《checkAndSendVerificationCode》 ===出参 SUCCESS");
        return success("验证码已发送成功");
    }

    /**
     * 校验验证码
     *
     * @param  body:{"phone":"18888888888","verificationCode":"111111","messageType":10}
     * @return code:0表示成功
     * -1013 缺少必要参数
     * -9999 验证失败
     * @author gengjiajia
     * @since 2019/01/23 15:49
     */
    @PostMapping("/checkVerificationCode")
    public Mono<ResponseEntity<SystemResponse<Object>>> checkVerificationCode(@RequestBody Map<String,Object> body) {
        log.info("======《checkCode》 ===入参 {}", GsonUtils.format(body));
        smsService.checkVerificationCode(body);
        log.info("======《checkCode》 ===出参 SUCCESS");
        return success("验证成功");
    }
}