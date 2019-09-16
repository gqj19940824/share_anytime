package com.unity.common.utils;

import java.util.UUID;

/**
 * @author  lifeihong
 * Date: 2019/7/4
 *  Time: 20:31
 * Description:
 *
 */
public class UUIDUtil {

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
