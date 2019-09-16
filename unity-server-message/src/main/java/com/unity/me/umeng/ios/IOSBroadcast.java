package com.unity.me.umeng.ios;


import com.unity.me.umeng.IOSNotification;

/**
 * @author wangbin
 * @since 2018/11/6
 */
public class IOSBroadcast  extends IOSNotification {

    public IOSBroadcast(String appkey,String appMasterSecret) throws Exception {
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "broadcast");

    }


}
