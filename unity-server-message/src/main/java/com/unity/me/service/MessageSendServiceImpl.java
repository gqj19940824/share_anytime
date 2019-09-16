package com.unity.me.service;

import com.google.common.collect.Lists;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.UmengMessageDTO;
import com.unity.common.util.GsonUtils;
import com.unity.me.client.vo.MessageVO;
import com.unity.me.entity.PushMessageLog;
import com.unity.me.enums.BizModelEnum;
import com.unity.me.enums.BizTypeEnum;
import com.unity.me.enums.PushTypeEnum;
import com.unity.me.pojos.PushMessageLogPO;
import com.unity.me.umeng.MessageSend;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发送消息业务层
 * <p>
 * create by zhangxiaogang at 2019/3/16 14:16
 */
@Service
@Slf4j
public class MessageSendServiceImpl {

    private final PushMessageLogServiceImpl pushMessageLogService;

    public MessageSendServiceImpl(PushMessageLogServiceImpl pushMessageLogService) {
        this.pushMessageLogService = pushMessageLogService;
    }

    /**
     * 消息推送
     *
     * @param messageVO 推送消息
     * @author zhangxiaogang
     * @since 2019/3/16 14:19
     */
    public boolean sendUmengCustomizedcast(MessageVO messageVO) {
        log.error("推送：" + GsonUtils.format(messageVO));
        int listSize = messageVO.getAppUserIds().size();
        int toIndex = 49;
        //用map存起来新的分组后数据
        for (int i = 0; i < listSize; i += toIndex) {
            //作用为toIndex最后没有49条数据则剩余几条newList中就装几条
            if (i + toIndex > listSize) {
                toIndex = listSize - i;
            }
            List<Long> newList = messageVO.getAppUserIds().subList(i, i + toIndex);
            String userIds = StringUtils.join(newList.toArray(), ",");
            MessageSend.getInstence().sendCustomizedcast(UmengMessageDTO.newInstance()
                    .alias(userIds)
                    .title(messageVO.getTitle())
                    .text(messageVO.getText())
                    //.subTitle(messageVO.getSubTitle())
                    .column(messageVO.getColumn())
                    .targetId(messageVO.getTargetId())
                    .docType(BizTypeEnum.PUSH.getId().toString())
                    .build());
        }
        if (messageVO.isFlag()) {
            saveMessageLog(messageVO, BizTypeEnum.PUSH.getId());
        }
        return true;
    }



    /**
     * 推送广播消息
     *
     * @param messageVO 消息内容
     * @author zhangxiaogang
     * @since 2019/3/16 15:26
     */
    public void sendBroadcastMessage(MessageVO messageVO) {
        MessageSend.getInstence().sendBroadcast(UmengMessageDTO.newInstance()
                .title(messageVO.getTitle())
                .text(messageVO.getText())
                //.subTitle(messageVO.getSubTitle())
                .column(messageVO.getColumn())
                .targetId(messageVO.getTargetId())
                .docType(BizTypeEnum.ANNOUNCEMENT.getId().toString())
                .build());
        if (messageVO.isFlag()) {
            saveMessageLog(messageVO, BizTypeEnum.ANNOUNCEMENT.getId());
        }
    }


    /**
     * 保存消息日志
     *
     * @param messageVO 消息内容
     * @author zhangxiaogang
     * @since 2019/3/16 14:25
     */
    private void saveMessageLog(MessageVO messageVO, Integer bizType) {
        PushMessageLog pushMessageLog = new PushMessageLog();
        pushMessageLog.setTitle(messageVO.getTitle());
        pushMessageLog.setTextContent(messageVO.getText());
        pushMessageLog.setRecordStatus(YesOrNoEnum.YES.getType());
        pushMessageLog.setNotes("备注：" + messageVO.getExtraField());
        pushMessageLog.setBizType(bizType);//业务类型
        if (StringUtils.isNotEmpty(messageVO.getDocType())) {
            pushMessageLog.setBizModel(Integer.valueOf(messageVO.getDocType()));//业务模块
        }
        if (StringUtils.isNotEmpty(messageVO.getTargetId())) {
            pushMessageLog.setTaskId(Long.valueOf(messageVO.getTargetId()));//任务
        }
        pushMessageLog.setPushType(PushTypeEnum.CUSTOMIZEDCAST.getId());//自定义播
        pushMessageLogService.save(pushMessageLog);
        if(CollectionUtils.isNotEmpty(messageVO.getAppUserIds())){
            PushMessageLogPO pushMessageLogPO = new PushMessageLogPO();
            pushMessageLogPO.setMessageReceiverList(messageVO.getAppUserIds());
            pushMessageLogPO.setUserGroupInfoPOS(Lists.newArrayList());
            pushMessageLogService.saveMessageReceivers(pushMessageLogPO, pushMessageLog);
        }
    }


}
