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
         * 数据来源:status:1 新增清单 add1,2 新增清单协同 add2,3 清单超时未处理 timeout1,4 清单协同超时未处理 timeout2,5 清单超时未更新进展 no_update1,6 清单协同超时未更新进展 no_update2,7 主责单位再次编辑基本信息 update,8 主责单位删除 delete,9 处理中->处理完毕 complete,10 处理完毕->处理中 continued
         **/
        @CommentTarget("数据来源")
        @TableField("data_source")
        private Integer dataSource ;
        
        
        
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




