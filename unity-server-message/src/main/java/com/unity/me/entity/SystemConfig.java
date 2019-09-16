package com.unity.me.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统配置
 * <p>
 * create by zhangxiaogang at 2018/12/20 14:56
 */
@Component
@ConfigurationProperties(prefix = "system.config")
@Data
public class SystemConfig {

    /**
     * 富文本上传地址
     */
    private String ueditorUploadUrl;

    private boolean productModel;
    // private boolean productModel = true;
    //应用唯一标识
    private String androidAppkey;
    // 服务器秘钥
    private String androidAppMasterSecret;
    private String iosAppkey;
    private String iosAppMasterSecret;


}