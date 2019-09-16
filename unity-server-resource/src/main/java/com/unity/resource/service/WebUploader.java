package com.unity.resource.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.resource.entity.FastdfsFileDTO;
import com.unity.resource.entity.FileDTO;
import com.unity.resource.entity.FileResource;
import com.unity.resource.util.FileLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static java.awt.SystemColor.info;

/**
 * 文件上传处理
 * <p>
 * <p>
 * create by zhangxiaogang at 2019/1/19 15:49
 */
@Service
@Scope("prototype")
@Slf4j
public class WebUploader {


    //错误详情
    private String msg;
    private final FileResourceServiceImpl fileResourceService;
    private final FileService fileService;

    public WebUploader(FileResourceServiceImpl fileResourceService, FileService fileService) {
        this.fileResourceService = fileResourceService;
        this.fileService = fileService;
    }


    /**
     * 处理文件上传后将文件上传至服务器
     *
     * @param uploadFolder 文件上传路径
     * @param fileName     文件名称
     * @param info         文件信息
     * @return 上传结果
     */
    public String dealUploadFileResult(String uploadFolder, String fileName, FileDTO info) {
        String filePath = uploadFolder + "/" + fileName;
        FastdfsFileDTO fastdfsFileDTO;
        InputStream fileInputStream = null;
        File newFile = new File(filePath);
        try {
            fileInputStream = new FileInputStream(newFile);
            String fileType = FilenameUtils.getExtension(fileName.toLowerCase());
            fastdfsFileDTO = fileService.uploadFastDFSFile(fileInputStream, newFile.length(), fileType, fileName);
            saveMd52FileMap(info.getMd5(), fastdfsFileDTO.getFileUrl());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("文件上传异常：" + e.getMessage());
            return "{\"status\": 0, \"message\": \"" + getErrorMsg() + "\"}";
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
        return "{\"status\": 1, \"path\": \"" + fastdfsFileDTO.getFileUrl() + "\",\"md5\":\""+ info.getMd5() + "\", \"message\": \"文件上传\"}";
    }

    /**
     * 秒传验证
     * 根据文件的MD5签名判断该文件是否已经存在
     *
     * @param key 文件的md5签名
     * @return 若存在则返回该文件的路径，不存在则返回null
     * @author zhangxiaogang
     * @since 2019/1/25 14:39
     */
    public FileResource md5Check(String key) {
        QueryWrapper<FileResource> fileResourceQueryWrapper = new QueryWrapper<>();
        fileResourceQueryWrapper.lambda()
                .eq(FileResource::getMd5, key);
        return fileResourceService.getOne(fileResourceQueryWrapper);
    }

    /**
     * 分片验证
     * 验证对应分片文件是否存在，大小是否吻合
     *
     * @param file 分片文件的路径
     * @param size 分片文件的大小
     * @return 分片验证结果
     * @author zhangxiaogang
     * @since 2019/1/19 15:41
     */
    public boolean chunkCheck(String file, Long size) {
        //检查目标分片是否存在且完整
        File target = new File(file);
        if (target.isFile() && size == target.length()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 分片合并操作
     * 要点:
     * > 合并: NIO
     * > 并发锁: 避免多线程同时触发合并操作
     * > 清理: 合并清理不再需要的分片文件、文件夹、tmp文件
     *
     * @param folder 分片文件所在的文件夹名称
     * @param ext    合并后的文件后缀名
     * @param chunks 分片总数
     * @param md5    文件签名
     * @param path   合并后的文件所存储的位置
     * @return 合并后的目标文件
     * @author zhangxiaogang
     * @since 2019/1/19 15:42
     */
    public String chunksMerge(String folder, String ext, int chunks, String md5, String path) {

        //合并后的目标文件
        FileResource target;
        //检查是否满足合并条件：分片数量是否足够
        if (chunks == this.getChunksNum(path + "/" + folder)) {
            //同步指定合并的对象
            Lock lock = FileLock.getLock(folder);
            lock.lock();
            try {
                //检查是否满足合并条件：分片数量是否足够
                //File[] files = this.getChunks(path + "/" +folder);
                List<File> files = Arrays.asList(this.getChunks(path + "/" + folder));
                if (chunks == files.size()) {
                    //按照名称排序文件，这里分片都是按照数字命名的
                    files = files.parallelStream()
                            .sorted((File f1, File f2) -> Integer.valueOf(f1.getName()).compareTo(Integer.valueOf(f2.getName())))
                            .collect(Collectors.toList());

                    //创建合并后的文件
                    File outputFile = new File(path + "/" + this.randomFileName(ext));
                    if (outputFile.exists()) {
                        log.error("文件[" + folder + "]随机命名冲突");
                        this.setErrorMsg("文件随机命名冲突");
                        return null;
                    }
                    outputFile.createNewFile();
                    FileChannel outChannel = new FileOutputStream(outputFile).getChannel();

                    //合并
                    FileChannel inChannel;
                    for (File file : files) {
                        inChannel = new FileInputStream(file).getChannel();
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                        inChannel.close();
                        //删除分片
                        if (!file.delete()) {
                            log.error("分片[" + folder + "=>" + file.getName() + "]删除失败");
                        }
                    }
                    outChannel.close();

                    //将MD5签名和合并后的文件path存入持久层
                    /*if (!this.saveMd52FileMap(md5, outputFile.getName())) {
                        log.error("文件[" + md5 + "=>" + outputFile.getName() + "]保存关系到持久成失败，但并不影响文件上传，只会导致日后该文件可能被重复上传而已");
                    }*/

                    //清理：文件夹，tmp文件
                    this.cleanSpace(folder, path);

                    return outputFile.getName();
                }
            } catch (Exception ex) {
                log.error("数据分片合并失败", ex);
                this.setErrorMsg("数据分片合并失败");
                return null;

            } finally {
                //解锁
                lock.unlock();
                //清理锁对象
                FileLock.removeLock(folder);
            }
        }

        //去持久层查找对应md5签名，直接返回对应path
        target = this.md5Check(md5);
        if (target == null) {
            log.error("文件[签名:" + md5 + "]数据不完整，可能该文件正在合并中");
            this.setErrorMsg("数据不完整，可能该文件正在合并中");
            return null;
        }
        return target.getUrl();
    }

    /**
     * 将MD5签名和目标文件path的映射关系存入持久层
     *
     * @param key      md5签名
     * @param filePath 文件路径
     * @return 数据库保存结果
     * @author zhangxiaogang
     * @since 2019/1/19 15:44
     */
    public boolean saveMd52FileMap(String key, String filePath) {
        FileResource fileResource = FileResource.newInstance().build();
        fileResource.setUrl(filePath);
        fileResource.setMd5(key);
        FileResource fileResourceOld = md5Check(key);
        boolean result;
        if (fileResourceOld == null) {
            result = fileResourceService.save(fileResource);
        } else {
            fileResourceOld.setUrl(filePath);
            result = fileResourceService.updateById(fileResourceOld);
        }
        return result;
    }

    /**
     * 为上传的文件创建对应的保存位置
     * 若上传的是分片，则会创建对应的文件夹结构和tmp文件
     *
     * @param info 上传文件的相关信息
     * @param path 文件保存根路径
     * @return 文件信息
     */
    public File getReadySpace(FileDTO info, String path) {

        //创建上传文件所需的文件夹
        if (!this.createFileFolder(path, false)) {
            return null;
        }

        String newFileName;    //上传文件的新名称

        //如果是分片上传，则需要为分片创建文件夹
        if (info.getChunks() > 0) {
            newFileName = String.valueOf(info.getChunk());

            String fileFolder = this.md5(info.getUserId() + info.getName() + info.getType() + info.getLastModifiedDate() + info.getSize());
            if (fileFolder == null) {
                return null;
            }

            path += "/" + fileFolder;    //文件上传路径更新为指定文件信息签名后的临时文件夹，用于后期合并

            if (!this.createFileFolder(path, true)) {
                return null;
            }

        } else {
            //生成随机文件名
            newFileName = this.randomFileName(info.getName());
        }

        return new File(path, newFileName);
    }

    /**
     * 清理分片上传的相关数据
     * 文件夹，tmp文件
     *
     * @param folder 文件夹名称
     * @param path   上传文件根路径
     * @return 删除结果
     * @author zhangxiaogang
     * @since 2019/1/19 15:34
     */
    private boolean cleanSpace(String folder, String path) {
        //删除分片文件夹
        File garbage = new File(path + "/" + folder);
        if (!garbage.delete()) {
            return false;
        }

        //删除tmp文件
        garbage = new File(path + "/" + folder + ".tmp");
        if (!garbage.delete()) {
            return false;
        }

        return true;
    }

    /**
     * 获取指定文件的所有分片
     *
     * @param folder 文件夹路径
     * @return 文件列表
     * @author zhangxiaogang
     * @since 2019/1/19 15:35
     */
    private File[] getChunks(String folder) {
        File targetFolder = new File(folder);
        return targetFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return false;
                }
                return true;
            }
        });
    }

    /**
     * 获取指定文件的分片数量
     *
     * @param folder 文件夹路径
     * @return 文件数量
     * @author zhangxiaogang
     * @since 2019/1/19 15:35
     */
    private int getChunksNum(String folder) {
        File[] filesList = this.getChunks(folder);
        return filesList.length;
    }

    /**
     * 创建存放上传的文件的文件夹
     *
     * @param file 文件夹路径
     * @return 创建结果
     * @author zhangxiaogang
     * @since 2019/1/19 15:35
     */
    public boolean createFileFolder(String file, boolean hasTmp) {

        //创建存放分片文件的临时文件夹
        File tmpFile = new File(file);
        if (!tmpFile.exists()) {
            try {
                tmpFile.mkdir();
            } catch (SecurityException ex) {
                log.error("无法创建文件夹", ex);
                this.setErrorMsg("无法创建文件夹");
                return false;
            }
        }

        if (hasTmp) {
            //创建一个对应的文件，用来记录上传分片文件的修改时间，用于清理长期未完成的垃圾分片
            tmpFile = new File(file + ".tmp");
            if (tmpFile.exists()) {
                tmpFile.setLastModified(System.currentTimeMillis());
            } else {
                try {
                    tmpFile.createNewFile();
                } catch (IOException ex) {
                    log.error("无法创建tmp文件", ex);
                    this.setErrorMsg("无法创建tmp文件");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 为上传的文件生成随机名称
     *
     * @param originalName 文件的原始名称，主要用来获取文件的后缀名
     * @return 临时文件名称
     * @author zhangxiaogang
     * @since 2019/1/19 15:36
     */
    private String randomFileName(String originalName) {
        String ext[] = originalName.split("\\.");
        return UUID.randomUUID().toString() + "." + ext[ext.length - 1];
    }

    /**
     * MD5签名
     *
     * @param content 要签名的内容
     * @return MD5签名
     * @author zhangxiaogang
     * @since 2019/1/19 15:44
     */
    private String md5(String content) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(content.getBytes("UTF-8"));
            byte[] tmpFolder = md5.digest();
            for (int i = 0; i < tmpFolder.length; i++) {
                sb.append(Integer.toString((tmpFolder[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            log.error("无法生成文件的MD5签名", ex);
            this.setErrorMsg("无法生成文件的MD5签名");
            return null;
        } catch (UnsupportedEncodingException ex) {
            log.error("无法生成文件的MD5签名", ex);
            this.setErrorMsg("无法生成文件的MD5签名");
            return null;
        }
    }

    /**
     * 记录异常错误信息
     *
     * @param msg 错误详细
     */
    private void setErrorMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取错误详细
     *
     * @return 错误详细
     */
    public String getErrorMsg() {
        return this.msg;
    }
}
