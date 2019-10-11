package com.unity.common.utils;

import com.unity.common.client.ReClient;
import com.unity.common.pojos.FileDownload;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.util.FileReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件下载工具类
 * <p>
 * create by gengjiajia at 2019/10/11 15:15
 */
@Slf4j
@Component
public class FileDownloadUtil {

    @Resource
    ReClient reClient;
    @Resource
    SystemConfiguration systemConfiguration;

    private static FileDownloadUtil fileDownloadUtil;

    @PostConstruct
    public void init(){
        fileDownloadUtil = this;
        fileDownloadUtil.reClient = this.reClient;
        fileDownloadUtil.systemConfiguration = this.systemConfiguration;
    }

    public static ResponseEntity<byte[]> downloadFileToZip(List<FileDownload> fileInfo,String zipFileName){
        //创建文件存放临时目录
        String systemPath = fileDownloadUtil.systemConfiguration.getMultipartPath() + File.separator + System.currentTimeMillis() + File.separator;
        File dir = new File(systemPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        HttpHeaders headers = new HttpHeaders();
        byte[] content = null;
        try {
            String zipFilePath = systemPath + zipFileName;
            //处理文件下载工具
            content = dealZIPFiles(fileInfo, zipFilePath);
            new File(zipFilePath).delete();
            headers.setContentDispositionFormData("attachment", new String(zipFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } catch (Exception e) {
            log.info("---压缩文件处理异常:" + e.getMessage());
        } finally {
            if (dir.exists()) {
                dir.delete();
            }
        }
        return new ResponseEntity<>(content, headers, HttpStatus.CREATED);
    }

    /**
     * 处理文件下载工具
     *
     * @param fileList 文件列表
     * @param zipFile  压缩包名称
     * @return 字节数组
     * @throws Exception 异常
     * @author zhangxiaogang
     * @since 2018/10/17 19:09
     */
    private static byte[] dealZIPFiles(List<FileDownload> fileList, String zipFile) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        try {
            for (FileDownload fileInfo : fileList) {
                byte[] bytes = fileDownloadUtil.reClient.download(fileInfo.getUrl());
                zos.putNextEntry(new ZipEntry(fileInfo.getName()));
                zos.write(bytes);
            }
        } catch (Exception e) {
            log.info("--文件压缩异常：" + e.getMessage());
        } finally {
            zos.close();
        }
        return FileReaderUtil.getBytes(new File(zipFile));
    }
}
