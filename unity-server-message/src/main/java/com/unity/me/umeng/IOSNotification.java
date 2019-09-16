package com.unity.me.umeng;

import com.unity.common.pojos.UmengMessageDTO;
import com.unity.me.umeng.ios.IOSBroadcast;
import com.unity.me.umeng.ios.IOSCustomizedcast;
import com.unity.me.umeng.ios.IOSUnicast;
import com.unity.me.util.JSONObjectUtil;
import com.unity.me.util.MessageConstants;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

/**
 * @author wangbin
 * @since 2018/11/6
 */
public class IOSNotification extends UmengNotification {
    private static String payload = "payload";
    private static String aps = "aps";
    private static String policy = "policy";
    private static String alert = "alert";

    // Keys can be set in the aps level
    protected static final HashSet<String> APS_KEYS = new HashSet<String>(Arrays.asList(new String[]{
            alert, "badge", "sound", "content-available"
    }));
    // Keys can be set in the aps level
    protected static final HashSet<String> ALERT_KEYS = new HashSet<String>(Arrays.asList(new String[]{
            "title", "subtitle", "body", "content-available"
    }));

    @Override
    public boolean setPredefinedKeyValue(String key, Object value) throws Exception {
        if (ROOT_KEYS.contains(key)) {
            // This key should be in the root level
            rootJson.put(key, value);
        } else if (APS_KEYS.contains(key)) {
            // This key should be in the aps level
            //JSONObject payloadJson;
            //JSONObject apsJson;
            JSONObject payloadJson = JSONObjectUtil.getObjectCondition(rootJson, payload);
            /*if (rootJson.has(payload)) {
                payloadJson = rootJson.getJSONObject(payload);
            } else {
                payloadJson = new JSONObject();
                rootJson.put(payload, payloadJson);
            }*/
            JSONObject apsJson = JSONObjectUtil.getObjectCondition(payloadJson, aps);
            /*if (payloadJson.has(aps)) {
                apsJson = payloadJson.getJSONObject(aps);
            } else {
                apsJson = new JSONObject();
                payloadJson.put(aps, apsJson);
            }*/
            apsJson.put(key, value);
        } else if (POLICY_KEYS.contains(key)) {
            // This key should be in the body level
            JSONObject policyJson = JSONObjectUtil.getObjectCondition(rootJson, policy);
           /* JSONObject policyJson;
            if (rootJson.has(policy)) {
                policyJson = rootJson.getJSONObject(policy);
            } else {
                policyJson = new JSONObject();
                rootJson.put(policy, policyJson);
            }*/
            policyJson.put(key, value);
        } else if (ALERT_KEYS.contains(key)) {
            JSONObject payloadJson = JSONObjectUtil.getObjectCondition(rootJson, payload);
            JSONObject apsJson = JSONObjectUtil.getObjectCondition(payloadJson, aps);
            JSONObject alertJson = JSONObjectUtil.getObjectCondition(apsJson, alert);
            /*JSONObject apsJson;
            JSONObject payloadJson;
            JSONObject alertJson;
            if (rootJson.has(payload)) {
                payloadJson = rootJson.getJSONObject(payload);
            } else {
                payloadJson = new JSONObject();
                rootJson.put(payload, payloadJson);
            }
            if (payloadJson.has(aps)) {
                apsJson = payloadJson.getJSONObject(aps);
            } else {
                apsJson = new JSONObject();
                payloadJson.put(aps, apsJson);
            }
            if (apsJson.has(alert)) {
                alertJson = apsJson.getJSONObject(alert);
            } else {
                alertJson = new JSONObject();
                apsJson.put(alert, alertJson);
            }*/
            alertJson.put(key, value);
        } else {
            if (payload.equals(key) || aps.equals(key) || policy.equals(key)) {
                throw new Exception("You don't need to set value for " + key + " , just set values for the sub keys in it.");
            } else {
                throw new Exception("Unknownd key: " + key);
            }
        }

        return true;
    }


    // Set customized key/value for IOS notification
    public boolean setCustomizedField(String key, String value) {
        //rootJson.put(key, value);
        JSONObject payloadJson = JSONObjectUtil.getObjectCondition(rootJson, payload);
        /*JSONObject payloadJson;
        if (rootJson.has(payload)) {
            payloadJson = rootJson.getJSONObject(payload);
        } else {
            payloadJson = new JSONObject();
            rootJson.put(payload, payloadJson);
        }*/
        payloadJson.put(key, value);
        return true;
    }

    public void setTitle(String title) throws Exception {
        setPredefinedKeyValue("title", title);
    }

    public void setSubtitle(String subtitle) throws Exception {
        setPredefinedKeyValue("subtitle", subtitle);
    }

    public void setBody(String body) throws Exception {
        setPredefinedKeyValue("body", body);
    }

    public void setAlert(String token) throws Exception {
        setPredefinedKeyValue(alert, token);
    }

    public void setBadge(Integer badge) throws Exception {
        setPredefinedKeyValue("badge", badge);
    }

    public void setSound(String sound) throws Exception {
        setPredefinedKeyValue("sound", sound);
    }

    public void setContentAvailable(Integer contentAvailable) throws Exception {
        setPredefinedKeyValue("content-available", contentAvailable);
    }

    /**
     * 组装广播消息
     *
     * @param umengMessageDTO 消息内容
     * @param broadcast       广播对象
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2019/3/15 13:38
     */
    public void setIOSBroadcastParam(UmengMessageDTO umengMessageDTO, IOSBroadcast broadcast) throws Exception {
        setIOSCustomFieldParam(umengMessageDTO, broadcast);
    }

    /**
     * 自定义组装别名推送参数
     *
     * @param umengMessageDTO 消息对象
     * @param customizedcast  别名推送对象
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2019/3/15 14:27
     */
    public void setIOSCustomizedCastParam(UmengMessageDTO umengMessageDTO, IOSCustomizedcast customizedcast) throws Exception {
        customizedcast.setAlias(umengMessageDTO.getAlias(), MessageConstants.ALIAS_TYPE);
        customizedcast.setBadge(0);
        customizedcast.setSound("default");
        //自定义参数
        setIOSCustomFieldParam(umengMessageDTO, customizedcast);
    }

    /**
     * 自定义组装设备号推送参数
     *
     * @param umengMessageDTO 消息对象
     * @param iosUnicast      别名推送对象
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2019/3/15 14:27
     */
    public void setIOSUnicastParam(UmengMessageDTO umengMessageDTO, IOSUnicast iosUnicast) throws Exception {
        iosUnicast.setDeviceToken(umengMessageDTO.getDeviceToken());
        iosUnicast.setBadge(0);
        iosUnicast.setSound("default");
        //自定义参数
        setIOSCustomFieldParam(umengMessageDTO, iosUnicast);
    }

    /**
     * 自定义组装通用参数和额外参数
     *
     * @param umengMessageDTO 消息对象
     * @param iosNotification ios推送对象
     * @author zhangxiaogang
     * @since 2019/3/15 14:27
     */
    private void setIOSCustomFieldParam(UmengMessageDTO umengMessageDTO, IOSNotification iosNotification) throws Exception {
        iosNotification.setTitle(umengMessageDTO.getTitle());
        iosNotification.setBody(umengMessageDTO.getText());
        iosNotification.setSubtitle(umengMessageDTO.getSubTitle());
        Map<String, String> extraField = umengMessageDTO.getExtraField();
        if (!extraField.isEmpty()) {
            for (Map.Entry<String, String> map : extraField.entrySet()) {
                iosNotification.setCustomizedField(map.getKey(), map.getValue());
            }
        }
    }
}
