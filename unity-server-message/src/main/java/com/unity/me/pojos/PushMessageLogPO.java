package com.unity.me.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 消息跟人员  关系
 *
 * <p>
 * create by wangbin at 2019年2月12日15:45:19
 */
@AllArgsConstructor
@Data
public class PushMessageLogPO {

    /**
     * ID
     **/
    private Long id;

    /**
     * 文本消息内容
     **/
    private String textContent ;



    /**
     * 消息内容
     **/
    private String content ;

    /**
     * 图片
     **/
    private String imgUrl ;



    /**
     * 标题
     **/
    private String title ;



    /**
     * 是否已读:flag:1 是,0 否
     **/
    private Integer isRead ;



    /**
     * 业务类型:status:1 推送 push,2 公告 announcement, 3其它 other
     **/
    private Integer bizType ;



    /**
     * 发送类型:status:1 单播 unicast,2 列播 listcast,3 文件播 filecast,4 广播 broadcast,5 组播 groupcast,6 自定义播 customizedcast
     **/
    private Integer pushType ;



    /**
     * 操作系统:status:1 安卓 android,2 iOS iOS
     **/
    private Integer os ;



    /**
     * 发送状态:status:1 成功 success,-1 失败 fail
     **/
    private Integer recordStatus ;



    /**
     * 自有账号
     **/
    private String alias ;



    /**
     * 任务
     **/
    private Long taskId ;



    /**
     * 业务模块
     **/
    private Integer bizModel;

    /**
     * 备注
     **/
    private String notes ;

    /**
     * 分组信息
     **/
    private List<UserGroupInfoPO> userGroupInfoPOS ;

    /**
     * 接收人集合
     */
    private List<Long> messageReceiverList;


    public PushMessageLogPO(){}
}
