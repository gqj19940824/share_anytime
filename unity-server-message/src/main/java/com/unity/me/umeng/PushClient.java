package com.unity.me.umeng;

import com.google.common.collect.Maps;
import com.unity.common.utils.HttpsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**s
 * @author wangbin
 * @since 2018/11/6
 */
@Slf4j
public class PushClient {

    // The host
    private static final String host = "http://msg.umeng.com";

    // The upload path
    private static final String uploadPath = "/upload";

    // The post path
    private static final String postPath = "/api/send";

    /**
     * 获取http请求的请求头数据
     *
     * @author zhangxiaogang
     * @since 2019/3/13 20:52
     */
    private static Map<String, String> getHeaders() {
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("User-Agent", "Mozilla/5.0");
        return headerMap;
    }

    /**
     * 使用友盟进行消息推送
     *
     * @param msg 消息内容
     * @return 发送结果
     * @author zhangxiaogang
     * @since 2019/3/13 20:51
     */
    public static boolean send(UmengNotification msg) {
        StringBuffer urlSB = new StringBuffer();
        urlSB.append(host.concat(postPath));
        Lock lock = new ReentrantLock();
        lock.lock();
        int status;
        try {
            msg.setPredefinedKeyValue("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            String postBody = msg.getPostBody();
            log.info(postBody);
            String sign = DigestUtils.md5Hex(("POST".concat(urlSB.toString()).concat(postBody).concat(msg.getAppMasterSecret()).getBytes("UTF-8")));
            urlSB.append("?sign=".concat(sign));
            HttpResponse response = HttpsUtil.doPost(urlSB.toString(), getHeaders(), postBody);
            status = response.getStatusLine().getStatusCode();
            log.info(status + "==" + HttpsUtil.getData(response));
        } catch (Exception e) {
            log.info("发送请求失败:" + e.getMessage());
            status = HttpStatus.SC_BAD_REQUEST;
        } finally {
            lock.unlock();
        }
        log.info("Response Code : " + status);
        if (status == HttpStatus.SC_OK) {
            log.info("Notification sent successfully.");
        } else {
            log.info("Failed to send the notification!");
        }
        return true;
    }

    /**
     * Upload file with device_tokens to Umeng
     *
     * @param appkey          appkey
     * @param appMasterSecret 密钥
     * @param contents        上传内容
     * @return 上传结果
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2019/3/13 20:59
     */
    public static String uploadContents(String appkey, String appMasterSecret, String contents) throws Exception {
        // Construct the json string
        JSONObject uploadJson = new JSONObject();
        uploadJson.put("appkey", appkey);
        uploadJson.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        uploadJson.put("content", contents);
        // Construct the request
        StringBuffer urlSB = new StringBuffer();
        urlSB.append(host.concat(postPath));
        String postBody = uploadJson.toString();
        String sign = DigestUtils.md5Hex(("POST" + urlSB.toString() + postBody + appMasterSecret).getBytes("utf8"));
        urlSB.append("?sign=".concat(sign));
        HttpResponse response = HttpsUtil.doPost(urlSB.toString(), getHeaders(), postBody);
        String result = HttpsUtil.getData(response);
        log.info("upload is result" + result);
        // Decode response string and get file_id from it
        JSONObject respJson = new JSONObject(result);
        String ret = respJson.getString("ret");
        if (!ret.equals("SUCCESS")) {
            throw new Exception("Failed to upload file");
        }
        JSONObject data = respJson.getJSONObject("data");
        return data.getString("file_id");
    }
}
