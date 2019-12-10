package cn.xiaozhou.login.dto;

import java.io.Serializable;

public class ResponseInfo implements Serializable {
    private String code;
    private String message;

    public ResponseInfo(String code, String message) {
        /*构造函数里面调不调用父类构造方法super()，有很大区别吗？*/
        super();
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
