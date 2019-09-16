package com.unity.me.util;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.unity.me.Constants.Constants;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 短信工具类
 * create at 2018年7月18日20:44:25
 */
public class SMSUtil {

    /**
     *  短信发送方法
     * @param phoneNumber 不带国家码的手机号
     * @param templateParam 模板参数列表，如模板 {1}...{2}...{3}，那么需要带三个参数
     * @param templateId 模板id
     * @return resultMessage 短信发送结果
     * @author Wangqingzhi
     * @since 2018-09-04 14:22:32
     */
    public static boolean send(String phoneNumber, String[] templateParam, int templateId) {
        boolean resultMessage = false;
        try {
            SmsSingleSender ssender = new SmsSingleSender(Constants.APP_ID, Constants.APP_KEY);
            // 签名参数未提供或者为空时，会使用默认签名发送短信
            SmsSingleSenderResult result =
                    ssender.sendWithParam("86", phoneNumber,
                            templateId, templateParam, Constants.SMS_SIGN, "", "");
            if (result.result==0){
                resultMessage = true;
            }
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
        return resultMessage;
    }
}
