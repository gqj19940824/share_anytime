package com.unity.me.controller.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.unity.common.util.GsonUtils;
import com.unity.me.client.vo.MessageVO;
import com.unity.me.entity.PushMessageLog;
import com.unity.me.service.MessageSendServiceImpl;
import com.unity.me.service.PushMessageLogServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 跨服务的消息调用
 * <p>
 * create by zhangxiaogang at 2019/3/16 15:33
 */
@RestController
@RequestMapping("/feign/message")
@Slf4j
public class MessageFeignController {
    private final MessageSendServiceImpl messageSendService;
    private final PushMessageLogServiceImpl pushMessageLogService;

    public MessageFeignController(MessageSendServiceImpl messageSendService, PushMessageLogServiceImpl pushMessageLogService) {
        this.messageSendService = messageSendService;
        this.pushMessageLogService = pushMessageLogService;
    }


    /**
     * 获取系统通知信息数量
     *
     * @return 系统通知数量
     * @author zhangxiaogang
     * @since 2019/4/25 16:30
     */
    @GetMapping("/getSystemMessageInfos")
    public Map<String, Object> getSystemMessageInfos() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("messageCount", pushMessageLogService.count());
        return map;
    }

    /**
     * 推送消息给该用户，如果已经根据id查询出了用户信息，请调用下面的重载方法
     *
     * @param messageVO 消息参数
     * @return true:发送成功 false:发送失败
     * @author wangbin
     * @since 2019年2月14日16:29:17
     */
    @PostMapping("/sendUmengCustomizedcast")
    public boolean sendUmengCustomizedcast(@RequestBody MessageVO messageVO) {
        String valid = messageVO.validCustomizedcast();
        if (valid != null) {
            log.info("校验结果：" + valid);
            return false;
        }
        return messageSendService.sendUmengCustomizedcast(messageVO);
    }

    /**
     * 广播消息全部用户
     *
     * @param messageVO 消息标题
     * @return true:发送成功 false:发送失败
     * @author wangbin
     * @since 2019年3月3日14:00:07
     */
    @PostMapping("/sendBroadcast")
    public boolean sendBroadcast(@RequestBody MessageVO messageVO) {
        String valid = messageVO.valid();
        if (valid != null) {
            log.info("校验结果：" + valid);
            return false;
        }
        log.error("推送：" + GsonUtils.format(messageVO));
        messageSendService.sendBroadcastMessage(messageVO);
        return true;
    }
}
