
package com.unity.me.dao;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.base.BaseDao;
import com.unity.me.entity.PushMessageLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 推送消息和公告消息
 *
 * @author creator
 * 生成时间 2019-02-12 12:47:36
 */
public interface PushMessageLogDao extends BaseDao<PushMessageLog> {
    /**
     * 查询推送消息指定用户的组织架构
     *
     * @param userId  指定用户id
     * @param bizType 类型
     * @param offset  页数
     * @param limit   条数
     * @return 指定用户的组织架构
     * @author zhangxiaogang
     * @since 2018/12/14 20:13
     */
    @Select("<script>" +
            "SELECT DISTINCT " +
            " me.id, " +
            " me.gmt_create, " +
            " FROM_UNIXTIME( " +
            "  me.gmt_modified / 1000, " +
            "  '%Y-%m-%d %H:%I:%S' " +
            " ) AS createTime, " +
            " me.gmt_modified, " +
            " me.i_sort, " +
            " me.notes, " +
            " me.creator, " +
            " me.img_url, " +
            " me.doc_url, " +
            " me.editor, " +
            " me.text_content, " +
            " me.content, " +
            " me.title, " +
            " me.biz_type, " +
            " me.push_type, " +
            " me.record_status, " +
            " me.alias, " +
            " me.task_id, " +
            " receiver.is_read AS isRead, " +
            " me.biz_model " +
            "FROM " +
            " me_push_message_log me " +
            "LEFT JOIN me_e_message_reciver receiver ON me.id = receiver.id_me_push_message_log " +
            "WHERE " +
            " me.is_deleted = 0 " +
            "AND me.record_status = 1 " +
            "AND receiver.is_deleted = 0 " +
            "AND receiver.pbk_user_info_id = ${userId} " +
            "AND me.biz_type = ${bizType} " +
            "ORDER BY " +
            " me.gmt_modified DESC " +
            "LIMIT #{offset},#{limit}" +
            "</script>")
    List<PushMessageLog> selectPushMessageLog(@Param("userId") Long userId, @Param("bizType") Integer bizType, @Param("offset") Integer offset, @Param("limit") Integer limit);
    /**
     * 查询推送消息指定用户的组织架构
     *
     * @param userId  指定用户id
     * @param bizType 类型
     * @return 指定用户的组织架构
     * @author zhangxiaogang
     * @since 2018/12/14 20:13
     */
    @Select("SELECT DISTINCT " +
            " me.id, " +
            " me.gmt_create, " +
            " FROM_UNIXTIME( " +
            "  me.gmt_modified / 1000, " +
            "  '%Y-%m-%d %H:%I:%S' " +
            " ) AS createTime, " +
            " me.gmt_modified, " +
            " me.i_sort, " +
            " me.notes, " +
            " me.creator, " +
            " me.img_url, " +
            " me.doc_url, " +
            " me.editor, " +
            " me.text_content, " +
            " me.content, " +
            " me.title, " +
            " me.biz_type, " +
            " me.push_type, " +
            " me.record_status, " +
            " me.alias, " +
            " me.task_id, " +
            " receiver.is_read AS isRead, " +
            " me.biz_model " +
            "FROM " +
            " me_push_message_log me " +
            "LEFT JOIN me_e_message_reciver receiver ON me.id = receiver.id_me_push_message_log " +
            "WHERE " +
            " me.is_deleted = 0 " +
            "AND me.record_status = 1 " +
            "AND receiver.is_deleted = 0 " +
            "AND receiver.pbk_user_info_id = ${userId} " +
            "AND me.biz_type = ${bizType} " +
            "ORDER BY " +
            " me.gmt_modified DESC ")
    List<PushMessageLog> selectPushMessageLogByPage(Page page, @Param("userId") Long userId, @Param("bizType") Integer bizType);

    /**
     * 查询公告消息
     *
     * @param bizType 类型
     * @param offset  页数
     * @param limit   条数
     * @return 指定用户的组织架构
     * @author zhangxiaogang
     * @since 2018/12/14 20:13
     */
    @Select("<script>" +
            "SELECT DISTINCT  " +
            "  me.id,  " +
            "  me.gmt_create,  " +
            "  FROM_UNIXTIME(  " +
            "    me.gmt_modified / 1000,  " +
            "    '%Y-%m-%d %H:%I:%S'  " +
            "  ) AS createTime,  " +
            "  me.gmt_modified,  " +
            "  me.i_sort,  " +
            "  me.notes,  " +
            "  me.creator,  " +
            "  me.img_url,  " +
            "  me.doc_url,  " +
            "  me.editor,  " +
            "  me.text_content,  " +
            "  1 as isRead, " +
            "  me.content,  " +
            "  me.title,  " +
            "  me.biz_type,  " +
            "  me.push_type,  " +
            "  me.record_status,  " +
            "  me.alias,  " +
            "  me.task_id,  " +
            "  me.biz_model  " +
            "FROM  " +
            "  me_push_message_log me  " +
            "WHERE  " +
            "  me.is_deleted = 0  " +
            "AND me.record_status = 1  " +
            "AND me.biz_type = #{bizType}  " +
            "ORDER BY  " +
            "  me.gmt_modified DESC  " +
            "LIMIT #{offset},#{limit}" +
            "</script>")
    List<PushMessageLog> selectAnnouncementMessageLog(@Param("bizType") Integer bizType, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 分页查询公告消息
     *
     * @param bizType 类型
     * @return 指定用户的组织架构
     * @author zhangxiaogang
     * @since 2018/12/14 20:13
     */
    @Select("SELECT DISTINCT  " +
            "  me.id,  " +
            "  me.gmt_create,  " +
            "  FROM_UNIXTIME(  " +
            "    me.gmt_modified / 1000,  " +
            "    '%Y-%m-%d %H:%I:%S'  " +
            "  ) AS createTime,  " +
            "  me.gmt_modified,  " +
            "  me.i_sort,  " +
            "  me.notes,  " +
            "  me.creator,  " +
            "  me.img_url,  " +
            "  me.doc_url,  " +
            "  me.editor,  " +
            "  me.text_content,  " +
            "  1 as isRead, " +
            "  me.content,  " +
            "  me.title,  " +
            "  me.biz_type,  " +
            "  me.push_type,  " +
            "  me.record_status,  " +
            "  me.alias,  " +
            "  me.task_id,  " +
            "  me.biz_model  " +
            "FROM  " +
            "  me_push_message_log me  " +
            "WHERE  " +
            "  me.is_deleted = 0  " +
            "AND me.record_status = 1  " +
            "AND me.biz_type = #{bizType}  " +
            "ORDER BY  " +
            "  me.gmt_modified DESC")
    List<PushMessageLog> selectAnnouncementMessageLogByPage(Page page, @Param("bizType") Integer bizType);




}

