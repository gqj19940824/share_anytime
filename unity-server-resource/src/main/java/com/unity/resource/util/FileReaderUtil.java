package com.unity.resource.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileReaderUtil {

    public static String getString(InputStream is) throws Exception {
        byte[] buffer = null;
        buffer = getBytes(is, buffer);
        return new String(buffer);
    }

    public static String read(String path, String coding) throws Exception {
        String result = "";
        File file = new File(path);
        byte[] rawbyte = getBytes(file);
        result = new String(rawbyte, coding);
        return result;
    }

    public static InputStream getInputStream(String data) throws Exception {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(data.getBytes("gbk"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }


    public static byte[] getBytes(File file) throws Exception {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = bis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            bis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }


    public static final void write(final String path, final String message, String coding) throws Exception {
        // 先写入临时文件
        String tmpFile = path + ".tmp";
        string2FileNotSafe(message, tmpFile);

        // 备份之前的文件
        String bakFile = path + ".bak";
        String prevContent = file2String(path);
        if (prevContent != null) {
            string2FileNotSafe(prevContent, bakFile);
        }

        // 删除正式文件
        File file = new File(path);
        file.delete();

        // 临时文件改为正式文件
        file = new File(tmpFile);
        file.renameTo(new File(path));
    }


    public static final void string2FileNotSafe(final String str, final String fileName) throws IOException {
        File file = new File(fileName);
        File fileParent = file.getParentFile();
        if (fileParent != null) {
            fileParent.mkdirs();
        }
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(str);
        } catch (IOException e) {
            throw e;
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }


    public static final String file2String(final String fileName) throws Exception {
        File file = new File(fileName);
        return file2String(file);
    }


    private static final String file2String(final File file) throws Exception {
        if (file.exists()) {
            char[] data = new char[(int) file.length()];
            boolean result = false;

            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
                int len = fileReader.read(data);
                result = (len == data.length);
            } catch (IOException e) {
                throw e;
            } finally {
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (IOException e) {
                        throw e;
                    }
                }
            }

            if (result) {
                String value = new String(data);
                return value;
            }
        }
        return null;
    }

    /**
     * 将文件流转成byte[]
     *
     * @param is 文件流
     * @return byte[]
     * @author gengjiajia
     * @since 2018/10/30 17:41
     */
    public static byte[] getByteByInputStream(InputStream is) throws Exception {
        byte[] buffer = null;
        buffer = getBytes(is, buffer);
        return buffer;
    }

    private static byte[] getBytes(InputStream is, byte[] buffer) {
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = bis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            bis.close();
            bos.close();
            buffer = bos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 将byte数组另存为文件
     *
     * @param buf      byte 数组
     * @param filePath 文件存储路径
     * @param fileName 文件名称
     * @return 新文件路径
     * @author zhangxiaogang
     * @since 2019/1/7 9:31
     */
    public static String byte2File(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            fos.flush();
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath + File.separator + fileName;
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
    public static byte[] dealZIPFiles(List<String> fileList, String zipFile) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        InputStream fis = null;
        try {
            for (String url : fileList) {
                File file = new File(url);
                zos.putNextEntry(new ZipEntry(file.getName()));
                fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int r = 0;
                while ((r = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, r);
                }
                fis.close();
                file.delete();
            }
        } catch (Exception e) {
            log.info("--文件压缩异常：" + e.getMessage());
        } finally {
            zos.close();
            if (fis != null) {
                fis.close();
            }
        }
        return FileReaderUtil.getBytes(new File(zipFile));
    }

    /**
     * 将文件流封装到文件内
     *
     * @param ins  文件流
     * @param file file对象
     * @author zhangxiaogang
     * @since 2019/2/27 19:11
     */
    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
