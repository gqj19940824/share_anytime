package com.unity.configuration;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class CustomZuulConfig {

    @Autowired
    ZuulProperties zuulProperties;

    @Autowired
    ServerProperties server;

    @Autowired
    RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Bean
    public SimpleRouteLocator routeLocator() {

        Set<String> ignoredPatterns = this.getIgnoredPatterns();
        zuulProperties.setIgnoredPatterns(ignoredPatterns);
//        zuulProperties.getSensitiveHeaders().forEach(o->{
//            System.out.println("SensitiveHeaders========="+o);
//        });
//        zuulProperties.getSensitiveHeaders().clear();
//        zuulProperties.setIgnoreSecurityHeaders(false);

//        SimpleRouteLocator routeLocator = new SimpleRouteLocator(this.server.getServletPrefix(), this.zuulProperties);

        SimpleRouteLocator routeLocator = new SimpleRouteLocator(this.server.getServlet().getServletPrefix(), this.zuulProperties);

//        routeLocator.getRoutes().forEach(o->{
//            System.out.println("route========="+o.getPath());
//            o.getSensitiveHeaders().add("*");
//        });
        return routeLocator;
    }

    /**
     * controller 中包含的所有路径均忽略转发
     *
     * @return
     */
    private Set<String> getIgnoredPatterns() {
        Set<String> urlList = new HashSet<String>();

        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            Set<String> set = info.getPatternsCondition().getPatterns();

            set.stream().forEach(s -> {
                // 替换路径中的通配符
                s = s.replaceAll("\\{(.*?)\\}", "*");
                System.out.println(s);
                urlList.add(s);
            });

        }

        return urlList;
    }

    @Bean
    public PatternServiceRouteMapper serviceRouteMapper() {
        return new PatternServiceRouteMapper("(unity-server-|unity-business-)(?<name>.+$)", "/${name}");
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
