package com.unity.common.utils;

import com.sun.crypto.provider.TlsMasterSecretGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;


/**
 * 工具类针对获取url的结果进行处理
 * <p>
 * create by zhangxiaogang at 2018/7/28 15:45
 */
@Slf4j
public class HttpsUtil {

    /**
     * 创建https请求对象
     *
     * @return
     */
    private static CloseableHttpClient createSSLClientDefault(){
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 接受所有证书
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                    return true;
                }

                // 接受所有证书
                public boolean isTrusted(TlsMasterSecretGenerator[] chain, String authType)
                        throws java.security.cert.CertificateException {
                    return true;
                }

            }).build();
            SSLConnectionSocketFactory sslsf =
                    new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();

        } catch (Exception e) {
            //throw new Exception(SystemResponse.FormalErrorCode.SERVER_ERROR,"HTTP status exception : " + e.getMessage());
        }

        return HttpClients.createDefault();
    }

    /**
     * doGet请求的方法
     *
     * @param url url连接
     * @return HttpResponse 相应内容
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2018/7/28 15:40
     */
    public static HttpResponse doGet(String url) throws Exception {
        HttpResponse response;
        HttpGet get = new HttpGet();
        get.setURI(new URI(url));
        response = createSSLClientDefault().execute(get);
        return response;
    }

    /**
     * doGet请求的方法
     *
     * @param url     url连接
     * @param headers 请求头
     * @return HttpResponse 相应内容
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2018/7/28 15:40
     */
    public static HttpResponse doGet(String url, Map<String, String> headers) throws Exception {
        HttpResponse response;
        HttpGet get = new HttpGet(new URI(url));
        get.setHeaders(parseHeader(headers));
        response = createSSLClientDefault().execute(get);
        return response;
    }

    /**
     * post请求的方法
     *
     * @param url       url连接
     * @param headers   请求头
     * @param postParam 参数
     * @return HttpResponse 相应内容
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2018/7/28 15:40
     */
    public static HttpResponse doPost(String url, Map<String, String> headers, Map<String, String> postParam) throws Exception {
        HttpResponse response;
        HttpPost httpPost = new HttpPost(new URI(url));
        // 设置请求头
        httpPost.setHeaders(parseHeader(headers));
        Header[] allHeaders = httpPost.getAllHeaders();
        for (Header header : allHeaders) {
            log.info(header.getName() + ":" + header.getValue());
        }
        // 设置post请求参数
        httpPost.setEntity(new UrlEncodedFormEntity(parseParam(postParam), "UTF-8"));
        // 执行post请求
        response = createSSLClientDefault().execute(httpPost);

        return response;
    }

    public static HttpResponse doPost(String url, Map<String, String> headers, String postParam) throws Exception {
        HttpResponse response;
        HttpPost httpPost = new HttpPost(new URI(url));
        // 设置请求头
        httpPost.setHeaders(parseHeader(headers));
        // 设置post请求参数
        httpPost.setEntity(new StringEntity(postParam, "UTF-8"));
        // 执行post请求
        response = createSSLClientDefault().execute(httpPost);
        return response;
    }

    public static HttpResponse doPost(String url, Map<String, String> headers, InputStream is) throws Exception {
        HttpResponse response;
        HttpPost httpPost = new HttpPost(new URI(url));
        // 设置请求头
        httpPost.setHeaders(parseHeader(headers));
        // 设置post请求参数
        InputStreamEntity ise = new InputStreamEntity(is);
        ise.setContentType("binary/octet-stream");
        ise.setChunked(true);
        httpPost.setEntity(ise);
        // 执行post请求
        response = createSSLClientDefault().execute(httpPost);
        return response;
    }

    /**
     * 转换headers
     *
     * @param postParam 参数
     * @return 结果
     * @author zhangxiaogang
     * @since 2018/7/28 15:32
     */
    private static List<NameValuePair> parseParam(Map<String, String> postParam) {
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        for (String key : postParam.keySet()) {
            formParams.add(new BasicNameValuePair(key, postParam.get(key)));
        }
        return formParams;
    }

    /**
     * 转换headers
     *
     * @param headers 参数
     * @return 结果
     * @author zhangxiaogang
     * @since 2018/7/28 15:32
     */
    private static Header[] parseHeader(Map<String, String> headers) {
        if (headers == null) {
            throw new NullPointerException("HTTP headers not be null!");
        }
        Header[] allHeader = new BasicHeader[headers.size()];
        int i = 0;
        for (String str : headers.keySet()) {
            allHeader[i] = new BasicHeader(str, headers.get(str));
            i++;
        }
        return allHeader;
    }


    /**
     * 获取cookie
     *
     * @param response 响应内容
     * @return 结果
     * @author zhangxiaogang
     * @since 2018/7/28 15:32
     */
    public String getCookie(final HttpResponse response) throws Exception {
        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_OK) {
            Header[] headers2 = response.getHeaders("Set-cookie");
            for (Header header : headers2) {
                if (header.getValue().contains("sessionid")) {
                    return header.getValue();
                }
            }
        } else {
            throw new Exception();
        }
        return null;
    }

    /**
     * 读取响应数据
     *
     * @param response 响应内容
     * @return 结果
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2018/7/28 15:32
     */
    public static String getData(final HttpResponse response) throws Exception {
        String data = "";
        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new Exception( );
            } else {
                String contentCharSet = getContentCharSet(entity);
                data = EntityUtils.toString(entity, contentCharSet);
                EntityUtils.consume(entity);
            }
        } else if (status == 302 || status == 301) {
            return "302";
        } else {
            throw new Exception( );
        }
        return data;
    }

    /**
     * 读取响应文件流并将文件进行存档
     *
     * @param response 响应内容
     * @return 结果
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2018/7/28 15:32
     */
    public static String getDataForStream(final HttpResponse response) throws Exception {
        String data = "";
        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new Exception( );
            } else {
                String contentCharSet = getContentCharSet(entity);
                data = EntityUtils.toString(entity, contentCharSet);
            }
        } else {
            throw new Exception( );
        }
        return data;
    }

    /**
     * 读取响应文件流并转换string
     *
     * @param is 文件流
     * @return 结果
     * @throws IOException 异常
     * @author zhangxiaogang
     * @since 2018/7/28 15:32
     */
    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }


    /**
     * 设置响应数据编码
     *
     * @param entity 请求实体
     * @return 结果
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2018/7/28 15:32
     */
    private static String getContentCharSet(final HttpEntity entity) throws Exception {
        String charset = null;
        if (entity.getContentType() != null) {
            HeaderElement values[] = entity.getContentType().getElements();
            if (values.length > 0) {
                NameValuePair param = values[0].getParameterByName("charset");
                if (param != null) {
                    charset = param.getValue();
                }
            }
        }
        if (null == charset || "".equals(charset) || charset.length() == 0) {
            charset = "UTF-8";
        }
        return charset;
    }

    /**
     * 设置响应数据编码
     *
     * @param arrays 参数
     * @return 结果
     * @author zhangxiaogang
     * @since 2018/7/28 15:32
     */
    public byte[] encode(byte[] arrays) {
        ByteArrayOutputStream localByteArrayOutputStream = null;
        try {
            localByteArrayOutputStream = new ByteArrayOutputStream();

            GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);

            byte[] arrayOfByte1 = arrays;
            localGZIPOutputStream.write(arrayOfByte1);
            localGZIPOutputStream.close();
            byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
            return arrayOfByte2;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localByteArrayOutputStream != null) {
                try {
                    localByteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
