package com.unity.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.rbac.entity.generated.mDepartment;
import lombok.*;

import java.util.List;


/**
 * @author gengjiajia
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "rbac_department")
@EqualsAndHashCode(callSuper=false)
public class Department extends mDepartment{

    /**
     * 子机构idList//子机构idList
     */
    @TableField(exist=false)
    private List<Long> childIdList;

    @TableField(exist = false)
    private List<User> users;

    @TableField(exist = false)
    private Integer accountLevel;
    /**
     * 0:上移 1：下移
     */
    @TableField(exist = false)
    private Integer upOrDown;

    /**
     * 列表展示，是否有编辑/删除 按钮
     * 1：有
     * 0：没有
     **/
    @TableField(exist = false)
    private Integer operationButton ;
}

