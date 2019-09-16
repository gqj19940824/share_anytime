package com.unity.configuration;//package com.unity.configuration;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
//import org.springframework.security.web.access.channel.ChannelProcessingFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import javax.annotation.Resource;
//import javax.servlet.Filter;
//
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityCfg extends WebSecurityConfigurerAdapter {
//    @Resource
//    private Environment env;
//
//    @Bean
//    public Filter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        config.addExposedHeader("x-auth-token");
//        config.addExposedHeader("x-total-count");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        System.out.println("HttpSecurity**********************************************Configure");
//        String contextPath = env.getProperty("server.servlet.context-path");
//        if (StringUtils.isEmpty(contextPath)) {
//            contextPath = "";
//        }
//
//        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry =
//                http.addFilterBefore(corsFilter(), ChannelProcessingFilter.class)
//                        .authorizeRequests()
//                        .and().exceptionHandling()
//                        .and().headers().frameOptions().disable()
//                        .and().csrf().disable()
//                        .anonymous()
//                        .and().authorizeRequests()
//                        .antMatchers("/**").permitAll()
//                        .antMatchers(HttpMethod.OPTIONS).permitAll();
//
//        expressionInterceptUrlRegistry
//                .and().httpBasic();
//
//    }
//}
