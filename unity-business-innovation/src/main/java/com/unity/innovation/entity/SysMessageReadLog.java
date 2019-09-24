package com.unity.innovation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.innovation.entity.generated.mSysMessageReadLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_message_read_log")
public class SysMessageReadLog extends mSysMessageReadLog {


}

