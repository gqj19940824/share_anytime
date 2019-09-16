package com.unity.resource.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.exception.FdfsServerException;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.google.common.collect.Lists;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.DateUtils;
import com.unity.common.util.GsonUtils;
import com.unity.resource.entity.FastdfsFileDTO;
import com.unity.resource.util.FileReaderUtil;
import com.unity.resource.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 处理fastdfs上传文件
 * <p>
 * create by zhangxiaogang at 2018/10/27 11:10
 */
@Slf4j
@Service
public class FileService {

    private final SystemConfiguration systemConfiguration;
    private final FastFileStorageClient dfsClient;

    public FileService(SystemConfiguration systemConfiguration, FastFileStorageClient dfsClient) {
        this.systemConfiguration = systemConfiguration;
        this.dfsClient = dfsClient;
    }

    /**
     * 单文件上传
     *
     * @param file 上传文件
     * @return 上传结果
     * @throws UnityRuntimeException 异常
     * @author zhangxiaogang
     * @since 2019/1/8 16:00
     */
    public FastdfsFileDTO fileUpload(MultipartFile file, boolean flag){
        String fileType = FilenameUtils.getExtension(file.getOriginalFilename().toLowerCase());
        FastdfsFileDTO fastdfsFileDTO;
        try {
            fastdfsFileDTO = uploadFastDFSFile(file.getInputStream(), file.getSize(), fileType, file.getOriginalFilename());
            if (flag) {
                String newPath = systemConfiguration.getUploadPath() + File.separator + ImageUtils.DEFAULT_PREVFIX + file.getOriginalFilename();
                File files = new File(newPath);
                FileReaderUtil.inputStreamToFile(file.getInputStream(), files);
                ImageUtils.thumbnailImage(files, newPath, 113, 100, ImageUtils.DEFAULT_FORCE);
                fastdfsFileDTO.setThumbUrl(fileUploadFastDfs(newPath));
                if (files.exists()) {
                    files.delete();
                }
            }
            log.info("文件上传成功："+ GsonUtils.format(fastdfsFileDTO));
        } catch (Exception e) {
            e.printStackTrace();
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .message("文件上传异常")
                    .build();
        }
        return fastdfsFileDTO;
    }

    /**
     * 跨服文件上传
     *
     * @param file       文件流
     * @return 文件地址
     * @throws UnityRuntimeException 异常
     * @author zhangxiaogang
     * @since 2019/3/1 14:45
     */
    public String fileFeignUpload(File file){
        FastdfsFileDTO fastdfsFileDTO;
        try {
            String fileType = FilenameUtils.getExtension(file.getName().toLowerCase());
            fastdfsFileDTO = uploadFastDFSFile(new FileInputStream(file), file.length(), fileType, file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .message("文件上传异常")
                    .build();
        }
        return fastdfsFileDTO.getFileUrl();
    }

    /**
     * @param filePath 文件路径
     * @return fastdfs文件路径
     * @author zhangxiaogang
     * @since 2019/2/27 17:41
     */
    private String fileUploadFastDfs(String filePath) {
        InputStream fileInputStream = null;
        File newFile = new File(filePath);
        try {
            fileInputStream = new FileInputStream(newFile);
            String fileType = FilenameUtils.getExtension(newFile.getName().toLowerCase());
            FastdfsFileDTO fastdfsFileDTO = uploadFastDFSFile(fileInputStream, newFile.length(), fileType, newFile.getName());
            return fastdfsFileDTO.getFileUrl();
        } catch (Exception e) {
            log.info("文件上传异常：" + e.getMessage());
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.info("关闭流异常");
                }
            }
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        return null;
    }


    /**
     * 文件上传
     *
     * @param is               文件流
     * @param size             文件大小
     * @param fileType         文件类型
     * @param fileOrganizeName 文件名称
     * @return 文件地址
     * @author zhangxiaogang
     * @since 2019/1/25 15:20
     */
    public FastdfsFileDTO uploadFastDFSFile(InputStream is, Long size, String fileType, String fileOrganizeName) throws IOException {
        String fastdfsFileReadPathHead = systemConfiguration.getFastdfsFileReadPathHead();
        StorePath path = dfsClient.uploadFile(is, size, fileType, null);
        //fast 服务器文件相对地址 不包含服务器地址 需要拼接上服务器地址
        if (is != null) {
            is.close();
        }
        String fullPath = path.getFullPath();
        fullPath = fullPath.replace(path.getGroup() + "/", "");
        return FastdfsFileDTO.newInstance()
                .filePath(fullPath)
                .fileUrl(fastdfsFileReadPathHead + path.getFullPath())
                .groupName(path.getGroup())
                .fileName(fileOrganizeName)
                .build();
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件参数
     * @return 文件二进制内容
     * @author zhangxiaogang
     * @since 2019/1/8 16:10
     */
    public byte[] downloadFile(String fileUrl) throws UnityRuntimeException {
        String fastdfsFileReadPathHead = systemConfiguration.getFastdfsFileReadPathHead();
        //String fastdfsFileReadPathHead = "http://document.jingcaiwang.cn/";
        if (StringUtils.isNotBlank(fastdfsFileReadPathHead)) {
            String groupUrl = fileUrl.substring(fastdfsFileReadPathHead.length(), fileUrl.length());
            if (groupUrl.contains("/")) {
                String[] groupArray = groupUrl.split("/");
                String groupName = groupArray[0];
                String filePath = groupUrl.substring(groupName.length() + 1, groupUrl.length());
                log.info(groupName + "==========" + filePath);
                try {
                    DownloadByteArray callback = new DownloadByteArray();
                    return dfsClient.downloadFile(groupName, filePath, callback);
                } catch (Exception e) {
                    throw UnityRuntimeException.newInstance().message(e.getMessage())
                            .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                            .build();
                }
            }
        }
        return null;
    }

    /**
     * 批量下载文件
     *
     * @param fastdfsFileDTOList 文件参数
     * @return 文件压缩内容
     * @throws UnityRuntimeException 异常
     * @author zhangxiaogang
     * @since 2019/1/8 16:48
     */
    public byte[] downloadBatch(List<FastdfsFileDTO> fastdfsFileDTOList) throws UnityRuntimeException {
        String systemPath = systemConfiguration.getMultipartPath() + File.separator + "liabilityList" + File.separator;
        File dir = new File(systemPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        List<String> filePathList = Lists.newArrayList();
        for (FastdfsFileDTO fastdfsFileDTO : fastdfsFileDTOList) {
            byte[] bytes = downloadFile(fastdfsFileDTO.getFileUrl());
            FileReaderUtil.byte2File(bytes, systemPath, fastdfsFileDTO.getFileName());
            filePathList.add(systemPath + fastdfsFileDTO.getFileName());
        }
        String zipFileName = DateUtils.getDate("yyyyMMddHHmmss") + ".zip";
        String zipFilePath = systemPath + zipFileName;
        //处理文件下载工具
        byte[] content;
        try {
            content = FileReaderUtil.dealZIPFiles(filePathList, zipFilePath);
            new File(zipFilePath).delete();
        } catch (Exception e) {
            throw UnityRuntimeException.newInstance().message("文件压缩异常")
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .build();
        }
        return content;
    }


    /**
     * 根据文件路径删除服务器文件
     *
     * @param pathUrl 文件路径
     * @throws UnityRuntimeException 异常
     * @author zhangxiaogang
     * @since 2018/10/27 11:29
     */
    public void deleteFile(String pathUrl) throws UnityRuntimeException {
        String fastdfsFileReadPathHead = systemConfiguration.getFastdfsFileReadPathHead();
        //String fastdfsFileReadPathHead = "http://document.jingcaiwang.cn/";
        if (StringUtils.isNotBlank(fastdfsFileReadPathHead)) {
            String groupUrl = pathUrl.substring(fastdfsFileReadPathHead.length(), pathUrl.length());
            try {
                dfsClient.deleteFile(groupUrl);
            } catch (FdfsServerException e) {
                throw UnityRuntimeException.newInstance().message(e.getMessage())
                        .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                        .build();
            }
        } else {
            throw UnityRuntimeException.newInstance().message("未发现要删除的文件")
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .build();
        }
    }

    /**
     * 批量删除文件
     *
     * @param fileList 文件列表
     * @author zhangxiaogang
     * @since 2019/1/8 17:07
     */
    public void deleteFileBatch(List<String> fileList) throws UnityRuntimeException {
        if (!fileList.isEmpty()) {
            for (String fileUrl : fileList) {
                this.deleteFile(fileUrl);
            }
        }
    }


}
