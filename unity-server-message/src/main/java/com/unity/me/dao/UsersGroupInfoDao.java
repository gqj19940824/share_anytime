
package com.unity.me.dao;


import com.unity.common.base.BaseDao;
import com.unity.me.entity.UsersGroupInfo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 人员分组关系表
 * @author creator
 * 生成时间 2019-02-12 12:47:36
 */
public interface UsersGroupInfoDao  extends BaseDao<UsersGroupInfo>{

    /**
     * 查询分组以及分组成员   树形
     *
     * @param groupInfoId 分组ID
     * @return 分组下的成员
     * @author wangbin
     * @since 2019年2月18日10:49:28
     */
    @Select("SELECT * FROM  me_users_group_info mugi  \n" + "\n" + "WHERE  mugi.id_me_group_info = #{groupInfoId} AND mugi.is_deleted = 0 ")
    List<UsersGroupInfo> selectMeUserByGroupInfo(Long groupInfoId);
}

