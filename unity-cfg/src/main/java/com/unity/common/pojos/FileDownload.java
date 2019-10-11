package com.unity.common.pojos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 文件下载辅助类
 * <p>
 * create by gengjiajia at 2019/10/11 15:20
 */
@Data
@AllArgsConstructor
@Builder(builderMethodName = "newInstance")
public class FileDownload {

    String url;

    String name;
}
