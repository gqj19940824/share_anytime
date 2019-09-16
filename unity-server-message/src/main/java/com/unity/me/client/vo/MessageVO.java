package com.unity.me.client.vo;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 消息参数
 * <p>
 * create by zhangxiaogang at 2019/3/4 10:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "newInstance")
public class MessageVO {
    private List<Long> appUserIds;//接收消息的用户
    private String title;//消息标题
    private String text;//消息内容
    private String subTitle;//副标题，仅当用户使用ios时起作用
    //private ExtraFieldVO extraFieldVO;//个性化参数
    //以下为自定义字段
    /**
     * 类型:10 富文本 rich_text,20 pdf pdf,30 视频 video,40 音频 audio,50 动态 book
     */
    private String docType;

    /**
     * 栏目code
     */
    private String column;

    /**
     * 调用目标查询详情的id
     */
    private String targetId;

    private boolean flag;//是否保存日志

    private Map<String, String> extraField;//自定义参数

    public Map<String, String> getExtraField() {
        this.extraField = Maps.newHashMap();
        this.extraField.put("docType", this.getDocType());
        this.extraField.put("targetId", this.getTargetId());
        this.extraField.put("column", this.getColumn());
        return this.extraField;
    }


    /**
     * 校验消息推送参数
     *
     * @author zhangxiaogang
     * @since 2019/3/4 10:53
     */
    public String valid() {
        if (StringUtils.isEmpty(title)) {
            return "消息标题不能为空";
        }
        if (StringUtils.isEmpty(text)) {
            return "消息内容不能为空";
        }
        return null;
    }

    /**
     * 校验消息推送参数
     *
     * @author zhangxiaogang
     * @since 2019/3/4 10:53
     */
    public String validCustomizedcast() {
        if (appUserIds.isEmpty()) {
            return "用户id不能为空";
        }
        String valid = valid();
        if (valid != null) {
            return valid;
        }

        return null;
    }

}
