
package com.unity.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.common.client.MessageClient;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.IPUtils;
import com.unity.common.util.RedisUtils;
import com.unity.common.util.VerifyCodeUtils;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.entity.User;
import com.unity.rbac.enums.MessageTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
*
* ClassName: DefaultRoleService
* date: 2019-01-11 17:13:27
*
* @author creator
* @version
* @since JDK 1.8
*/
@Service
@Transactional(rollbackFor = Exception.class)
public class SMSServiceImpl{

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private MessageClient messageClient;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 发送短信验证码
     *
     * @param  body:{"phone":"18500632163","messageType":"10"}
     * @author gengjiajia
     * @since 2019/01/26 14:01
     */
    public void sendVerificationCode(Map<String,Object> body){
        if(body.get(UserConstants.PHONE) == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到手机号")
                    .build();
        }
        if(body.get(UserConstants.VERIF_TYPE) == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到发送方式")
                    .build();
        }
        User user = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getPhone, body.get(UserConstants.PHONE).toString()));
        if(!MessageTypeEnum.REGISTRATION.getId().toString().equals(body.get(UserConstants.VERIF_TYPE).toString())){
            if(user == null){
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                        .message("手机号未注册")
                        .build();
            }
        } else { //获取注册验证码时，手机号已存在
            if(user != null){
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                        .message("手机号已注册")
                        .build();
            }
        }

        Map<String,Object> map = messageClient.sendVerificationCode(body);
        if(map.get("code") == null || Integer.parseInt(map.get("code").toString()) != SystemResponse.FormalErrorCode.SUCCESS.getValue()){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .message("验证码发送失败，请稍后重试！")
                    .build();
        }
    }

    /**
     * 生成4位验证码
     *
     * @param
     * @author gengjiajia
     * @since 2019/01/26 14:01
     */
    public String createVerifyCode(String oldCode,HttpServletRequest req){
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        String ipAddress = IPUtils.getIPAddress(req);
        if(StringUtils.isNotEmpty(oldCode)){
            redisUtils.removeCurrentVerifyCodeByCode(oldCode.toLowerCase());
        }
        redisUtils.putCurrentVerifyCode(verifyCode.toLowerCase(),ipAddress);
        return verifyCode;
    }

    /**
     * 校验后发送短信验证码
     *
     * @param  body:{"phone":"18500632163","messageType":"10"，"verifyCode":24gh}
     * @author gengjiajia
     * @since 2019/05/22 14:01
     */
    public void checkAndSendVerificationCode(Map<String,Object> body, HttpServletRequest req){
        String code = (String) body.get(UserConstants.VERIFY_CODE);
        if(StringUtils.isBlank(code)){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到4位校验码")
                    .build();
        }
        String ipAddress = IPUtils.getIPAddress(req);
        String currentVerifyCodeByCode = (String)redisUtils.getCurrentVerifyCodeByCode(code.toLowerCase());
        if (ipAddress.equalsIgnoreCase(currentVerifyCodeByCode)){
            redisUtils.removeCurrentVerifyCodeByCode(code);
            sendVerificationCode(body);
        }else{
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LOGIN_VERIFY_CODE_ERROR)
                    .message("4位校验码输入错误")
                    .build();
        }
    }

    /**
     * 校验短信验证码
     *
     * @param  body:{"phone":"18888888888","verificationCode":"111111","messageType":10}
     * @author gengjiajia
     * @since 2019/01/23 16:07
     */
    public void checkVerificationCode(Map<String,Object> body){
        if(body.get(UserConstants.PHONE) == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到手机号")
                    .build();
        }
        if(body.get(UserConstants.VERIF_CODE) == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到短信验证码")
                    .build();
        }
        if(body.get(UserConstants.VERIF_TYPE) == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到验证方式")
                    .build();
        }
        Map<String, Object> map = messageClient.checkVerificationCode(body);
        if(map.get("code") == null || Integer.parseInt(map.get("code").toString()) != SystemResponse.FormalErrorCode.SUCCESS.getValue()){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .message(map.get("msg").toString())
                    .build();
        }
    }

}
