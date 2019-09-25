package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mSysNoticeDepartment;
import com.unity.innovation.entity.generated.mSysNoticeUser;
import lombok.*;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "sys_m_notice_department")
public class SysNoticeDepartment extends mSysNoticeDepartment {

    /**
     * 单位名称
     * */
    @TableField(exist = false)
    private String departmentName ;

}

