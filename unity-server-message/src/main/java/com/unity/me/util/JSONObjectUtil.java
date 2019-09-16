package com.unity.me.util;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.unity.me.Constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 短信工具类
 * create at 2018年7月18日20:44:25
 */
@Slf4j
public class JSONObjectUtil {

    /**
     * 判断组装新的对象
     *
     * @param jsonObj json对象
     * @param objKey  判断key
     * @return 新的json对象
     * @author zhangxiaogang
     * @since 2019/3/13 15:41
     */
    public static JSONObject getObjectCondition(JSONObject jsonObj, String objKey) {
        JSONObject objJson;
        if (jsonObj.has(objKey)) {
            objJson = jsonObj.getJSONObject(objKey);
        } else {
            objJson = new JSONObject();
            jsonObj.put(objKey, objJson);
        }
        return objJson;
    }
}
