package com.unity.innovation.util;

import com.unity.common.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * zip压缩文件实例
 * add by qinhuan
 *
 * @author Administrator
 */
@Slf4j
public class ZipUtil {

    public static void main(String[] args) throws Exception {

        // 创建文件夹
        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
        String basePath = resource.getPath() + UUIDUtil.getUUID() + "/";
        String filePaht = basePath + "创新发布/";
        //创建文件夹;
        createFile(filePaht + "工作动态/");
        createFile(filePaht + "创新发布清单/");
        createFile(filePaht + "与会企业信息/");
        //创建Excel文件;
//        createExcelFile(filePaht);
        //生成.zip文件;
        zip(basePath + "创新发布.zip", filePaht);
        //删除目录下所有的文件;
        //delFile(new File(basePath));
    }

    /**
     * 创建文件夹;
     *
     * @param path
     * @return
     */
    public static String createFile(String path) {
        File file = new File(path);
        //判断文件是否存在;
        if (!file.exists()) {
            //创建文件;
            file.mkdirs();
        }
        return path;
    }

//    /**
//     * 在指定目录下创建Excel文件;
//     *
//     * @param path
//     * @throws Exception
//     */
//    public static void createExcelFile(String path) throws Exception {
//        FileOutputStream outputStream = new FileOutputStream(path + "/xx.xlsx");
//
//        XSSFWorkbook wb = ExcelExportByTemplate.getWorkBook("template/darb.xlsx");
//        wb.write(outputStream);
//    }

    public static void zip(String targetFileName, String sourceFileName) throws Exception {
        log.info("压缩中...");
        //创建zip输出流
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetFileName));
        //创建缓冲输出流
        BufferedOutputStream bos = new BufferedOutputStream(out);
        File sourceFile = new File(sourceFileName);

        //调用函数
        compress(out, sourceFile, sourceFile.getName());

        bos.close();
        out.close();
        log.info("压缩完成");
    }

    public static void compress(ZipOutputStream out, File sourceFile, String base) throws Exception {
        //如果路径为目录（文件夹）
        if (sourceFile.isDirectory()) {
            //取出文件夹中的文件（或子文件夹）
            File[] flist = sourceFile.listFiles();
            if (flist.length == 0){//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
                log.info(base + "/");
                out.putNextEntry(new ZipEntry(base + "/"));
            } else {//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                for (int i = 0; i < flist.length; i++) {
                    compress(out, flist[i], base + "/" + flist[i].getName());
                }
            }
        } else {//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
            out.putNextEntry(new ZipEntry(base));
            FileInputStream fos = new FileInputStream(sourceFile);
            BufferedInputStream bis = new BufferedInputStream(fos);
            int tag;
            log.info(base);
            //将源文件写入到zip文件中
            while ((tag = bis.read()) != -1) {
                out.write(tag);
            }
            bis.close();
            fos.close();
        }
    }

    public static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }
}