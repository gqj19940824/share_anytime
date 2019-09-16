package com.unity.me.umeng.android;


import com.unity.me.umeng.AndroidNotification;

/**
 * @author wangbin
 * @since 2018/11/6
 */
public class AndroidUnicast extends AndroidNotification {

    public AndroidUnicast(String appkey,String appMasterSecret) throws Exception {
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "unicast");
    }




    public void setDeviceToken(String token) throws Exception {
        setPredefinedKeyValue("device_tokens", token);
    }
}
