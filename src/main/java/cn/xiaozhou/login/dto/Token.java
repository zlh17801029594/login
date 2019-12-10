package cn.xiaozhou.login.dto;

import java.io.Serializable;

public class Token implements Serializable {
    private String token;
    private Long loginTime;

    public Token(String token, Long loginTime) {
        this.token = token;
        this.loginTime = loginTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }
}
