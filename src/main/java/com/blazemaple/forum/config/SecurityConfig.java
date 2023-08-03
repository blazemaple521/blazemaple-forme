package com.blazemaple.forum.config;

import com.blazemaple.forum.common.ForumConstant;
import com.blazemaple.forum.util.ForumUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements ForumConstant {

    /**
     * 配置全局的某些通用事物，例如静态资源等
     */
    @Bean
    public WebSecurityCustomizer securityCustomizer(){
        return (web) -> web.ignoring().antMatchers("/resources/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers(
                "/user/setting",
                "/user/upload",
                "/discuss/add",
                "/comment/add/**",
                "/letter/**",
                "/notice/**",
                "/like",
                "/follow",
                "/unfollow"
            )
            .hasAnyAuthority(
                AUTHORITY_USER,
                AUTHORITY_ADMIN,
                AUTHORITY_MODERATOR
            )
            .antMatchers(
                "/discuss/top",
                "/discuss/wonderful"
            )
            .hasAnyAuthority(
                AUTHORITY_MODERATOR
            )
            .antMatchers(
                "/discuss/delete",
                "/data/**",
                "/actuator/**"
            )
            .hasAnyAuthority(
                AUTHORITY_ADMIN
            )
            .anyRequest().permitAll()
            .and().csrf().disable().exceptionHandling()
            .authenticationEntryPoint(new AuthenticationEntryPoint() {
                // 没有登录
                @Override
                public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    // 判断是普通请求还是异步请求，进行不同的处理
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(ForumUtil.getJSONString(403, "你还没有登录哦!"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/login");
                    }
                }
            })
            .accessDeniedHandler(new AccessDeniedHandler() {
                // 权限不足
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(ForumUtil.getJSONString(403, "你没有访问此功能的权限!"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/denied");
                    }
                }
            });
        // 为了执行自己的logout
        // Security底层默认会拦截/logout请求,进行退出处理.
        // 覆盖它默认的逻辑,才能执行我们自己的退出代码.
        // 此处为一个欺骗，程序中没有"/securitylogout"，拦截到这个路径不会处理
        http.logout().logoutUrl("/securitylogout");

        return http.build();
    }



}