package com.unity.admin.util;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunctions;
import de.codecentric.boot.admin.server.web.client.LegacyEndpointConverters;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.boot.actuate.endpoint.http.ActuatorMediaType;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 *支持https请求
 *<p>
 *create by zhangxiaogang at 2019/7/15 9:17
 */
public class InstanceWebClient {
    private final WebClient webClient;

    public InstanceWebClient(HttpHeadersProvider httpHeadersProvider) {
        this(httpHeadersProvider, Duration.ofSeconds(2), Duration.ofSeconds(5));
    }

    public InstanceWebClient(HttpHeadersProvider httpHeadersProvider, Duration connectTimeout, Duration readTimeout) {
        this(httpHeadersProvider, connectTimeout, readTimeout, builder -> { });
    }

    public InstanceWebClient(HttpHeadersProvider httpHeadersProvider,
                             Duration connectTimeout,
                             Duration readTimeout,
                             WebClientCustomizer customizer) {
        this(createDefaultWebClient(connectTimeout, readTimeout, customizer), httpHeadersProvider);
    }

    public InstanceWebClient(WebClient webClient, HttpHeadersProvider httpHeadersProvider) {
        this.webClient = webClient.mutate().filters(filters -> {
            filters.add(InstanceExchangeFilterFunctions.addHeaders(httpHeadersProvider));
            filters.add(InstanceExchangeFilterFunctions.rewriteEndpointUrl());
            filters.add(InstanceExchangeFilterFunctions.convertLegacyEndpoint(LegacyEndpointConverters.health()));
            filters.add(InstanceExchangeFilterFunctions.convertLegacyEndpoint(LegacyEndpointConverters.info()));
            filters.add(InstanceExchangeFilterFunctions.convertLegacyEndpoint(LegacyEndpointConverters.env()));
            filters.add(InstanceExchangeFilterFunctions.convertLegacyEndpoint(LegacyEndpointConverters.httptrace()));
            filters.add(InstanceExchangeFilterFunctions.convertLegacyEndpoint(LegacyEndpointConverters.threaddump()));
            filters.add(InstanceExchangeFilterFunctions.convertLegacyEndpoint(LegacyEndpointConverters.liquibase()));
            filters.add(InstanceExchangeFilterFunctions.convertLegacyEndpoint(LegacyEndpointConverters.flyway()));
        }).build();
    }

    public WebClient instance(Mono<Instance> instance) {
        return webClient.mutate()
                .filters(filters -> filters.add(0, InstanceExchangeFilterFunctions.setInstance(instance)))
                .build();
    }

    public WebClient instance(Instance instance) {
        return webClient.mutate()
                .filters(filters -> filters.add(0, InstanceExchangeFilterFunctions.setInstance(instance)))
                .build();
    }

    private static WebClient createDefaultWebClient(Duration connectTimeout,
                                                    Duration readTimeout,
                                                    WebClientCustomizer customizer) {



//配置ssl信任
        SslContext sslContext=null;
        try {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (SSLException e) {
            e.printStackTrace();
        }

        final SslContext _sslContext =sslContext;
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
                options -> options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.toMillis())
                        .compression(true)
                        .afterNettyContextInit(ctx -> {
                            ctx.addHandlerLast(
                                    new ReadTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS));
                        })
                        .sslContext(_sslContext)
        );

        WebClient.Builder builder = WebClient.builder()
                .clientConnector(connector)
                .defaultHeader(HttpHeaders.ACCEPT, ActuatorMediaType.V2_JSON,
                        ActuatorMediaType.V1_JSON, MediaType.APPLICATION_JSON_VALUE);
        customizer.customize(builder);
        return builder.build();
    }
}