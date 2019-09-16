package com.unity.me.umeng;

import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.pojos.UmengMessageDTO;
import com.unity.common.util.GsonUtils;
import com.unity.me.entity.SystemConfig;
import com.unity.me.umeng.android.AndroidBroadcast;
import com.unity.me.umeng.android.AndroidCustomizedcast;
import com.unity.me.umeng.android.AndroidUnicast;
import com.unity.me.umeng.ios.IOSBroadcast;
import com.unity.me.umeng.ios.IOSCustomizedcast;
import com.unity.me.umeng.ios.IOSUnicast;
import com.unity.springboot.support.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息推送(友盟)
 * <p>
 * create by wangbin at 2018年11月12日16:31:12
 */
@Slf4j
public class MessageSend {
    private static MessageSend instence = new MessageSend();
    private static SystemConfig systemConfig = SpringUtils.getBean(SystemConfig.class);

    private MessageSend() {
    }

    public static MessageSend getInstence() {
        return instence;
    }

    /**
     * android使用deviceToken 方式
     *
     * @param umengMessageDTO 消息对象
     * @author zhangxiaogang
     * @since 2019/3/15 14:27
     */
    public boolean sendAndroidUnicast(UmengMessageDTO umengMessageDTO) {
        String validUnicast = umengMessageDTO.validUnicast();
        if (validUnicast == null) {
            try {
                AndroidUnicast unicast = new AndroidUnicast(systemConfig.getAndroidAppkey(), systemConfig.getAndroidAppMasterSecret());
                unicast.setProductionMode(systemConfig.isProductModel());
                unicast.setAndroidUnicastParam(umengMessageDTO, unicast);
                return PushClient.send(unicast);
            } catch (Exception e) {
                log.info("android单播推送失败：" + e.getMessage());
                return false;
            }
        } else {
            log.info("android单播推送失败：" + validUnicast);
            return false;
        }
    }

    /**
     * ios使用deviceToken 方式
     *
     * @param umengMessageDTO 消息对象
     * @author zhangxiaogang
     * @since 2019/3/15 14:27
     */
    public boolean sendIOSUnicast(UmengMessageDTO umengMessageDTO) {
        String validUnicast = umengMessageDTO.validUnicast();
        if (validUnicast == null) {
            try {
                IOSUnicast unicast = new IOSUnicast(systemConfig.getIosAppkey(), systemConfig.getIosAppMasterSecret());
                unicast.setProductionMode(systemConfig.isProductModel());
                unicast.setIOSUnicastParam(umengMessageDTO, unicast);
                return PushClient.send(unicast);
            } catch (Exception e) {
                log.info("ios单播推送失败：" + e.getMessage());
                return false;
            }
        } else {
            log.info("ios单播推送失败：" + validUnicast);
            return false;
        }
    }

    /**
     * 广播方式 全部推送
     *
     * @param umengMessageDTO 消息内容
     * @return true
     * @author wangbin
     * @since 2018年11月12日16:29:01
     */
    private boolean sendIOSBroadcast(UmengMessageDTO umengMessageDTO) {
        try {
            IOSBroadcast broadcast = new IOSBroadcast(systemConfig.getIosAppkey(), systemConfig.getIosAppMasterSecret());
            broadcast.setProductionMode(systemConfig.isProductModel());
            broadcast.setIOSBroadcastParam(umengMessageDTO, broadcast);
            return PushClient.send(broadcast);
        } catch (Exception e) {
            log.info("IOS公告消息推送异常");
            return false;
        }
    }

    /**
     * 根据别名进行推送(IOS)
     *
     * @param umengMessageDTO 消息内容
     * @return true
     * @author wangbin
     * @since 2018年11月12日16:29:01
     */
    private boolean sendIOSCustomizedcast(UmengMessageDTO umengMessageDTO) {
        try {
            IOSCustomizedcast customizedcast = new IOSCustomizedcast(systemConfig.getIosAppkey(), systemConfig.getIosAppMasterSecret());
            customizedcast.setProductionMode(systemConfig.isProductModel());
            customizedcast.setIOSCustomizedCastParam(umengMessageDTO, customizedcast);
            return PushClient.send(customizedcast);
        } catch (Exception e) {
            log.info("IOS别名消息推送异常");
            return false;
        }
    }


    /**
     * 根据别名进行推送(安卓)
     *
     * @param umengMessageDTO 消息内容
     * @return true
     * @author wangbin
     * @since 2018年11月12日16:29:01
     */
    private boolean sendAndroidCustomizedcast(UmengMessageDTO umengMessageDTO) {
        try {
            AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(systemConfig.getAndroidAppkey(), systemConfig.getAndroidAppMasterSecret());
            customizedcast.setProductionMode(systemConfig.isProductModel());
            customizedcast.setAndroidCustomizedCastParam(umengMessageDTO, customizedcast);
            return PushClient.send(customizedcast);
        } catch (Exception e) {
            log.info("android别名消息推送异常");
            return false;
        }
    }

    /**
     * 根据别名进行推送(安卓和ios)
     *
     * @param umengMessageDTO 消息内容
     * @return true
     * @author wangbin
     * @since 2018年11月12日16:29:01
     */
    public boolean sendCustomizedcast(UmengMessageDTO umengMessageDTO) {
        String valid = umengMessageDTO.validCustomizedcast();
        if (valid == null) {
            boolean androidFlag = sendAndroidCustomizedcast(umengMessageDTO);
            boolean iosFlag = sendIOSCustomizedcast(umengMessageDTO);
            return androidFlag && iosFlag;
        } else {
            log.info("推送异常：" + valid);
            return false;
        }
    }


    /**
     * 广播方式 全部推送(安卓)
     *
     * @param umengMessageDTO 消息内容
     * @return true
     * @author wangbin
     * @since 2018年11月12日16:29:01
     */
    private boolean sendAndroidBroadcast(UmengMessageDTO umengMessageDTO) {
        try {
            AndroidBroadcast broadcast = new AndroidBroadcast(systemConfig.getAndroidAppkey(), systemConfig.getAndroidAppMasterSecret());
            broadcast.setProductionMode(systemConfig.isProductModel());
            broadcast.setAndroidBroadcastParam(umengMessageDTO, broadcast);
            return PushClient.send(broadcast);
        } catch (Exception e) {
            log.info("android公告消息推送异常");
            return false;
        }

    }

    /**
     * 广播消息推送
     *
     * @param umengMessageDTO 消息内容
     * @author zhangxiaogang
     * @since 2019/3/15 15:29
     */
    public boolean sendBroadcast(UmengMessageDTO umengMessageDTO) {
        String valid = umengMessageDTO.valid();
        if (valid == null) {
            boolean iosBroadcast = this.sendIOSBroadcast(umengMessageDTO);
            boolean androidBroadcast = this.sendAndroidBroadcast(umengMessageDTO);
            return iosBroadcast && androidBroadcast;
        } else {
            log.info("推送异常：" + valid);
            return false;
        }
    }

}
