package com.unity.innovation.interceptor;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unity.common.util.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MyWebSocketHandler implements WebSocketHandler {

    /**
     * 在线用户列表
     */
    private static final Map<String, WebSocketSession> USERS = Maps.newHashMap();

    /**
     * 新增socket
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("===== 《MyWebSocketHandler》 成功建立连接 =====");
        String id = session.getUri().toString().split("ID=")[1];
        id = id.split("\\?token")[0];
        if (id != null) {
            USERS.put(id, session);
            session.sendMessage(new TextMessage("Successfully created Socket connection"));
        }
        log.info("===== 《WebSocket》 当前在线人数： {}", USERS.size());
    }

    /**
     * 接收socket信息
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        try {
            String format = GsonUtils.format(webSocketMessage.getPayload());
            Map<String, Object> map = GsonUtils.map(format);
            log.info("===== 《MyWebSocketHandler》 服务器接收socket信息 {}", format);
            if (map.get("id") != null && map.get("message") == null) {
                //获取当前用户的需求数，返回给后台页面
                sendMessageToUser(map.get("id") + "", new TextMessage("{\"sysMessageNum\":0,\"noticeNum\":0}"));
            } else if (map.get("id") != null && map.get("message") != null) {
                //心跳检测
                sendMessageToUser(map.get("id") + "", new TextMessage("pang"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送信息给指定用户
     *
     * @param clientId 客户端标识
     * @param message  要发送的消息
     * @return 发送结果是否成功
     */
    public boolean sendMessageToUser(String clientId, TextMessage message) {
        if (USERS.get(clientId) == null) {
            return false;
        }
        WebSocketSession session = USERS.get(clientId);
        if (!session.isOpen()) {
            return false;
        }
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 广播信息
     *
     * @param message 消息
     * @return 是否成功
     */
    public boolean sendMessageToAllUsers(TextMessage message) {
        boolean allSendSuccess = true;
        Set<String> clientIds = USERS.keySet();
        WebSocketSession session;
        for (String clientId : clientIds) {
            try {
                session = USERS.get(clientId);
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                allSendSuccess = false;
            }
        }
        return allSendSuccess;
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        log.info("===== 《MyWebSocketHandler》 socket连接出错 =====");
        USERS.remove(getClientId(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("===== 《MyWebSocketHandler》 socket连接已关闭 {}", status);
        USERS.remove(getClientId(session));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取用户标识
     *
     * @param session session
     * @return 用户标识
     */
    private Integer getClientId(WebSocketSession session) {
        try {
            return (Integer) session.getAttributes().get("WEBSOCKET_USERID");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前登陆后台用户id集合
     *
     * @return 当前登陆后台用户信息
     * @author zhangxiaogang
     * @since 2019/5/7 13:48
     */
    public List<Long> getUserIds() {
        List<Long> list;
        if (MapUtils.isNotEmpty(USERS)) {
            list = USERS.entrySet().stream()
                    .filter(user -> NumberUtils.isDigits(user.getKey()))
                    .map(user -> Long.valueOf(user.getKey()))
                    .collect(Collectors.toList());
        } else {
            list = Lists.newArrayList();
        }
        return list;
    }
}