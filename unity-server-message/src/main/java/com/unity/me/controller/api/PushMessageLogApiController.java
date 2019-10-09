package com.unity.me.controller.api;

import com.google.common.collect.Maps;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.pojos.UmengMessageDTO;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.me.client.CmsClient;
import com.unity.me.client.RbacMeClient;
import com.unity.me.entity.MessageReciver;
import com.unity.me.entity.PushMessageLog;
import com.unity.me.pojos.PushMessageLogPO;
import com.unity.me.service.MessageReciverServiceImpl;
import com.unity.me.service.PushMessageLogServiceImpl;
import com.unity.me.umeng.MessageSend;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author wangbin
 * @since 2019/2/21
 */
@RestController
@RequestMapping("api/messageLog")
public class PushMessageLogApiController extends BaseWebController {

    @Autowired
    PushMessageLogServiceImpl service;
    @Autowired
    MessageReciverServiceImpl messageReceiverService;
    @Autowired
    CmsClient cmsClient;
    @Autowired
    RbacMeClient rbacClient;

    /**
     * 前端用户查询推送消息或公共消息列表
     *
     * @param pageEntity 当前页面
     * @return 查询结果
     * code 0 success
     * -1013 参数为空
     * @author zhangxiaogang
     * @since 2019/4/16 15:47
     */
    @PostMapping("selectPushMessageLogList")
    public Mono<ResponseEntity<SystemResponse<Object>>> selectPushMessageLogList(@RequestBody PageEntity<PushMessageLog> pageEntity) {
        if (pageEntity.getEntity() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "参数为空");
        }
        LoginContextHolder.getRequestAttributes();
        PushMessageLog entity = pageEntity.getEntity();
        List<PushMessageLog> pushMessageLogs = service.selectPushMessageLogList(pageEntity.getPageable(), entity.getBizType());
        return success(PageElementGrid.<Map<String, Object>>newInstance()
                .total(pageEntity.getPageable().getTotal())
                .items(convert2List(pushMessageLogs)).build());
    }

    /**
     * 前端用户查询推送消息或公共消息详情
     *
     * @param id 消息id
     * @return 查询结果
     * code 0 success
     * -1013 参数为空/未查询到该消息内容
     * @author zhangxiaogang
     * @since 2019/4/16 15:47
     */
    @GetMapping("findPushMessageLogById")
    public Mono<ResponseEntity<SystemResponse<Object>>> findPushMessageLogById(Long id) {
        if (id == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "参数为空");
        }
        LoginContextHolder.getRequestAttributes();
        PushMessageLog pushMessageLog = service.getById(id);
        if (pushMessageLog == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未查询到该消息内容");
        }
        return success(JsonUtil.<PushMessageLog>ObjectToMap(pushMessageLog,
                new String[]{"id", "textContent", "content", "title", "docUrl"},
                (m, entity) -> {
                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtModified()));
                }
        ));
    }


    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return 处理后参数
     * @author zhangxiaogang
     * @since 2019/4/16 15:52
     */
    private List<Map<String, Object>> convert2List(List<PushMessageLog> list) {
        return JsonUtil.<PushMessageLog>ObjectToList(list,
                new String[]{"id", "textContent", "content", "title", "docUrl"},
                (m, entity) -> {
                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtModified()));
                }
        );
    }

    /**
     * 标记为已读
     *
     * @param pushMessageLogPO 推送消息和公告消息实体
     * @return
     */
    @PostMapping("/messageIsRead")
    public Mono<ResponseEntity<SystemResponse<Object>>> messageIsRead(@RequestBody PushMessageLogPO pushMessageLogPO) {
        if (pushMessageLogPO.getId() == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "消息为空");
        }
        PushMessageLog pushMessageLog = service.getById(pushMessageLogPO.getId());
        if (pushMessageLog == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "消息不存在");
        }
        if (pushMessageLog.getRecordStatus() == 1) {
            Customer customer = LoginContextHolder.getRequestAttributes();
            List<MessageReciver> messageRecivers = messageReceiverService.selectReceiverByUserIdAndMessageId(customer.getId(), pushMessageLog.getId());
            //接收人不为空
            if (CollectionUtils.isNotEmpty(messageRecivers)) {
                messageRecivers.forEach(messageReciver -> {
                    messageReciver.setIsRead(1);
                });
                messageReceiverService.updateBatchById(messageRecivers);
            }
        }
        return success(InnovationConstant.SUCCESS);
    }


    /**
     * 当前登陆用户是否有未读消息
     *
     * @return
     */
    @PostMapping("/messageIsReadCount")
    public Mono<ResponseEntity<SystemResponse<Object>>> messageIsReadCount() {
        Customer customer = LoginContextHolder.getRequestAttributes();
        Map<String, Long> map = Maps.newHashMap();
        //消息未读数量
        Long pushIsRead = messageReceiverService.selectReceiverByMessageIdAndUserId(customer.getId(), 1);
        //公告未读数量
        Long noticeIsRead = messageReceiverService.selectReceiverByMessageIdAndUserId(customer.getId(), 2);
        map.put("pushIsRead", pushIsRead);
        map.put("noticeIsRead", noticeIsRead);
        //接收人不为空
        return success(map);
    }

    /**
     * 当前登陆用户是否有未读消息
     *
     * @return
     */
    @PostMapping("/selectUserByIds")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> selectUserByIds(@RequestBody Map<String, List<Long>> map) {
        LoginContextHolder.getRequestAttributes();
        List<Long> ids = map.get("ids");
        if (CollectionUtils.isEmpty(ids)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "用户ID为空");
        }
        //接收人不为空
        return success(rbacClient.findUserInfoByIdIn(ids));
    }

    /**
     * 指定推送test
     *
     * @author zhangxiaogang
     * @since 2019/3/15 15:39
     */
    @GetMapping("sendTestMessageAndroid")
    public void sendTestMessageAndroid() {
        UmengMessageDTO umengMessageDTO = UmengMessageDTO.newInstance()
                .subTitle("你是好人哈哈。。。")
                .title("奖章领取")
                .text("恭喜你获取1000000万大奖，哈哈哈哈")
                .column("2032432")
                .targetId("1")
                .docType("10")
                .alias("46,13")
                .build();
        MessageSend.getInstence().sendCustomizedcast(umengMessageDTO);
    }

    /**
     * 广播消息
     *
     * @author zhangxiaogang
     * @since 2019/3/15 15:39
     */
    @GetMapping("sendTestMessageAndroidAndIOS")
    public void sendTestMessageAndroidAndIOS() {
        UmengMessageDTO umengMessageDTO = UmengMessageDTO.newInstance()
                .subTitle("你是好人哈哈。。。")
                .title("奖章领取")
                .text("恭喜你获取1000000万大奖，哈哈哈哈")
                .column("2032432")
                .targetId("1")
                .docType("10")
                .build();
        MessageSend.getInstence().sendBroadcast(umengMessageDTO);
    }
}
