
package com.unity.me.dao;


import com.unity.common.base.BaseDao;
import com.unity.me.entity.MessageReciver;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息接收人员关联信息
 *
 * @author creator
 * 生成时间 2019-02-12 12:47:33
 */
public interface MessageReciverDao extends BaseDao<MessageReciver> {


    /**
     * 根据消息ID查询单独选择的人员
     *
     * @param groupInfoId 分组ID
     * @return 分组下的成员
     * @author wangbin
     * @since 2019年2月18日10:49:28
     */
    @Select("SELECT  mugi.pbk_user_info_id FROM  me_e_message_reciver mugi  \n" + "\n" + "WHERE  mugi.id_me_push_message_log = #{groupInfoId} AND mugi.group_id IS NULL AND mugi.is_deleted = 0")
    List<Long> selectMessageReceiverByGroupInfoIsNull(Long groupInfoId);

    /**
     * 根据消息ID查询分组选择的人员
     *
     * @param groupInfoId 分组ID
     * @return 分组下的成员
     * @author wangbin
     * @since 2019年2月18日10:49:28
     */
    @Select("SELECT mugi.group_id AS groupId , mugi.pbk_user_info_id FROM  me_e_message_reciver mugi  \n" + "\n" + "WHERE  mugi.id_me_push_message_log = #{groupInfoId} AND mugi.group_id IS NOT NULL AND mugi.is_deleted = 0")
    List<MessageReciver> selectMessageReceiverByGroupInfo(Long groupInfoId);

    /**
     * 根据消息ID查询分组选择的人员
     *
     * @param messageId 分组ID
     * @return 分组下的成员
     * @author wangbin
     * @since 2019年2月18日10:49:28
     */
    @Select("SELECT mugi.id FROM me_e_message_reciver mugi WHERE mugi.id_me_push_message_log = #{messageId} AND mugi.is_deleted = 0")
    List<Long> selectMessageReceiverByMessageId(Long messageId);

    /**
     * 根据消息以及当前登陆用户查询人员表ID
     *
     * @param userId    当前登陆用户ID
     * @param messageId 消息ID
     * @return 分组下的成员
     * @author wangbin
     * @since 2019年2月18日10:49:28
     */
    @Select("SELECT receiver.id, receiver.id_me_push_message_log , receiver.gmt_create ,\n"
            + " receiver.gmt_modified , receiver.is_deleted , receiver.i_sort , \n"
            + "receiver.notes , receiver.creator ,receiver.editor ,receiver.pbk_user_info_id ,receiver.is_read ,receiver.group_id \n"
            + "FROM me_e_message_reciver receiver \n"
            + "WHERE 1=1 AND receiver.pbk_user_info_id = #{userId} \n"
            + "AND receiver.id_me_push_message_log = #{messageId} " +
            "AND receiver.is_deleted = 0")
    List<MessageReciver> selectReceiverByUserIdAndMessageId(@Param("userId") Long userId, @Param("messageId") Long messageId);

    /**
     * 查询当前用户未读数量
     *
     * @param userId 用户ID
     * @return 分组下的成员
     * @author wangbin
     * @since 2019年2月18日10:49:28
     */
    @Select("<script>" +
            "SELECT\n"
            + "\tCOUNT(receiver.id)\n"
            + "FROM\n"
            + "\tme_e_message_reciver receiver\n"
            + "LEFT JOIN me_push_message_log me ON me.id = receiver.id_me_push_message_log\n"
            + "WHERE\n"
            + "\t1 = 1\n"
            + "AND receiver.pbk_user_info_id = #{userId}\n"
            + "AND receiver.is_read = 0\n"
            + "AND receiver.is_deleted = 0\n"
            + "AND me.record_status = 1\n"
            + "<if test=\"bizType == 2\">\n"
            + "  AND me.biz_type = #{bizType}\n"
            + "   </if>"
            + "<if test=\"bizType != 2\">\n"
            + "  AND me.biz_type != 2\n"
            + "   </if>" +
            "</script>"
    )
    Long selectReceiverByMessageIdAndUserId(@Param("userId") Long userId, @Param("bizType") Integer bizType);


    /**
     * 删除用户关联消息
     *
     * @param messageId 消息id
     * @author zhangxiaogang
     * @since 2019/3/16 13:13
     */
    @Transactional(rollbackFor = Exception.class)
    @Delete("delete from me_e_message_reciver where id_me_push_message_log = ${messageId}")
    void deleteMessageRecivers(@Param("messageId") Long messageId);
}

