package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.*;

/**
 * 创新发布清单-操作日志
 * @author zhang
 * 生成时间 2019-09-21 15:45:36
 */
@Data
@Builder(builderMethodName = "newInstance")
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "ipl_log")
@EqualsAndHashCode(callSuper=false)
public class IplLog extends BaseEntity{


        /**
        * 处理状态
        **/
        @CommentTarget("处理状态")
        @TableField("deal_status")
        private Integer dealStatus ;
        
        
        
        /**
        * 处理进展
        **/
        @CommentTarget("处理进展")
        @TableField("process_info")
        private String processInfo ;



        /**
         * 主责单位id
         **/
        @CommentTarget("完成额度")
        @TableField("complete_num")
        private Double completeNum ;
        
        
        
        /**
        * 协同单位id
        **/
        @CommentTarget("协同单位id")
        @TableField("id_rbac_department_assist")
        private Long idRbacDepartmentAssist ;
        
        
        
        /**
        * 主表id
        **/
        @CommentTarget("主表id")
        @TableField("id_ipl_main")
        private Long idIplMain ;


        /**
        * 主责单位id
        **/
        @CommentTarget("主责单位id")
        @TableField("id_rbac_department_duty")
        private Long idRbacDepartmentDuty ;

        /**
         * 状态名称
         */
        @TableField(exist = false)
        private String statusName;

        /**
         *
         **/
        @CommentTarget("")
        @TableField("biz_type")
        private Integer bizType ;
}




