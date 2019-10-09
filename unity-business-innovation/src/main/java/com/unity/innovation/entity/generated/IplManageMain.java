package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplEsbMain;
import com.unity.innovation.entity.IplSupervisionMain;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import java.util.List;

/**
 * 创新发布清单-发布管理主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:37
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "ipl_manage_main")
public class IplManageMain extends BaseEntity{

        /**
        * 标题
        **/
        @CommentTarget("标题")
        @TableField("title")
        private String title ;
        
        
        
        /**
        * 状态
        **/
        @CommentTarget("状态")
        @TableField("status")
        private Integer status ;
        
        
        
        /**
        * 附件
        **/
        @CommentTarget("附件")
        @TableField("attachment_code")
        private String attachmentCode ;
        
        
        
        /**
        * 单位id
        **/
        @CommentTarget("主责单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty ;
        
        
        
        /**
        * 发布结果
        **/
        @CommentTarget("发布结果")
        @TableField("publish_result")
        private String publishResult ;

        /**
         * 提请时间
         **/
        @TableField("gmt_submit")
        private Long gmtSubmit ;
        

        /**
        * 发改局列表
        **/
        @CommentTarget("发改局列表ID")
        @TableField(exist = false)
        private List<Long> idiplDarbMains ;

        /**
         * 附件集合
         **/
        @TableField(exist = false)
        private List<Attachment> attachments;

        /***
         *是否提交 1:提交 0:不提交
         */
        @TableField(exist = false)
        private Integer isCommit;

        /**
         * 清亲政商基础数据集合
         **/
        @TableField(exist = false)
        private List<IplSupervisionMain> supervisionMainList;

        /**
         * 快照json数据
         **/
        @TableField("snapshot")
        private String snapshot;

        /**
         * 清亲政商基础数据id
         **/
        @TableField(exist = false)
        private Long idIplSupervisionMain;

        /**
         * 页面查询用提交时间
         **/
        @TableField(exist = false)
        private String createTime;

        /**
         * 状态名称
         **/
        @TableField(exist = false)
        private String statusName;


        /**
         * 企服局基础数据集合
         **/
        @CommentTarget("企业创新发展数据集合")
        @TableField(exist = false)
        private List<IplEsbMain> iplEsbMainList;

}




