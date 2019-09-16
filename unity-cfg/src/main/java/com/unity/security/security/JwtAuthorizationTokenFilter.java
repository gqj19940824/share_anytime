package com.unity.security.security;

import com.unity.common.base.SessionHolder;
import com.unity.common.pojos.AuthUser;
import com.unity.common.pojos.MyUserDetails;
import com.unity.common.util.RedisUtils;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

    private final RedisUtils redisUtils;

    public JwtAuthorizationTokenFilter(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = SessionHolder.getTokenByRequest(request);
        log.info("======《Filter》===URL {} ===authToken {}", request.getRequestURL(), token);
        SecurityContextHolder.getContext().setAuthentication(null);
        LoginContextHolder.setLoginAttributes(null);
        try {
            AuthUser currentUser = redisUtils.getCurrentUserByToken(token);
            if (currentUser != null) {
                LoginContextHolder.setLoginAttributes(currentUser);
                Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
                if (currentUser.getAuth() != null) {
                    authorities = currentUser.getAuth().parallelStream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }
                UserDetails userDetails = new MyUserDetails(currentUser.getId(), currentUser.getLoginName(),
                        currentUser.getPwd(), true, authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                        null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.error("===== 《Filter》 获取用户信息为空 token {}", token);
            }
        } catch (Exception e) {
            log.error("====== 《Filter》 异常 Authenticator throw an Exception {}", e.toString());
            SecurityContextHolder.clearContext();
        } catch (Throwable t) {
            log.error("====== 《Filter》 异常 Authenticator throw an THROWABLE {}", t.toString());
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }
}
