package com.unity.me.service;

import com.unity.common.base.BaseServiceImpl;
import com.unity.me.dao.MessageReciverDao;
import com.unity.me.entity.MessageReciver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: MessageReciverService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-02-12 12:47:33
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MessageReciverServiceImpl extends BaseServiceImpl<MessageReciverDao, MessageReciver> {


    /**
     * 根据消息ID查询单独选择的人员
     *
     * @return 单独选择人员ID
     * @author wangbin
     * @since 2019年2月18日15:20:03
     */
    public List<Long> getMessageReceiverByGroupInfoIsNull(Long groupInfoId) {
        //查询组织下所有的用户
        return baseMapper.selectMessageReceiverByGroupInfoIsNull(groupInfoId);
    }

    /**
     * 根据消息ID查询单独选择的人员
     *
     * @return 分组选择人员ID
     * @author wangbin
     * @since 2019年2月18日15:20:03
     */
    public List<MessageReciver> getMessageReceiverByGroupInfo(Long groupInfoId) {
        //查询组织下所有的用户
        return baseMapper.selectMessageReceiverByGroupInfo(groupInfoId);
    }

    /**
     * 根据消息ID 查询出所有的人员
     *
     * @return 分组选择人员ID
     * @author wangbin
     * @since 2019年2月18日15:20:03
     */
    public List<Long> getMessageReceiverByPushId(Long groupInfoId) {
        return baseMapper.selectMessageReceiverByMessageId(groupInfoId);
    }

    /**
     * 根据消息以及当前登陆用户查询人员表ID
     *
     * @return 分组选择人员ID
     * @author wangbin
     * @since 2019年2月18日15:20:03
     */
    public List<MessageReciver> selectReceiverByUserIdAndMessageId(Long userId, Long groupInfoId) {
        return baseMapper.selectReceiverByUserIdAndMessageId(userId, groupInfoId);
    }

    /**
     * 查询当前登陆人未读消息
     *
     * @return 分组选择人员ID
     * @author wangbin
     * @since 2019年2月18日15:20:03
     */
    public Long selectReceiverByMessageIdAndUserId(Long userId, Integer type) {
        return baseMapper.selectReceiverByMessageIdAndUserId(userId, type);
    }

    /**
     * 删除用户关联消息
     *
     * @param messageId 消息id
     * @author zhangxiaogang
     * @since 2019/3/16 13:13
     */
    public void deleteMessageRecivers(Long messageId) {
        baseMapper.deleteMessageRecivers(messageId);
    }

}
