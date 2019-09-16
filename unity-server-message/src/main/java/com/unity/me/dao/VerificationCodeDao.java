
package com.unity.me.dao;


import com.unity.common.base.BaseDao;
import com.unity.me.entity.VerificationCode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 短信验证码
 * @author liuzhen
 * 生成时间 2019-01-24 19:52:49
 */
public interface VerificationCodeDao  extends BaseDao<VerificationCode>{
    @Select("SELECT * FROM me_verification_code where phone=#{phone} and message_type=#{msgType} ORDER BY gmt_create DESC LIMIT 1 ")
    VerificationCode findFirstByPhoneNoOrderBySendTimeDesc(@Param("phone") String phone,@Param("msgType") int msgType);
}

