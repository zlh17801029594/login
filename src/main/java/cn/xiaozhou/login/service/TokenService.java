package cn.xiaozhou.login.service;

import cn.xiaozhou.login.dto.LoginUser;
import cn.xiaozhou.login.dto.Token;

public interface TokenService {

    Token saveToken(LoginUser loginUser);

    void refresh(LoginUser loginUser);

    LoginUser getLoginUser(String token);

    boolean deleteToken(String token);
}
