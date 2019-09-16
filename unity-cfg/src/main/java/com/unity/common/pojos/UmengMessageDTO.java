package com.unity.common.pojos;

import com.google.common.collect.Maps;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author wangbin
 * @since 2018/11/6
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class UmengMessageDTO {

    /**
     * 设备号
     */
    private String deviceToken;
    /**
     * 推送标题
     */
    private String title;
    /**
     * 仅当用户使用android为ticker（通知文字描述 ）
     * ios时为body 同上
     */
    private String text;
    /**
     * 仅当用户使用ios时有效（通知栏提示文字）
     * 仅用户使用android为ticker（通知栏提示文字）
     */
    private String subTitle;
    /**
     * 用户别名一般为用户id
     */
    private String alias;

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

    // 用户自定义参数 key值不可以是"type"和"target"
    private Map<String, String> extraField;

    //组装参数
    public Map<String, String> getExtraField() {
        extraField = Maps.newHashMap();
        extraField.put("docType", this.getDocType());
        extraField.put("targetId", this.getTargetId());
        extraField.put("column", this.getColumn());
        return extraField;
    }

    /**
     * 通用参数校验
     *
     * @return 校验结果
     * @author zhangxiaogang
     * @since 2019/3/15 16:12
     */
    public String valid() {
        if (StringUtils.isBlank(title)) {
            return "消息标题不能为空";
        } else if (StringUtils.isBlank(text)) {
            return "消息内容不能为空";
        }
        return null;
    }

    /**
     * 指定用户推送参数校验
     *
     * @return 校验结果
     * @author zhangxiaogang
     * @since 2019/3/15 16:15
     */
    public String validCustomizedcast() {
        String valid = valid();
        if (valid != null) {
            return valid;
        }
        if (StringUtils.isBlank(alias)) {
            return "推送用户不能为空";
        }
        return null;
    }

    /**
     * 单播参数校验
     *
     * @return 校验结果
     * @author zhangxiaogang
     * @since 2019/3/15 16:15
     */
    public String validUnicast() {
        String valid = valid();
        if (valid != null) {
            return valid;
        }
        if (StringUtils.isBlank(deviceToken)) {
            return "单播设备号不能为空";
        }
        return null;
    }


}
