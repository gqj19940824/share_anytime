package com.unity.resource.controller;

import com.google.common.collect.Lists;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.DateUtils;
import com.unity.resource.entity.FastdfsFileDTO;
import com.unity.resource.entity.FileDTO;
import com.unity.resource.entity.FileResource;
import com.unity.resource.service.FileService;
import com.unity.resource.service.WebUploader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/server")
public class FileController extends BaseWebController {
    private final SystemConfiguration systemConfiguration;
    private final FileService fileService;
    private final WebUploader wu;

    public FileController(SystemConfiguration systemConfiguration, FileService fileService, WebUploader wu) {
        this.systemConfiguration = systemConfiguration;
        this.fileService = fileService;
        this.wu = wu;
    }


    /**
     * 大文件上传
     *
     * @param status 文件状态
     * @param info   文件信息
     * @param file   上传文件
     * @return 上传结果
     * @author zhangxiaogang
     * @since 2019/1/19 15:29
     */
    @PostMapping(value = "fileUpload")
    public String fileUpload(String status, FileDTO info, @RequestParam(value = "file", required = false) MultipartFile file) {
        log.info("uploadStatus--"+status);
        String uploadFolder = systemConfiguration.getMultipartPath() + File.separator + "fileList";
        if (status == null) {    //文件上传
            if (file != null && !file.isEmpty()) {    //验证请求不会包含数据上传，所以避免NullPoint这里要检查一下file变量是否为null
                try {
                    File target = wu.getReadySpace(info, uploadFolder);    //为上传的文件准备好对应的位置
                    if (target == null) {
                        return "{\"status\": 0, \"message\": \"" + wu.getErrorMsg() + "\"}";
                    }
                    file.transferTo(target);    //保存上传文件
                    //将MD5签名和合并后的文件path存入持久层，注意这里这个需求导致需要修改webuploader.js源码3170行
                    //因为原始webuploader.js不支持为formData设置函数类型参数，这将导致不能在控件初始化后修改该参数
                    //log.info("info---getChunks--"+info.getChunks());
                    if (info.getChunks() <= 0) {
                        return wu.dealUploadFileResult(uploadFolder, target.getName(), info);
                    }
                    return "{\"status\": 1, \"path\": \"" + target.getName() + "\"，\"md5\":\"" + info.getMd5() + "\"}";

                } catch (IOException ex) {
                    log.error("数据上传失败", ex);
                    return "{\"status\": 0, \"message\": \"数据上传失败\"}";
                }
            }
        } else {
            switch (status) {
                case "md5Check": {
                    log.info("=======md5======="+info.getMd5());
                    FileResource fileResource = wu.md5Check(info.getMd5());
                    if (fileResource == null) {
                        return "{\"ifExist\": 0}";
                    } else {
                        return "{\"ifExist\": 1, \"path\": \"" + fileResource.getUrl() + "\"}";
                    }
                }
                case "chunkCheck": {
                    //检查目标分片是否存在且完整
                    if (wu.chunkCheck(uploadFolder + "/" + info.getName() + "/" + info.getChunkIndex(), Long.valueOf(info.getSize()))) {
                        return "{\"ifExist\": 1}";
                    } else {
                        return "{\"ifExist\": 0}";
                    }
                }
                case "chunksMerge": {//合并切片
                    String path = wu.chunksMerge(info.getName(), info.getExt(), info.getChunks(), info.getMd5(), uploadFolder);
                    return wu.dealUploadFileResult(uploadFolder, path, info);

                }
            }
        }

        log.error("请求参数不完整");
        return "{\"status\": 0, \"message\": \"请求参数不完整\"}";
    }



    /**
     * jar文件上传(用于远程jar更新部署)
     *
     * @param status 文件状态
     * @param info   文件信息
     * @param file   上传文件
     * @return 上传结果
     * @author zhangxiaogang
     * @since 2019/1/19 15:29
     */
    @PostMapping(value = "jarFileUpload")
    public String jarFileUpload(String status, FileDTO info, @RequestParam(value = "file", required = false) MultipartFile file) {
        log.info("uploadStatus--"+status);
        String jarFilePath = systemConfiguration.getMultipartPath() + "fileListJar";
        wu.createFileFolder(jarFilePath, false);
        String uploadFolder = jarFilePath + File.separator + DateUtils.getCurrentDate("-") + File.separator ;
        if (status == null) {    //文件上传
            if (file != null && !file.isEmpty()) {    //验证请求不会包含数据上传，所以避免NullPoint这里要检查一下file变量是否为null
                try {
                    File target = wu.getReadySpace(info, uploadFolder);    //为上传的文件准备好对应的位置
                    if (target == null) {
                        return "{\"status\": 0, \"message\": \"" + wu.getErrorMsg() + "\"}";
                    }
                    file.transferTo(target);    //保存上传文件
                    //将MD5签名和合并后的文件path存入持久层，注意这里这个需求导致需要修改webuploader.js源码3170行
                    //因为原始webuploader.js不支持为formData设置函数类型参数，这将导致不能在控件初始化后修改该参数
                    //log.info("info---getChunks--"+info.getChunks());
                    if (info.getChunks() <= 0) {
                        wu.saveMd52FileMap(info.getMd5(), target.getAbsolutePath());
                        return "{\"status\": 1, \"path\": \"" + target.getAbsolutePath() + "\",\"md5\":\""+ info.getMd5() + "\", \"message\": \"文件上传\"}";
                    }
                    return "{\"status\": 1, \"path\": \"" + target.getName() + "\"，\"md5\":\"" + info.getMd5() + "\"}";

                } catch (IOException ex) {
                    log.error("数据上传失败", ex);
                    return "{\"status\": 0, \"message\": \"数据上传失败\"}";
                }
            }
        } else {
            switch (status) {
                case "md5Check": {
                    log.info("=======md5======="+info.getMd5());
                    FileResource fileResource = wu.md5Check(info.getMd5());
                    if (fileResource == null) {
                        return "{\"ifExist\": 0}";
                    } else {
                        return "{\"ifExist\": 1, \"path\": \"" + fileResource.getUrl() + "\"}";
                    }
                }
                case "chunkCheck": {
                    //检查目标分片是否存在且完整
                    if (wu.chunkCheck(uploadFolder + "/" + info.getName() + "/" + info.getChunkIndex(), Long.valueOf(info.getSize()))) {
                        return "{\"ifExist\": 1}";
                    } else {
                        return "{\"ifExist\": 0}";
                    }
                }
                case "chunksMerge": {//合并切片
                    String fileName = wu.chunksMerge(info.getName(), info.getExt(), info.getChunks(), info.getMd5(), uploadFolder);
                    String filePath = uploadFolder.concat(fileName);
                    log.info("======filePath====="+filePath);
                    wu.saveMd52FileMap(info.getMd5(), filePath);
                    return "{\"status\": 1, \"path\": \"" + filePath + "\",\"md5\":\""+ info.getMd5() + "\", \"message\": \"文件上传\"}";
                }
                default:{
                    return "{\"status\": 0, \"message\": \"数据上传失败\"}";
                }
            }
        }
        log.error("请求参数不完整");
        return "{\"status\": 0, \"message\": \"请求参数不完整\"}";
    }

    /**
     * 根据md5值获取url
     *
     * @param md5 文件对应md5值
     * @return 文件信息
     * @author zhangxiaogang
     * @since 2019/1/26 16:51
     */
    @GetMapping("findBigFileUrl")
    public Mono<ResponseEntity<SystemResponse<Object>>> findBigFileUrl(@RequestParam(value = "md5", required = false) String md5) {
        //fastdfs 文件服务器地址
        if (StringUtils.isEmpty(md5)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到要上传的文件成功返回的md5值");
        }
        try {
            return success(wu.md5Check(md5));
        } catch (UnityRuntimeException e) {
            return error(e.getCode(), e.getMessage());
        }
    }

    /**
     * 上传文件 到fastdfs文件服务器
     *
     * @param file 需要上传的文件
     * @param flag 是否上传缩略图
     * @return 保存后的文件地址，文件组名
     * @author Jung
     * @since 2018年06月16日12:17:42
     */
    @PostMapping("upload")
    public Mono<ResponseEntity<SystemResponse<Object>>> upload(@RequestParam(required = false) MultipartFile file,@RequestParam(required = false, defaultValue = "false") boolean flag) {
        //fastdfs 文件服务器地址
        if (file == null || file.isEmpty()) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到要上传的文件");
        }
        try {
            return success(fileService.fileUpload(file, flag));
        } catch (UnityRuntimeException e) {
            return error(e.getCode(), e.getMessage());
        }
    }

    /**
     * 上传文件 到fastdfs文件服务器
     *
     * @param fileList 需要上传的文件
     * @return 保存后的文件地址，文件组名
     * @author Jung
     * @since 2018年06月16日12:17:42
     */
    @PostMapping("uploadBatch")
    public Mono<ResponseEntity<SystemResponse<Object>>> uploadBatch(@RequestParam(required = false) List<MultipartFile> fileList) {
        //fastdfs 文件服务器地址
        if (fileList == null || fileList.isEmpty()) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到要上传的文件");
        }
        List<FastdfsFileDTO> resulList = Lists.newArrayList();
        try {
            for (MultipartFile file : fileList) {
                resulList.add(fileService.fileUpload(file,false));
            }
            return success(resulList);
        } catch (UnityRuntimeException e) {
            if (!resulList.isEmpty()) {
                try {
                    fileService.deleteFileBatch(resulList.parallelStream()
                            .map(FastdfsFileDTO::getFileUrl)
                            .collect(Collectors.toList()));
                } catch (UnityRuntimeException e1) {
                    log.info("批量文件上传异常");
                }
            }
            return error(e.getCode(), e.getMessage());
        }
    }

    /**
     * 下载文件 fastdfs文件服务器
     *
     * @param fileUrl  文件路径
     * @param fileName 文件原名称
     * @author gengjiajia
     * @since 2018/09/20 09:50
     */
    @GetMapping("download")
    public Mono<ResponseEntity<byte[]>> download(@RequestParam(value = "fileUrl", required = false) String fileUrl, @RequestParam(value = "fileName", required = false) String fileName) throws UnityRuntimeException {
        if (StringUtils.isEmpty(fileUrl) || StringUtils.isEmpty(fileName)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("缺少下载参数")
                    .build();
        }
        byte[] content;
        HttpHeaders headers = new HttpHeaders();
        try {
            content = fileService.downloadFile(fileUrl);
            headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("UTF-8"), "iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } catch (Exception e) {
            throw UnityRuntimeException.newInstance()
                    .message(e.getMessage())
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .build();
        }
        return Mono.just(new ResponseEntity<>(content, headers, HttpStatus.CREATED));
    }

    /**
     * 批量下载文件 fastdfs文件服务器
     *
     * @param fileList 文件路径
     * @author gengjiajia
     * @since 2018/09/20 09:50
     */
    @PostMapping("downloadBatch")
    public Mono<ResponseEntity<byte[]>> downloadBatch(@RequestBody List<FastdfsFileDTO> fileList) throws UnityRuntimeException {
        if (fileList.isEmpty() || !fileList.parallelStream()
                .filter(fastdfsFileDTO -> fastdfsFileDTO.valid() != null)
                .collect(Collectors.toList())
                .isEmpty()) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("缺少下载参数")
                    .build();
        }
        byte[] content;
        HttpHeaders headers = new HttpHeaders();
        try {
            content = fileService.downloadBatch(fileList);
            headers.setContentDispositionFormData("attachment", new String((DateUtils.getDate("yyyyMMddHHmmss") + ".zip").getBytes("UTF-8"), "iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } catch (Exception e) {
            throw UnityRuntimeException.newInstance()
                    .message(e.getMessage())
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .build();
        }
        return Mono.just(new ResponseEntity<>(content, headers, HttpStatus.CREATED));
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件路径
     * @return code 0 success
     * @author zhangxiaogang
     * @since 2019/1/8 17:40
     */
    @GetMapping("delete")
    public Mono<ResponseEntity<SystemResponse<Object>>> delete(@RequestParam(value = "fileUrl", required = false) String fileUrl) {
        //fastdfs 文件服务器地址
        if (StringUtils.isEmpty(fileUrl)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到要删除的文件");
        }
        try {
            fileService.deleteFile(fileUrl);
            return success();
        } catch (UnityRuntimeException e) {
            return error(e.getCode(), e.getMessage());
        }
    }

    /**
     * 批量删除文件
     *
     * @param fileUrlList 文件路径
     * @return code 0 success
     * @author zhangxiaogang
     * @since 2019/1/8 17:40
     */
    @PostMapping("deleteBatch")
    public Mono<ResponseEntity<SystemResponse<Object>>> deleteBatch(@RequestBody List<String> fileUrlList) {
        try {
            fileService.deleteFileBatch(fileUrlList);
            return success();
        } catch (UnityRuntimeException e) {
            return error(e.getCode(), e.getMessage());
        }

    }


}
