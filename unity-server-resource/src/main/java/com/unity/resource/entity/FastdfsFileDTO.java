package com.unity.resource.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件上传结果信息
 * <p>
 * <p>
 * create by gengjiajia at 2018/09/20 09:53
 */
@Data
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
public class FastdfsFileDTO {

    private String groupName;
    private String filePath;
    private String fileUrl;
    private String thumbUrl;//缩略图
    private String fileName;

    public FastdfsFileDTO() {

    }

    /**
     *校验文件内容
     *
     *@author zhangxiaogang
     *@since 2019/1/8 17:18
     */
    public String valid(){
        if(StringUtils.isBlank(fileUrl)){
            return "文件路径不能为空";
        }
        if(StringUtils.isBlank(fileName)){
            return "文件名称不能为空";
        }
        return null;
    }
}
