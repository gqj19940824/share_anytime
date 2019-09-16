package com.unity.me.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 短信验证码
 * @author creator
 * 生成时间 2019-01-24 19:52:49
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mVerificationCode extends BaseEntity{
        /**
        * 手机号
        **/
        @TableField("phone")
        private String phone ;

        /**
        * 验证码（6位）
        **/
        @TableField("verification_code")
        private String verificationCode ;
        
        /**
        * 消息类型:status:10 登录 login,20 注册 registration,30 找回密码 forgottenPassword,40 哨声通知 enterpriseWhistle
        **/
        @TableField("message_type")
        private Integer messageType ;


        /**
         * 有效时间(分钟)
         **/
        @TableField("i_expiry_time")
        private Integer expiryTime ;
        
}




