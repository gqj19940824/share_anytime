
package com.unity.rbac.dao;

import com.unity.common.base.BaseDao;
import com.unity.rbac.entity.UserIdentity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户身份
 * @author creator
 * 生成时间 2018-12-12 20:14:52
 */
public interface UserIdentityDao  extends BaseDao<UserIdentity> {

    /**
     * 查询用户关联的身份id信息
     *
     * @param  userId 用户id
     * @return 用户关联的身份信息
     * @author gengjiajia
     * @since 2018/12/14 19:42
     */
    @Select("SELECT i.id FROM rbac_identity i " +
            "INNER JOIN rbac_m_user_identity ui ON ui.id_rbac_identity = i.id INNER JOIN rbac_user u ON u.id = ui.id_rbac_user " +
            "WHERE u.is_deleted = 0 AND ui.is_deleted = 0 AND i.is_deleted = 0 AND u.id = #{userId}")
    List<Long> selectIdentityIdsByUserId(Long userId);

    /*
     * 查询用户关联的身份信息
     *
     * @param  userId 用户id
     * @return 用户关联的身份信息
     * @author gengjiajia
     * @since 2018/12/14 19:42
     */
    /*@Select("SELECT i.id AS id,i.notes AS notes,i.creator AS creator,i.editor AS editor,i. NAME AS NAME,i.platform AS platform,FROM_UNIXTIME(i.gmt_create/1000,'%Y-%m-%d %H:%i:%s') AS gmtCreate " +
            "FROM rbac_identity i INNER JOIN rbac_m_user_identity ui ON ui.id_rbac_identity = i.id INNER JOIN rbac_user u ON u.id = ui.id_rbac_user " +
            "WHERE u.is_deleted = 0 AND ui.is_deleted = 0 AND i.is_deleted = 0 AND u.id = #{userId} limit #{offset},#{limit}")
    List<Map<String,Object>> selectIdentityByUserId(Long userId, Long offset, Long limit);*/

    /*
     * 统计用户关联的身份数量
     *
     * @param  userId 用户id
     * @return 用户关联的身份信息
     * @author gengjiajia
     * @since 2018/12/14 19:42
     */
    /*@Select("SELECT count(i.id) " +
            "FROM rbac_identity i INNER JOIN rbac_m_user_identity ui ON ui.id_rbac_identity = i.id INNER JOIN rbac_user u ON u.id = ui.id_rbac_user " +
            "WHERE u.is_deleted = 0 AND ui.is_deleted = 0 AND i.is_deleted = 0 AND u.id = #{userId}")
    Long countIdentityByUserId(Long userId);*/
}

