/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ApiDictionaryItemSelect
 * Author:   admin
 * Date:     2018/12/26 10:47
 * Description: 字典项选择
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.unity.me.controller.feign;

import com.alibaba.druid.sql.visitor.functions.If;
import com.google.common.collect.Maps;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import com.unity.me.Constants.Constants;
import com.unity.me.entity.VerificationCode;
import com.unity.me.enums.MessageTypeEnum;
import com.unity.me.service.VerificationCodeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 获取，验证短信验证码<br>
 *
 * @author liuzhen
 * @create 2019/1/25
 * @since 1.0.0
 */
@RestController
@RequestMapping("/feign/sms")
@Slf4j
public class SMSFeignController extends BaseWebController {

    @Autowired
    VerificationCodeServiceImpl service;

    /**
     *  短信发送
     *
     * @param verificationCode  phone 手机号
     * @return
     * -1013, "缺少必要参数"
     * 0, "操作成功"
     * @author liuzhen
     * @since 2018-09-04 10:22:35
     */
    @PostMapping("/sendVerificationCode")
    public Map<String,Object> sendVerificationCode(@RequestBody VerificationCode verificationCode) {
        Map<String, Object> returnMap = Maps.newHashMap();
        returnMap.put("code", SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getValue());

        if (verificationCode.getPhone() ==null ){
            returnMap.put("msg", "手机号不能为空！");
            return returnMap;
        }
        service.sendVerificationCode(verificationCode.getPhone(),verificationCode.getMessageType());
        returnMap.put("code",SystemResponse.FormalErrorCode.SUCCESS.getValue());
        returnMap.put("msg", SystemResponse.FormalErrorCode.SUCCESS.getName());
        return returnMap;
    }
    /**
     * 发送短信通知
     *
     * @param verificationCode  phone 手机号 content 短信内容
     *  0  操作成功
     * -1013 缺少必要参数
     *@author zhangxiaogang
     *@since 2019/4/29 14:44
     */
    @PostMapping("/sendVerificationNotice")
    public void sendVerificationNotice(@RequestBody VerificationCode verificationCode) {
        if (verificationCode.getPhone() ==null ){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("手机号码不能为空").build();
        }
        service.sendShortMessage(verificationCode.getPhone(),verificationCode.getContent(), MessageTypeEnum.ENTERPRISEWHISTLE.getId(), Constants.MSG_TEMPLATE_SUBSCRIBER_ENTERPRISE_WHISTLE);
    }

    @PostMapping("/sendVerificationNotices")
    public void sendVerificationNotices(@RequestBody List<VerificationCode> verificationCodes) {
        if(CollectionUtils.isEmpty(verificationCodes)){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("参数不能为空").build();
        }
        log.info("发送的短信入参："+  GsonUtils.format(verificationCodes));
        new Thread(() -> {
            verificationCodes.forEach(code -> {
                if(code.getPhone()!= null){
                    service.sendShortMessage(code.getPhone(),code.getContent(), MessageTypeEnum.ENTERPRISEWHISTLES.getId(), Constants.MSG_TEMPLATE_ACCEPT_ENTERPRISE_WHISTLE);
                }
            });
        }).start();
    }

    /**
     *  校验验证码 前台传入(phone,code)
     *
     * @param verificationCode 参数封装 phone ，code
     * @return
     *   200 验证码正确
     *  -1008 手机号格式不正确
     *  -1014 验证码错误
     *  -1015 验证码超时
     *  -1011 手机号不存在
     * @author Wangqingzhi
     * @since 2018-09-04 19:24:41
     */
    @PostMapping("/checkVerificationCode")
    public Map<String,Object> checkSMSCaptcha(@RequestBody VerificationCode verificationCode)  {
        Map<String, Object> returnMap = Maps.newHashMap();
        String result=service.checkSMSVerificationCode(verificationCode);
        if(result==null){
            returnMap.put("code", SystemResponse.FormalErrorCode.SUCCESS.getValue());
            returnMap.put("msg", "验证成功！");
            return returnMap;
        }else{
            returnMap.put("code", SystemResponse.FormalErrorCode.SERVER_ERROR.getValue());
            returnMap.put("msg", "验证码校验错误！");
            return returnMap;
        }
    }
}