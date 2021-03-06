package com.unity.innovation.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/***
 * 将文件内容响应到浏览器
 */
@Slf4j
public class DownloadUtil {

    /**
     * 文件的内容类型
     */
    private static String getFileContentType(String name){
        String result = "";
        String fileType = name.toLowerCase();
        if (fileType.endsWith(".png")) {
            result = "image/png";
        } else if (fileType.endsWith(".gif")) {
            result = "image/gif";
        } else if (fileType.endsWith(".jpg") || fileType.endsWith(".jpeg")) {
            result = "image/jpeg";
        } else if(fileType.endsWith(".svg")){
            result = "image/svg+xml";
        }else if (fileType.endsWith(".doc")) {
            result = "application/msword";
        } else if (fileType.endsWith(".xls")) {
            result = "application/x-excel";
        } else if (fileType.endsWith(".zip")) {
            result = "application/zip";
        } else if (fileType.endsWith(".pdf")) {
            result = "application/pdf";
        } else {
            result = "application/octet-stream";
        }
        return result;
    }

    /**
     * 下载文件
     * @param file 下载的文件
     * @param fileName 自定义下载文件的名称
     * @param resp http响应
     * @param req http请求
     */
    public static void downloadFile(File file, String fileName, HttpServletResponse resp, HttpServletRequest req){
        String charsetCode = "utf-8";
        try {
            /**
             * 中文乱码解决
             */
            String type = req.getHeader("User-Agent").toLowerCase();
            if(type.indexOf("firefox")>0 || type.indexOf("chrome")>0){
                //  谷歌或火狐
                fileName = new String(fileName.getBytes(charsetCode), "iso8859-1");
            }else{
                // IE
                fileName = URLEncoder.encode(fileName, charsetCode);
            }
            // 设置响应的头部信息
            resp.setHeader("content-disposition", "attachment;filename=" + fileName);
            // 设置响应内容的类型
            resp.setContentType(getFileContentType(fileName)+"; charset=" + charsetCode);
            // 设置响应内容的长度
            resp.setContentLength((int) file.length());
            // 输出
            outStream(new FileInputStream(file), resp.getOutputStream());
        } catch (Exception e) {
            log.error("执行downloadFile发生了异常：" + e.getMessage(), e);
        }
    }

    /**
     * 基础字节数组输出
     */
    private static void outStream(InputStream is, OutputStream os) {
        try {
            byte[] buffer = new byte[10240];
            int length;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
                os.flush();
            }
        } catch (Exception e) {
            log.error("执行 outStream 发生了异常1：" + e.getMessage(), e);
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                log.error("执行 outStream 发生了异常2：" + e.getMessage(), e);
            }
            try {
                is.close();
            } catch (IOException e) {
                log.error("执行 outStream 发生了异常3：" + e.getMessage(), e);
            }
        }
    }

}
