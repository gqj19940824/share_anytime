
package com.unity.me.service;

import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.me.Constants.Constants;
import com.unity.me.client.SystemMeClient;
import com.unity.me.dao.VerificationCodeDao;
import com.unity.me.entity.VerificationCode;
import com.unity.me.enums.MessageTypeEnum;
import com.unity.me.util.MessageConstants;
import com.unity.me.util.SMSUtil;
import com.unity.me.util.ValidationCodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * ClassName: VerificationCodeService
 * Function: sms Verification Code
 * date: 2019-01-24 15:53:28
 *
 * @author liuzhen
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VerificationCodeServiceImpl extends BaseServiceImpl<VerificationCodeDao, VerificationCode> {

    @Autowired
    SystemMeClient systemClient;

    /**
     * 发送短信
     *
     * @param
     * @return
     * @author liuzhen
     * @since 2019/1/24 15:30
     */
    public String sendVerificationCode(String phone, int msgType) {
        //判断之前获取的验证码是否过期
        VerificationCode verificationCodeStoraged = baseMapper.findFirstByPhoneNoOrderBySendTimeDesc(phone, msgType);

        //超时
        if (verificationCodeStoraged != null) {
            long diff = System.currentTimeMillis() - verificationCodeStoraged.getGmtCreate();
            if (0 <= diff && diff <= MessageConstants.FREQUEN_CONDITION) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.MODIFY_DATA_REPEAT_OPERATION)
                        .message("您之前获取的验证码尚未过期").build();
            }
        }

        //6位验证码
        String validationCode = ValidationCodeUtils.getSixValidationCode();
        String[] strings = {validationCode};

        //短信验证码发送
        sendShortMessage(phone, strings, msgType,Constants.MSG_TEMPLATE_SUBSCRIBER_LOGIN);
        return validationCode;
    }


    /**
     * 发送短信
     *
     * @param phone   手机号
     * @param strings 发送参数
     * @param msgType 短信类型
     * @param templateId 模板id
     * @author zhangxiaogang
     * @since 2019/4/16 17:39
     */
    public void sendShortMessage(String phone, String[] strings, int msgType, int templateId) {
        if (phone.length() != 11 || !phone.matches(MessageConstants.MOBILE_CHECKED)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("手机号格式不正确").build();
        }
        //短信发送
        if (SMSUtil.send(phone, strings, templateId)) {
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setCreator(phone);
            verificationCode.setEditor(phone);
            verificationCode.setPhone(phone);
            if (msgType <= MessageTypeEnum.FORGOTTENPASSWORD.getId()) {
                verificationCode.setVerificationCode(strings[0]);
            }
            verificationCode.setNotes(Arrays.toString(strings));
            verificationCode.setMessageType(msgType);
            verificationCode.setExpiryTime(5);
            this.save(verificationCode);
        } else {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .message("短信发送失败").build();
        }
    }

    /**
     * 校验验证码
     *
     * @param verificationCode 参数封装 phone ，code
     * @return 200 验证码正确
     * -1008 手机号格式不正确
     * -1014 验证码错误
     * -1015 验证码超时
     * -1011 手机号不存在
     * @author liuzhen
     * @since 2019/1/24 15:30
     */
    public String checkSMSVerificationCode(VerificationCode verificationCode) {
        if (verificationCode == null || StringUtils.isBlank(verificationCode.getPhone())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("手机号不能为空").build();
        }

        //手机号校验
        if (!verificationCode.getPhone().matches(MessageConstants.MOBILE_CHECKED)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("手机号格式不正确").build();
        }

        //通过手机号获得数据库最新短信信息（验证码，时间）
        VerificationCode verificationCodeStoraged = baseMapper.findFirstByPhoneNoOrderBySendTimeDesc(verificationCode.getPhone(), verificationCode.getMessageType());
        if (verificationCodeStoraged == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("验证码不存在").build();
        }

        if (verificationCodeStoraged.getVerificationCode().equals(verificationCode.getVerificationCode())) {
            long diff = System.currentTimeMillis() - verificationCodeStoraged.getGmtCreate();
            //超时
            if (0 <= diff && diff <= MessageConstants.CODE_OVERTIME) {
                //  如果是修改手机号获取验证 或 修改密码获取的验证码 则不需要保存
                return null;
            } else {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.GET_DATA_OUT_TIME)
                        .message("验证码已超时").build();
            }
        } else {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("验证码错误").build();
        }
    }
}