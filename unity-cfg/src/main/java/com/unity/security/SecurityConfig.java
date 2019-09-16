package com.unity.security;

import com.unity.springboot.support.annotation.Anonymous;
import com.unity.springboot.support.configuration.UrlSecurityConfiguration;
import com.unity.springboot.support.holder.CustomAccessDeniedHandler;
import com.unity.springboot.support.holder.CustomAuthenticationEntryPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.Set;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UrlSecurityConfiguration urlSecurityConfiguration;

   @Autowired
   private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

   @Autowired
   private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * 自定义基于JWT的安全过滤器
     */
    @Autowired
    JwtAuthorizationTokenFilter authenticationTokenFilter;


    /*@Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the ROLE_ prefix
        return new GrantedAuthorityDefaults("");
    }*/

    @Bean
    public Filter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("X-AUTH-TOKEN");
        config.addExposedHeader("x-total-count");
        source.registerCorsConfiguration("", config);
        return new CorsFilter(source);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("======《SecurityConfiguration》======初始化配置=====");
        // 基于token，所以不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry
                expressionInterceptUrlRegistry =
                http.addFilterBefore(corsFilter(), ChannelProcessingFilter.class)
                        .authorizeRequests()
                        .and().csrf().disable().anonymous()
                        .and().authorizeRequests().and().exceptionHandling()
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint).and().authorizeRequests()
                        .antMatchers("/**").permitAll()
                        .antMatchers(HttpMethod.OPTIONS).permitAll();
        //expressionInterceptUrlRegistry.and().httpBasic().disable();

        requestMappingHandlerMapping
                .getHandlerMethods()
                .entrySet()
                .stream()
                .filter((entry) -> AnnotationUtils.findAnnotation(entry.getValue().getBeanType(), FeignClient.class) == null)
                .forEach(entry -> {
                    Set<String> patterns = entry.getKey()
                            .getPatternsCondition()
                            .getPatterns();
                    if( AnnotationUtils.findAnnotation(entry.getValue().getMethod(), Anonymous.class) == null){
                        patterns.forEach(pattern ->
                                expressionInterceptUrlRegistry
                                        .antMatchers(pattern.replaceAll("\\{.+}", "*"))
                                        .hasAuthority(pattern.replaceAll("\\{.+}", "*")));
                    }else{
                        //Set anonymous if contains Anonymous ANNOTATION
                        patterns.forEach(pattern ->
                                expressionInterceptUrlRegistry
                                        .antMatchers(pattern.replaceAll("\\{.+}", "*"))
                                        .permitAll());
                    }
                });

        switch (urlSecurityConfiguration.getModel()) {
            case permit:
                if (urlSecurityConfiguration.getPermit() != null && urlSecurityConfiguration.getPermit().length > 0) {
                    expressionInterceptUrlRegistry
                            .antMatchers(urlSecurityConfiguration.getPermit()).permitAll();
                }
                expressionInterceptUrlRegistry.anyRequest().authenticated();
                break;
            case authenticated:
                expressionInterceptUrlRegistry.anyRequest().permitAll();
                if (urlSecurityConfiguration.getAuthenticated() != null
                        && urlSecurityConfiguration.getAuthenticated().length > 0) {
                    expressionInterceptUrlRegistry.antMatchers(urlSecurityConfiguration.getAuthenticated())
                            .authenticated();
                }
                break;
            default:
                throw new IllegalArgumentException("Please configure new auth model!");
        }

        expressionInterceptUrlRegistry
                .antMatchers("/**").authenticated()
                .and().addFilterBefore(authenticationTokenFilter, org.springframework.security
                .web.authentication.UsernamePasswordAuthenticationFilter.class)
        ;
    }
}
