package cn.xiaozhou.login.config;

import cn.xiaozhou.login.dto.LoginUser;
import cn.xiaozhou.login.dto.ResponseInfo;
import cn.xiaozhou.login.dto.Token;
import cn.xiaozhou.login.filter.TokenFilter;
import cn.xiaozhou.login.service.TokenService;
import cn.xiaozhou.login.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityHandlerConfig {
    @Autowired
    private TokenService tokenService;

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                Token token = tokenService.saveToken(loginUser);
                ResponseUtil.responseJson(httpServletResponse, HttpStatus.OK.value(), token);
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                String msg = null;
                if (e instanceof BadCredentialsException) {
                    msg = "密码错误";
                } else {
                    msg = e.getMessage();
                }
                ResponseInfo info = new ResponseInfo(HttpStatus.UNAUTHORIZED.value() + "", msg);
                ResponseUtil.responseJson(httpServletResponse, HttpStatus.UNAUTHORIZED.value(), info);
            }
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                ResponseInfo info = new ResponseInfo(HttpStatus.UNAUTHORIZED.value() + "", "请先登录");
                ResponseUtil.responseJson(httpServletResponse, HttpStatus.UNAUTHORIZED.value(), info);
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                ResponseInfo info = new ResponseInfo(HttpStatus.OK.value() + "", "退出成功");
                String token = TokenFilter.getToken(httpServletRequest);
                tokenService.deleteToken(token);
                ResponseUtil.responseJson(httpServletResponse, HttpStatus.OK.value(), info);
            }
        };
    }
}
