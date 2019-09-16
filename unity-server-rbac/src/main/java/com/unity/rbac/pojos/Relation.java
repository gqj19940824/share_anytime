package com.unity.rbac.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关系维护 数据载体
 *
 * <p>
 * create by gengjiajia at 2018/12/17 16:34
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Relation {

    /**
     * 绑定关系主键
     */
    private Long id;

    /**
     * 类型 1 角色ID  2 用户ID
     */
    private Integer type;

    /**
     * 要分配的资源id集
     */
    private Long[] bindResourceIds;

    /**
     * 要分配的角色id集
     */
    private Long[] bindRoleIds;

    /**
     * 要排除的资源id集
     */
    private Long[] excludeResourceIds;

}
