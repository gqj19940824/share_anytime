package com.unity.me.umeng.ios;


import com.unity.me.umeng.IOSNotification;

/**
 * @author wangbin
 * @since 2018/11/6
 */
public class IOSUnicast extends IOSNotification {

    public IOSUnicast(String appkey,String appMasterSecret) throws Exception{
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "unicast");
    }

    public void setDeviceToken(String token) throws Exception {
        setPredefinedKeyValue("device_tokens", token);
    }

}
