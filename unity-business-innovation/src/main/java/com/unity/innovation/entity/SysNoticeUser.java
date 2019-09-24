package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mSysNoticeUser;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "sys_m_notice_user")
public class SysNoticeUser extends mSysNoticeUser{

    /**
     * 单位名称
     * */
    @TableField(exist = false)
    private String departmentName ;

    /**
     * 用户名称
     * */
    @TableField(exist = false)
    private String userName ;

    /**
     * 浏览情况
     * */
    @TableField(exist = false)
    private String isReadName ;


}

