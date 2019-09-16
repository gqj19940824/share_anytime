package com.unity.rbac.pojos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * <p>
 * create by gengjiajia at 2019/07/18 15:05
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TreeNodeAttr {

    private String notes;

    private String resourceUrl;

    private Integer level;

    private Integer resourceType;
}
