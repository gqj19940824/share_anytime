package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.CommentTarget;
import com.unity.common.client.vo.DepartmentVO;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mSysNotice;

import java.util.List;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "sys_notice")
public class SysNotice extends mSysNotice{


    /**
     * 单位id
     **/
    @CommentTarget("单位id")
    @TableField(exist = false)
    private List<Long> departmentIds ;

    /**
     * 单位集合
     * */
    @TableField(exist = false)
    private List<DepartmentVO> departmentList ;


    /**
     * 附件列表
     **/
    @CommentTarget("附件列表")
    @TableField(exist = false)
    private List<Attachment> attachmentList ;

    /**
     * 列表查询开始时间
     * */
    @TableField(exist = false)
    private Long gmtStart ;

    /**
     * 列表查询结束时间
     * */
    @TableField(exist = false)
    private Long gmtEnd ;

    @TableField(exist = false)
    private Long userId ;

    @TableField(exist = false)
    private Integer isRead ;
  
}

