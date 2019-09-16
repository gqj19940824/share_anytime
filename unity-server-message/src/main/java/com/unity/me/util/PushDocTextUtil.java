package com.unity.me.util;

import com.unity.me.entity.SystemConfig;
import com.unity.me.entity.ueditor.Ueditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

/**
 * @author wangbin
 * @since 2019/2/26
 */
@Component
@Slf4j
public class PushDocTextUtil {

    @Autowired
    private static SystemConfig systemConfig;

   /* @PostConstruct
    public void init() {
        pushDocTextUtil = this;
        pushDocTextUtil.systemConfig = this.systemConfig;
    }*/

    public static String pushDocText(Ueditor entity) {
        try {
            String fileName = entity.getDistinguish() + entity.getId().toString();
            return createFile(fileName, entity.getContent());
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    /**
     * 创建文件
     *
     * @param fileName    文件名称
     * @param filecontent 文件内容
     * @return 是否创建成功，成功则返回文件路径
     * @author zhangxiaogang
     * @since 2019/4/16 10:36
     */
    private static String createFile(String fileName, String filecontent) {
        boolean bool = false;
        String filenameTemp = systemConfig.getUeditorUploadUrl() + fileName + ".html";
        File file = new File(filenameTemp);
        try {
            //如果文件不存在，则创建新的文件
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            bool = true;
            log.info("success create file,the file is " + filenameTemp);
            //创建文件成功后，写入内容到文件里
            writeFileContent(filenameTemp, filecontent);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return bool ? filenameTemp : null;

    }

    /**
     * 向文件中写入内容
     *
     * @param filepath 文件路径与名称
     * @param newstr   写入的内容
     * @throws IOException io异常
     * @author zhangxiaogang
     * @since 2019/4/16 10:33
     */
    private static void writeFileContent(String filepath, String newstr) throws IOException {
        String filein = newstr + "\r\n";//新写入的行，换行
        String temp;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();
            //文件原有内容
            for (int i = 0; (temp = br.readLine()) != null; i++) {
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer.append(System.getProperty("line.separator"));
            }
            buffer.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }


}
