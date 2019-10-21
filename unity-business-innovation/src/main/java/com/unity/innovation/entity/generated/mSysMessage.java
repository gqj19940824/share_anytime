package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * sys_message
 * @author G
 * 生成时间 2019-09-23 09:36:36
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mSysMessage extends BaseEntity{


        
        /**
        * 通知标题
        **/
        @CommentTarget("通知标题")
        @TableField("title")
        private String title ;
        
        
        
        /**
        * 源数据id
        **/
        @CommentTarget("源数据id")
        @TableField("source_id")
        private Long sourceId ;



        /**
         * 流程状态:status:1 新增清单/新增协同单位,2 超时未处理,3 超时未更新,4 清单删除,5 处理完毕,6 重新开启
         **/
        @CommentTarget("流程状态")
        @TableField("flow_status")
        private Integer flowStatus ;
        
        
        
        /**
        * 发送方式 0 点对点 1 广播
        **/
        @CommentTarget("发送方式 0 点对点 1 广播")
        @TableField("send_type")
        private Integer sendType ;
        
        
        
        /**
        * 单位id
        **/
        @CommentTarget("单位id")
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;


        /**
         * 数据来源归属:status:1 发改局 Development,2 企服局 company_server,3 科技局 Technology,4 组织部 organization,5 纪检组 inspection,6 宣传部 Propaganda,7 亦庄国投 investment
         **/
        @CommentTarget("数据来源归属")
        @TableField("data_source_class")
        private Integer dataSourceClass ;
}




