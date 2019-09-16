package com.unity.me.Constants;

/**
 * Author:liuzhen
 * Date:2019/1/24
 **/
/**
 * 系统常量池
 * Created by liuzhen on 24/01/2019.
 */
public abstract class Constants {
    private Constants() {

    }

    //短信有效期的默认时间（秒）
    public static final long SMS_VALIDATE = 60*5;

    /**
     * 短信应用SDK AppID
     *  1400开头
     */
    public static final int APP_ID = 1400204397;

    /**
     * 短信应用SDK AppKey
     */
    public static final String APP_KEY = "0be662adbe9b616cf5e6b9906c3c1374";

    /**
     * 签名
     * NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`
     */
    public static final String SMS_SIGN = "初心使命";

   /* 365284 普通短信 2019-07-02 17:42:03 验证码 您的验证码为:{1}，此验证码10分钟内有效。 待审核 编辑删除
365283 普通短信 2019-07-02 17:41:47 提醒 您好，您有已处理完毕的哨声，请登录电脑或微信小程序端查看。 待审核 编辑删除
365282 普通短信 2019-07-02 17:41:33 哨声提醒 您有新的哨声需要处理。*/

    /**
     * 短信模板ID，需要在短信应用中申请
     */
    public static final int MSG_TEMPLATE_SUBSCRIBER_LOGIN = 365284;

    /**
     * 短信模板ID，需要在短信应用中申请(企业哨声完成通知模板)
     */
    public static final int MSG_TEMPLATE_SUBSCRIBER_ENTERPRISE_WHISTLE = 365283;
    /**
     * 短信模板ID，需要在短信应用中申请(服务港收到哨声通知模板)
     */
    public static final int MSG_TEMPLATE_ACCEPT_ENTERPRISE_WHISTLE = 365282;
}

