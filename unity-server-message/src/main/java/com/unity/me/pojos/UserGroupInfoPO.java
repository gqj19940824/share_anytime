package com.unity.me.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分组ID与人员ID
 *
 * <p>
 * create by wangbin at 2019年2月13日16:12:04
 */
@AllArgsConstructor
@Data
public class UserGroupInfoPO {

    /**
     * ID
     **/
    private Long id;

    /**
     * 组ID
     **/
    private Long groupId;

    /**
     *  人员ID
     **/
    private Long userId;

    public UserGroupInfoPO(){}
}
