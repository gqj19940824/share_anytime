package com.unity.rbac.pojos;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 树节点
 * <p>
 * create by gengjiajia at 2019/07/18 14:49
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TreeNode {

    /**节点id*/
    private String id;
    /**节点名称*/
    private String text;
    /**节点选中状态*/
    private Boolean checked;
    /**是否父级节点*/
    private boolean isParent;
    /**是否禁用*/
    private boolean chkDisabled;
    /** treeNode 节点的 展开 / 折叠 状态*/
    private boolean open;
    /**级次编码*/
    private String gradationCode;
    /**icon样式*/
    private String iconSkin;
    /**icon地址*/
    private String icon;
    /**节点的子节点数据集合*/
    @Builder.Default
    private List<TreeNode> children = Lists.newArrayList();
    /**附加属性*/
    private TreeNodeAttr attr;
}
