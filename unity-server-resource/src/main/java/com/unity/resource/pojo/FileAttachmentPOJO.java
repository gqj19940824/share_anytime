package com.unity.resource.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.google.gson.reflect.TypeToken;
import com.unity.common.base.BaseEntity;
import com.unity.common.util.GsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *<p>
 *create by zhangxiaogang at 2019/9/9 20:10
 */
@Data
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
public class FileAttachmentPOJO extends BaseEntity {



    /**
     *
     **/
    private String attachmentCode ;



    /**
     *
     **/
    private String url ;



    /**
     *
     **/
    private String size ;

    /**
     *
     **/
    private String sizeLong ;

    /**
     *
     **/
    private String status ;

    /**
     *
     **/
    private String type ;

    /**
     *
     **/
    private String name ;

    /*public static void main(String[] args) {
        String content = "{\"id\":-1000,\"status\":\"上传完成\",\"name\":\"9.jpg\",\"type\":\"jpg\",\"url\":\"https://security-test.jingcaiwang.cn/group1/M00/00/C5/rBMBLF1kmFyAHw9YAAT-Bb1ye8A081.jpg\",\"sizeLong\":327173,\"size\":\"319.50KB\",\"mediaSort\":1}";
        FileAttachmentPOJO parse = GsonUtils.parse(content, new TypeToken<FileAttachmentPOJO>() {
        });
        System.out.println(parse.getName());
        String name = parse.getName();
        System.out.println(name.split("\\."));

        String filepath = "/data/upload/testTemp/fileListJar/2019-09-09/a929bf60-1798-41ff-9e44-88d5eec9296a.zip";
        String[] split = filepath.split("\\.")[0].split("\\/");
        System.out.println(split.length);
        System.out.println(split[split.length-1]);
    }*/
}
