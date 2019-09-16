package com.unity.common.pojos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 当前登录人信息
 * <p>
 * @author gengjiajia
 * @since 2018/12/21 13:45
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Customer extends AuthUser {
    /**
     * 按钮资源编码列表
     */
    public List<String> buttonCodeList;

    /**
     * 菜单资源编码列表
     */
    public List<String> menuCodeList;

    /**
     * 用户拥有的角色
     */
    public List<Long> roleList;

    /**
     * 账号级别
     */
    public Integer accountLevel;

    /**
     * 项目id
     */
    public Long idInfoProject;

    /**
     * 数据权限id列表
     */
    public List<Long> dataPermissionIdList;

    /**
     * 是否超级管理员
     */
    public Integer isSuperAdmin;

    /**
     * 是否管理员
     */
    public Integer isAdmin;
}