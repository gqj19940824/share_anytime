package com.unity.resource.controller.feign;

import com.unity.common.base.controller.BaseWebController;
import com.unity.resource.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户信息API控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 */
@Slf4j
@RestController
@RequestMapping("feign/filefeign")
public class FileFeignController extends BaseWebController {

    private final FileService fileService;

    public FileFeignController(FileService fileService) {
        this.fileService = fileService;
    }


    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @author zhangxiaogang
     * @since 2019/2/18 19:30
     */
    @PostMapping("deleteFile")
    public void deleteFile(@RequestBody String filePath) {
        fileService.deleteFile(filePath);
    }

    /**
     * 批量删除文件
     *
     * @param filePaths 文件路径集合
     * @author zhangxiaogang
     * @since 2019/2/18 19:30
     */
    @PostMapping("deleteFileBatch")
    public void deleteFileBatch(@RequestBody List<String> filePaths) {
        fileService.deleteFileBatch(filePaths);
    }

    /**
     * 文件上传
     *
     * @param file 文件流
     * @author zhangxiaogang
     * @since 2019/2/18 19:30
     */
    @PostMapping("fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file) {
        //fastdfs 文件服务器地址
        return fileService.fileUpload(file, false).getFileUrl();
    }

    /**
     * 文件下载--获取文件流
     *
     * @param fileUrl 文件地址
     * @return 文件流
     * @author gengjiajia
     * @since 2019/10/11 14:45
     */
    @GetMapping("download")
    public byte[] download(@RequestParam(value = "fileUrl") String fileUrl) {
        return fileService.downloadFile(fileUrl);
    }
}