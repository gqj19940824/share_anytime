package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.IplSupervisionMain;
import lombok.*;

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
        * 二次打包id
        **/
        @CommentTarget("二次打包id")
        @TableField("id_ipa_main")
        private Long idIpaMain ;

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
        private String submitTime;

        /**
         * 状态名称
         **/
        @TableField(exist = false)
        private String statusName;

        /**
         * 处理记录
         * */
        @TableField(exist = false)
        private List<IplmManageLog> logList;

        /**
         * 处理节点
         * */
        @TableField(exist = false)
        private List<IplmManageLog> processNodeList;

        /**
         * 数据集合
         **/
        @CommentTarget("数据集合")
        @TableField(exist = false)
        private List dataList;

        /**
         * 科技局基础数据集合
         **/
        @CommentTarget("科技局数据集合")
        @TableField(exist = false)
        private List<IplSatbMain> iplSatbMainList;

        /**
         * 企服局基础数据集合
         **/
        @CommentTarget("企服局数据集合")
        @TableField(exist = false)
        private List<IplDarbMain> iplDarbMainList;

        /**
         * 反给页面的快照数据
         **/
        @TableField(exist = false)
        private List snapShotList;

        /**
         * 通过/驳回  1:通过 0：驳回
         **/
        @TableField(exist = false)
        private Integer passOrReject;

        /**
         * 审批意见
         **/
        @TableField(exist = false)
        private String comment;

        /**
         * 查询类型
         **/
        @CommentTarget("查询类型")
        @TableField(exist = false)
        private String category;

        /**
         * 清单类型
         **/
        @CommentTarget("清单类型")
        @TableField("biz_type")
        private Integer bizType ;

}




