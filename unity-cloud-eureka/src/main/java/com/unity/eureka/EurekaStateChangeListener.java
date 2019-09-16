package com.unity.eureka;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * cloud上下线通知服务
 * Created by jung at 2018年05月13日15:20:50
 */
@Component
public class EurekaStateChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(EurekaStateChangeListener.class);

    @EventListener
    public void listen(EurekaInstanceCanceledEvent eurekaInstanceCanceledEvent) {
        logger.info("断线了：{}", new Gson().toJson(eurekaInstanceCanceledEvent));
    }

    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        logger.info("服务来注册了:{}", new Gson().toJson(event));
    }

    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        logger.info("服务来更新了:{}", new Gson().toJson(event));
    }

    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {
        logger.info("服务可用了:{}", new Gson().toJson(event));

    }

    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        logger.info("server启动了:{}", new Gson().toJson(event));
    }
}  