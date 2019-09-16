package com.unity.me.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 消息接收人员关联信息
 * @author creator
 * 生成时间 2019-02-12 12:47:33
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mMessageReciver extends BaseEntity{


        
        /**
        * 编号_推送消息记录
        **/
        @TableField("id_me_push_message_log")
        private Long idMePushMessageLog ;
        
        
        
        /**
        * 接收人
        **/
        @TableField("pbk_user_info_id")
        private Long pbkUserInfoId ;

        /**
        * 组
        **/
        @TableField("group_id")
        private Long groupId ;

        /**
        * 组
        **/
        @TableField("is_read")
        private Integer isRead ;

        

}




