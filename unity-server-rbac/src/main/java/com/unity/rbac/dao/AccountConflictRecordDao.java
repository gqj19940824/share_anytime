
package com.unity.rbac.dao;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.AccountConflictRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 账号冲突记录
 *
 * @author zhang
 * 生成时间 2019-07-25 18:51:37
 */
public interface AccountConflictRecordDao extends BaseDao<AccountConflictRecord> {

    /**
     * 查询冲突账号记录列表
     *
     * @param  record 条件参数
     * @param pageable 分页参数
     * @return 冲突账号记录列表
     * @author gengjiajia
     * @since 2019/07/26 15:22
     */
    @Select("<script> " +
            "SELECT " +
            " acr.*, u.phone, " +
            " u.source, " +
            " u.perfect_status, " +
            " u.account_level, " +
            " u.gmt_create AS userGmtCreate, " +
            " u.`name`, " +
            " d.`name` AS department " +
            "FROM " +
            " rbac_account_conflict_record acr " +
            "INNER JOIN rbac_user u ON acr.local_id = u.id " +
            "LEFT JOIN rbac_department d ON d.id = u.id_rbac_department " +
            "WHERE " +
            " acr.is_deleted = 0 " +
            " <if test='record.localLoginName != null'> " +
            "AND acr.local_login_name LIKE CONCAT('%',#{record.localLoginName},'%') " +
            " </if> " +
            " <if test='record.ucsSource != null'> " +
            "AND acr.ucs_source = #{record.ucsSource} " +
            " </if> " +
            "ORDER BY acr.gmt_create DESC " +
            "</script> ")
    List<AccountConflictRecord> findAccountConflictRecordListByPage(@Param("record") AccountConflictRecord record, @Param("pageable") Page pageable);
}

