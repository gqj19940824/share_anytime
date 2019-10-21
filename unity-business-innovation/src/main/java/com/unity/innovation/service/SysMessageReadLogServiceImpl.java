
package com.unity.innovation.service;

import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.MessageSaveFormEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.utils.HashRedisUtils;
import com.unity.innovation.dao.SysMessageReadLogDao;
import com.unity.innovation.entity.SysMessageReadLog;
import com.unity.innovation.interceptor.MyWebSocketHandler;
import org.apache.commons.collections4.MapUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import java.util.List;
import java.util.Map;

/**
 * ClassName: SysMessageReadLogService
 * date: 2019-09-23 09:39:17
 *
 * @author G
 * @since JDK 1.8
 */
@Service
public class SysMessageReadLogServiceImpl extends BaseServiceImpl<SysMessageReadLogDao, SysMessageReadLog> {

    private final HashRedisUtils hashRedisUtils;
    private final MyWebSocketHandler webSocketHandler;

    public SysMessageReadLogServiceImpl(HashRedisUtils hashRedisUtils, MyWebSocketHandler webSocketHandler) {
        this.hashRedisUtils = hashRedisUtils;
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * 给指定 用户 增加消息数量
     *
     * @param form       增/减量形式 1 系统消息  2 通知公告
     * @param userIdList 要发送的用户id
     * @param isAdd      是否是增加消息数量 0 否 1 是
     * @author gengjiajia
     * @since 2019/09/25 18:33
     */
    @Async
    public synchronized void updateMessageNumToUserIdList(Integer form, List<Long> userIdList, Integer isAdd) {
        //从redis获取当前增量形式对应的消息数量
        MessageSaveFormEnum formEnum = MessageSaveFormEnum.of(form);
        if (formEnum == null) {
            return;
        }
        Map<String, Object> numMap = hashRedisUtils.getObj(formEnum.getName());
        if (MapUtils.isEmpty(numMap)) {
            numMap = Maps.newHashMap();
        }
        int value = isAdd.equals(YesOrNoEnum.YES.getType()) ? 1 : -1;
        for (Long userId : userIdList) {
            Object numObj = numMap.get(userId.toString());
            int num = numObj == null ? 0 : (Integer) numObj;
            num += value;
            numMap.put(userId.toString(), num < 0 ? 0 : num);
        }
        hashRedisUtils.putValueByKey(formEnum.getName(), numMap);
        sendMessageByUserIdList(userIdList, YesOrNoEnum.YES.getType());
    }

    /**
     * 发送消息到指定用户
     *
     * @param userIdList 指定用户
     * @param isAdd      是否是增量提醒 0 否 1 是
     * @author gengjiajia
     * @since 2019/09/25 18:31
     */
    private synchronized void sendMessageByUserIdList(List<Long> userIdList, int isAdd) {
        Map<String, Object> sysMegNumMap = hashRedisUtils.getObj(MessageSaveFormEnum.SYS_MSG.getName());
        Map<String, Object> noticeMegNumMap = hashRedisUtils.getObj(MessageSaveFormEnum.NOTICE.getName());
        userIdList.forEach(userId -> {
            //获取所有消息数量，判断是否有属于当前人的消息
            int sysMessageNum = 0;
            int noticeNum = 0;
            if (MapUtils.isNotEmpty(sysMegNumMap)) {
                Object numBySysObj = sysMegNumMap.get(userId.toString());
                int numBySys = numBySysObj != null ? Integer.parseInt(numBySysObj.toString()) : 0;
                sysMessageNum += numBySys;
            }

            if (MapUtils.isNotEmpty(noticeMegNumMap)) {
                Object numByNoticeObj = noticeMegNumMap.get(userId.toString());
                int numByNotice = numByNoticeObj == null ? 0 : Integer.parseInt(numByNoticeObj.toString());
                noticeNum += numByNotice;
            }
            webSocketHandler.sendMessageToUser(userId.toString(),
                    new TextMessage("{\"isAdd\":" + isAdd + ",\"sysMessageNum\":" + sysMessageNum + ",\"noticeNum\":" + noticeNum + "}"));
        });
    }

    /**
     * 发送短信给指定手机号
     *
     * @param  phoneList 手机号集合
     * @param smsContent 短信内容
     * @author gengjiajia
     * @since 2019/10/17 20:52
     */
    public synchronized void sendSmsToUserIdList(List<String> phoneList,String smsContent){
        //TODO 调用messageClient
    }
}
