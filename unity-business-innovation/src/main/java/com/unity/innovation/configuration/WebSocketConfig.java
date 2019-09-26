package com.unity.innovation.configuration;

import com.unity.innovation.interceptor.MyWebSocketHandler;
import com.unity.innovation.interceptor.WebSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


/**
 * 实现接口来配置Websocket请求的路径和拦截器。
 *
 * @author gengjiajia
 * @since 2019/09/25 17:34
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //handler是webSocket的核心，配置入口
        registry.addHandler(new MyWebSocketHandler(), "/MyWebSocketHandler/{ID}").setAllowedOrigins("*").addInterceptors(new WebSocketInterceptor());
    }
}