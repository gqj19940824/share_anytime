package com.unity.common.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.unity.common.constant.SmsConstants;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SmsSetting;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 阿里短信服务工具类
 * <p>
 * create by gengjiajia at 2019/10/21 10:40
 */
@Slf4j
@Component
public class AliSmsUtils {

    @Resource
    private DicUtils dicUtils;

    /**
     * 获取阿里短信配置信息
     *
     * @return 阿里短信配置信息
     * @author gengjiajia
     * @since 2019/10/21 11:12  
     */
    private SmsSetting getAliSmdInfo(){
        //阿里短信必要数据保存在字典项中
        Dic dic = dicUtils.getDicByCode(SmsConstants.ALI_SMS_GROUP, SmsConstants.ALI_SMS_SETTING);
        if(dic == null || StringUtils.isNotBlank(dic.getNotes())){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("未配置阿里云短信服务")
                    .build();
        }
        try {
            return GsonUtils.parse(dic.getNotes(), SmsSetting.class);
        } catch (Exception e){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("阿里云短信服务配置错误")
                    .build();
        }
    }

    /**
     * 发送短信
     *
     * @param mobile 手机号，多个以英文逗号（,）分割
     * @param param 模板填充参数
     * @param templateCode 要使用的模板
     * @return code -> OK 发送成功
     * @author gengjiajia
     * @since 2019/10/21 11:13
     */
    public SendSmsResponse sendSms(String mobile, String param, String templateCode) {
        SmsSetting s = getAliSmdInfo();
        //设置超时时间-可自行调整
        System.setProperty(SmsConstants.SUN_NET_CLIENT_DEFAULTCONNECT, SmsConstants.SUN_NET_CLIENT_DEFAULTCONNECT_TIMEOUT);
        System.setProperty(SmsConstants.SUN_NET_CLIENT_DEFAULTREAD, SmsConstants.SUN_NET_CLIENT_DEFAULTREAD_TIMEOUT);
        try {
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile(SmsConstants.CN_HANGZHOU, s.getAccessKey(),
                    s.getSecretKey());
            DefaultProfile.addEndpoint(SmsConstants.CN_HANGZHOU, SmsConstants.CN_HANGZHOU, SmsConstants.ALI_SMS_PRODUCT, SmsConstants.ALI_SMS_DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
            request.setPhoneNumbers("18500632163");
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(s.getSignName());
            //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam(param);
            //请求失败这里会抛ClientException异常
            return acsClient.getAcsResponse(request);
        } catch (ClientException e){
            SendSmsResponse response = new SendSmsResponse();
            response.setCode("-9999");
            response.setMessage("网络异常，请稍后重试！");
            return response;
        }
    }
}
