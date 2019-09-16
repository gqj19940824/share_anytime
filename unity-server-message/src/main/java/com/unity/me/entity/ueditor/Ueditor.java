package com.unity.me.entity.ueditor;

import lombok.Data;

/**
 * @author wangbin
 * @since 2019/3/3
 */
@Data
public class Ueditor {
    private Long id ;
    private String content ;
    private Integer type;
    private String distinguish;
    public Ueditor(){}
}
