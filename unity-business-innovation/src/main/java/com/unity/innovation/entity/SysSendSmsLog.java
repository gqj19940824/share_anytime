package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mSysSendSmsLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_send_sms_log")
public class SysSendSmsLog extends mSysSendSmsLog {


}

