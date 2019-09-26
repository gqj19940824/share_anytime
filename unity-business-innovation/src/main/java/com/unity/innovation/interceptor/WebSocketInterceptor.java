package com.unity.innovation.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author gengjiajia
 */
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    /**
     * 在握手之前执行该方法, 继续握手返回true, 中断握手返回false. 通过attributes参数设置WebSocketSession的属性
     *
     * @author gengjiajia
     * @since 2019/09/25 17:35
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            String id = request.getURI().toString().split("ID=")[1];
            id = id.split("\\?token")[0];
            log.info("===== 《WebSocketInterceptor》 当前session的ID {}", id);
            attributes.put("WEBSOCKET_USERID", id);
        }
        return true;
    }

    /**
     * 完成握手之后执行该方法
     *
     * @author gengjiajia
     * @since 2019/09/25 17:36
     */
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        //System.out.println("进来webSocket的afterHandshake拦截器！");
    }
}