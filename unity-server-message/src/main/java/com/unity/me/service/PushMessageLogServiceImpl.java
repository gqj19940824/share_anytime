package com.unity.me.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.pojos.UmengMessageDTO;
import com.unity.common.util.MultipartFileUtil;
import com.unity.me.client.ReClient;
import com.unity.me.dao.PushMessageLogDao;
import com.unity.me.entity.MessageReciver;
import com.unity.me.entity.PushMessageLog;
import com.unity.me.enums.BizModelEnum;
import com.unity.me.enums.BizTypeEnum;
import com.unity.me.enums.PushTypeEnum;
import com.unity.me.pojos.PushMessageLogPO;
import com.unity.me.umeng.MessageSend;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: PushMessageLogService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-02-12 12:47:36
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Slf4j
public class PushMessageLogServiceImpl extends BaseServiceImpl<PushMessageLogDao, PushMessageLog> {

    @Autowired
    MessageReciverServiceImpl messageReceiverService;
    @Autowired
    ReClient reClient;

    /**
     * APP端用户查询推送消息或公共消息列表
     *
     * @param bizType 类型
     * @return 消息的列表
     * @author wangbin
     * @since 2019年2月20日14:22:03
     */
    public List<PushMessageLog> selectPushMessageLogList(Page page, Integer bizType) {
        if (bizType.equals(BizTypeEnum.PUSH.getId())) {
            Customer customer =   LoginContextHolder.getRequestAttributes();
            return this.baseMapper.selectPushMessageLogByPage(page,customer.id, bizType);
        } else {
            return this.baseMapper.selectAnnouncementMessageLogByPage(page, bizType);
        }
    }

    /**
     * 保存或更新推送日志信息
     *
     * @param pushMessageLogPO 推送消息
     * @author zhangxiaogang
     * @since 2019/3/16 10:56
     */
    public void saveOrUpdatePushMessageLog(PushMessageLogPO pushMessageLogPO) {
        PushMessageLog pushMessageLog = new PushMessageLog();
        pushMessageLog.setTitle(pushMessageLogPO.getTitle());
        pushMessageLog.setTextContent(pushMessageLogPO.getTextContent());
        pushMessageLog.setContent(pushMessageLogPO.getContent());
        pushMessageLog.setImgUrl(pushMessageLogPO.getImgUrl());
        pushMessageLog.setRecordStatus(YesOrNoEnum.NO.getType());
        pushMessageLog.setTaskId(pushMessageLogPO.getTaskId());
        pushMessageLog.setNotes(pushMessageLogPO.getNotes());
        //业务类型
        pushMessageLog.setBizType(pushMessageLogPO.getBizType());
        pushMessageLog.setBizModel(pushMessageLogPO.getBizModel());
        //自定义播
        pushMessageLog.setPushType(PushTypeEnum.CUSTOMIZEDCAST.getId());
        if (pushMessageLogPO.getId() != null) {
            pushMessageLog.setId(pushMessageLogPO.getId());
        }
        this.saveOrUpdate(pushMessageLog);
        if (BizTypeEnum.PUSH.getId().equals(pushMessageLog.getBizType())) {//保存推送消息
            saveMessageReceivers(pushMessageLogPO, pushMessageLog);
        }
    }

    /**
     * 保存或更新推送关联人员信息
     *
     * @param pushMessageLogPO 推送消息
     * @param pushMessageLog   推送日志
     * @author zhangxiaogang
     * @since 2019/3/16 10:56
     */
    public void saveMessageReceivers(PushMessageLogPO pushMessageLogPO, PushMessageLog pushMessageLog) {
        List<Long> messageReceiverList = pushMessageLogPO.getMessageReceiverList();
        List<MessageReciver> messageReceivers = messageReceiverList.parallelStream()
                .map(userId -> {
                    MessageReciver messageReceiver = new MessageReciver();
                    messageReceiver.setIdMePushMessageLog(pushMessageLog.getId());
                    messageReceiver.setPbkUserInfoId(userId);
                    messageReceiver.setIsRead(YesOrNoEnum.NO.getType());
                    return messageReceiver;
                })
                .collect(Collectors.toList());
        List<MessageReciver> groupUserList = pushMessageLogPO.getUserGroupInfoPOS()
                .parallelStream()
                .filter(userGroupPO -> messageReceiverList.stream().noneMatch(one -> userGroupPO.getUserId().equals(one)))
                .map(userGroupInfoPO -> {
                    MessageReciver messageReceiver = new MessageReciver();
                    messageReceiver.setIdMePushMessageLog(pushMessageLog.getId());
                    messageReceiver.setPbkUserInfoId(userGroupInfoPO.getUserId());
                    messageReceiver.setGroupId(userGroupInfoPO.getGroupId());
                    messageReceiver.setIsRead(YesOrNoEnum.NO.getType());
                    return messageReceiver;
                }).collect(Collectors.toList());
        messageReceivers.addAll(groupUserList);
        if (pushMessageLogPO.getId() != null) {
            //如果存在关联用户则先删除，然后保存
            List<Long> longList = messageReceiverService.getMessageReceiverByPushId(pushMessageLogPO.getId());
            if (CollectionUtils.isNotEmpty(longList)) {
                messageReceiverService.deleteMessageRecivers(pushMessageLogPO.getId());
            }
        }
        new Thread(() -> {
            messageReceiverService.saveOrUpdateBatch(messageReceivers);
        }).start();
    }

    /**
     * 消息发布
     *
     * @param pushMessageLog 消息内容
     * @author zhangxiaogang
     * @since 2019/3/16 14:10
     */
    public void releaseMessageUmeng(PushMessageLog pushMessageLog) {
        //生成富文本静态文件URL
        String fileName = "editor_" + pushMessageLog.getId() + ".html";
        String content = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
                "<meta content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;\"  name=\"viewport\"  >"
                + pushMessageLog.getContent();
        try {
            InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));
            MultipartFile multipartFile = MultipartFileUtil.createFileItemByInputStream(stream, fileName);
            String pushDocUrl = reClient.fileUpload(multipartFile);
            pushMessageLog.setDocUrl(pushDocUrl);
            pushMessageLog.setRecordStatus(YesOrNoEnum.YES.getType());
            this.saveOrUpdate(pushMessageLog);
        } catch (Exception e) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "生成文件异常");
        }
        /*QueryWrapper<MessageReciver> qw = new QueryWrapper<>();
        qw.lambda().eq(MessageReciver::getIdMePushMessageLog, pushMessageLog.getId());
        List<MessageReciver> receiverIdList = messageReceiverService.list(qw);
        //接收人不为空
        if (BizTypeEnum.PUSH.getId().equals(pushMessageLog.getBizType()) && CollectionUtils.isNotEmpty(receiverIdList)) {
            List<Long> userIdList = receiverIdList.parallelStream().map(MessageReciver::getPbkUserInfoId).collect(Collectors.toList());
            sendUnicast(userIdList, pushMessageLog);
        } else {
            new Thread(() -> {
                MessageSend.getInstence().sendBroadcast(UmengMessageDTO.newInstance()
                        .title(pushMessageLog.getTitle())
                        .text(pushMessageLog.getTextContent())
                        .docType(BizTypeEnum.ANNOUNCEMENT.getId().toString())
                        .column(BizModelEnum.ANNOUNCEMENT.getId().toString())
                        .build());
            }).start();
        }*/

    }

    /**
     * 消息推送
     *
     * @param appUserIds     userId的集合
     * @param pushMessageLog 标题
     * @author zhangxiaogang
     * @since 2019/3/16 15:21
     */
    private void sendUnicast(List<Long> appUserIds, PushMessageLog pushMessageLog) {
        int listSize = appUserIds.size();
        int toIndex = 49;
        //用map存起来新的分组后数据
        for (int i = 0; i < listSize; i += toIndex) {
            //作用为toIndex最后没有49条数据则剩余几条newList中就装几条
            if (i + toIndex > listSize) {
                toIndex = listSize - i;
            }
            List<Long> newList = appUserIds.subList(i, i + toIndex);
            String userIds = StringUtils.join(newList.toArray(), ",");
            new Thread(() -> {
                MessageSend.getInstence().sendCustomizedcast(UmengMessageDTO.newInstance()
                        .alias(userIds)
                        .title(pushMessageLog.getTitle())
                        .text(pushMessageLog.getTextContent())
                        .docType(BizTypeEnum.PUSH.getId().toString())
                        .targetId("")
                        .column(BizModelEnum.PUSH_MESSAGE.getId().toString())
                        .build());
            }).start();
        }
    }

}
