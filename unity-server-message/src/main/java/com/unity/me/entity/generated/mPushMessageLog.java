package com.unity.me.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 推送消息和公告消息
 * @author creator
 * 生成时间 2019-03-03 14:44:16
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mPushMessageLog extends BaseEntity{



        /**
         * 文本消息内容
         **/
        @TableField("text_content")
        private String textContent ;



        /**
         * 消息内容
         **/
        @TableField("content")
        private String content ;



        /**
         * 标题
         **/
        @TableField("title")
        private String title ;



        /**
         * 业务类型:status:1 推送 push,2 公告 announcement,其它 other
         **/
        @TableField("biz_type")
        private Integer bizType ;



        /**
         * 发送类型:status:1 单播 unicast,2 列播 listcast,3 文件播 filecast,4 广播 broadcast,5 组播 groupcast,6 自定义播 customizedcast
         **/
        @TableField("push_type")
        private Integer pushType ;



        /**
         * 发送状态:status:0 草稿 draft,1 已发布 success
         **/
        @TableField("record_status")
        private Integer recordStatus ;



        /**
         * 自有账号
         **/
        @TableField("alias")
        private String alias ;



        /**
         * 任务
         **/
        @TableField("task_id")
        private Long taskId ;



        /**
         * 业务模块
         **/
        @TableField("biz_model")
        private Integer bizModel ;



        /**
         * 图片
         **/
        @TableField("img_url")
        private String imgUrl ;

        /**
         * 文档地址
         **/
        @TableField("doc_url")
        private String docUrl;

}




