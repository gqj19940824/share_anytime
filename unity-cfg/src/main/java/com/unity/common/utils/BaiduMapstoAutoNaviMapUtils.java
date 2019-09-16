package com.unity.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * create by jiaww at 2019-04-29 16:44
 */
public class BaiduMapstoAutoNaviMapUtils
{

    static double pi = 3.14159265358979324;
    static double a = 6378245.0;
    static double ee = 0.00669342162296594323;
    static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    // World Geodetic System ==> Mars Geodetic System
    public static Map<String, String> transform(double wgLat, double wgLon) {
        Map<String, String> resultMap = new HashMap<String, String>();
        double mgLat = 0;
        double mgLon = 0;
        if (outOfChina(wgLat, wgLon)) {
            mgLat = wgLat;
            mgLon = wgLon;
            resultMap.put("mgLat", String.valueOf(mgLat));
            resultMap.put("mgLon", String.valueOf(mgLon));
            return resultMap;
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        mgLat = wgLat + dLat;
        mgLon = wgLon + dLon;
        resultMap.put("mgLat", String.valueOf(mgLat));
        resultMap.put("mgLon", String.valueOf(mgLon));
        return resultMap;
    }

    static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }


    /**
     * GCJ-02(火星坐标) 和 BD-09 （百度坐标）
     * bd_encrypt 将 GCJ-02 坐标转换成 BD-09 坐标， bd_decrypt 反之。
     * @param bd_lat
     * @param bd_lon
     */
    public static Map<String, String>  bd_decrypt(double bd_lat, double bd_lon){
        Map<String, String> resultMap = new HashMap<String,String>();
        double gg_lat = 0;
        double gg_lon = 0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;

        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);

        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);

        gg_lon = z * Math.cos(theta);

        gg_lat = z * Math.sin(theta);
        resultMap.put("mgLat", String.valueOf(gg_lat));
        resultMap.put("mgLon", String.valueOf(gg_lon));
        return resultMap;

    }


}
