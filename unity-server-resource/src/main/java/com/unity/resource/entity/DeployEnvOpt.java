package com.unity.resource.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.resource.entity.generated.mDeployEnvOpt;
import com.unity.resource.enums.OptTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "re_deploy_env_opt")
public class DeployEnvOpt extends mDeployEnvOpt {


    /**
     * 校验数据
     *
     * @return 校验结果
     * @author zhangxiaogang
     * @since 2019/9/9 10:52
     */
    public String valid() {
        if (StringUtils.isEmpty(getName())) {
            return "操作名称不能为空";
        }
        if (StringUtils.isEmpty(getServerName())) {
            return "服务名称不能为空";
        }

        if (getOptType() == null || OptTypeEnum.of(getOptType()) == null) {
            return "操作类型不能为空";
        }
        if (OptTypeEnum.JAR_TYPE.getId().equals(getOptType()) && StringUtils.isEmpty(getFilePath()) && StringUtils.isEmpty(getFileInfo())) {
            return "文件路径不能为空";
        }
        return null;
    }


}

