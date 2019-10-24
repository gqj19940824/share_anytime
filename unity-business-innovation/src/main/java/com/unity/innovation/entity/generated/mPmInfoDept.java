package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 企业信息发布管理
 * @author zhang
 * 生成时间 2019-10-15 15:33:01
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mPmInfoDept extends BaseEntity{


        
        /**
        * 标题
        **/
        @CommentTarget("标题")
        @TableField("title")
        private String title ;
        
        
        
        /**
        * 提交时间
        **/
        @CommentTarget("提交时间")
        @TableField("gmtSubmit")
        private Long gmtSubmit ;
        
        
        
        /**
        * 状态
        **/
        @CommentTarget("状态")
        @TableField("status")
        private Integer status ;
        
        
        
        /**
        * 附件码
        **/
        @CommentTarget("附件码")
        @TableField("attachment_code")
        private String attachmentCode ;
        
        
        
        /**
        * 单位id
        **/
        @CommentTarget("单位id")
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        
        
        /**
        * 业务类型
        **/
        @CommentTarget("业务类型")
        @TableField("biz_type")
        private Long bizType ;


        /**
         * 二次打包id
         **/
        @CommentTarget("二次打包id")
        @TableField("id_ipa_main")
        private Long idIpaMain ;
}




