package com.unity.me.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 分组与人员  关系
 *
 * <p>
 * create by wangbin at 2019年2月13日16:12:04
 */
@AllArgsConstructor
@Data
public class GroupInfoPO {

    /**
     * ID
     **/
    private Long id;

    /**
     * 组名
     **/
    private String groupName;

    /**
     * 备注
     */
    private String notes;


    private List<Long> usersGroupInfoList;

    public GroupInfoPO(){}
}
