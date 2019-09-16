package com.unity.common.client.vo;


import com.unity.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 系统配置
 * <p>
 * create by zhangxiaogang at 2019/3/3 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CfgVO extends BaseEntity {


    /**
     * 类型
     **/
    private String cfgType;


    /**
     * 值
     **/
    private String cfgVal;


}




