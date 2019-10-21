package com.unity.common.pojos;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Exrickx
 */
@Data
public class SmsSetting implements Serializable{

    private String serviceName;

    private String accessKey;

    private String secretKey;

    private String signName;

    private String bucket;

    private Integer type;

    private String templateCode;

    private Boolean changed;
}
